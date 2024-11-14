# 서비스 쿼리 분석 및 개선 보고서

서비스 운영이 이루어지고 데이터가 쌓여가면서, 처음 개발했던 상황과 달리 속도 지연이 발생할 수 있다.

짧은 시간동안 치솟는 트래픽 때문에 생기는 성능이슈와 다르게 DB를 읽을 때 발생하는 성능 이슈는
해당 문제가 생기지 않도록 '읽기 작업'에 대한 최적화 작업이 필요하다.

이번 보고서는 데이터 조회 쿼리 목록을 분석하고 성능 개선 방안을 검토하는 것을 목표로 한다. 

<br>

## 조회 쿼리 목록과 분석

**콘서트 예약 서비스** 시나리오에서 사용되는 조회 쿼리들은 다음과 같다.

### 공연 (Concert)

1. SELECT * FROM concert;
2. SELECT * FROM concert_schedule cs LEFT JOIN concert c ON cs.concert_id = c.concert_id WHERE cs.concert_id = {concertId} ORDER BY cs.concert_date;
3. SELECT * FROM concert_schedule cs WHERE cs.concert_id = {concertId} and concert_date = {date};
4. SELECT * FROM seat WHERE concert_schedule_id = {scheduleId} and is_occupied = 0 order by row_name, seat_no;

### 토큰 (Token)

1. SELECT FROM token_history h JOIN concert c ON h.concert_id = c.concert_id WHERE h.concert_id = {concertId} AND h.user_uuid = {uuid}

### 도메인 공통 - 예약 (Booking), 결제 (Payment), 포인트 (Point), 사용자 (User), 공연 (Concert), 토큰 (Token)

1. SELECT * FROM 도메인 WHERE id = {id};

<br>

### 쿼리 분포 현황

대부분의 도메인에서 기본키(PK, Primary Key)를 활용한 단건조회를 사용하기 때문에 빠른 조회 결과를 확인할 수 있다.
> 기본키는 기본적으로 단일 인덱스를 사용하여 쿼리 속도를 향상시키는 기능을 수행한다.

콘서트 도메인에서 WHERE절 없이 모든 데이터를 읽어오는 쿼리가 존재한다.
> 스케쥴러를 통한 대기열 토큰 상태업데이트를 위해 콘서트 목록의 ID를 전달하는 역할을 한다.
> 비즈니스 로직의 고도화(Redis의 TTL 활용 또는 콘서트 조건 추가)가 이루어지면 해당 쿼리는 사라진다.

토큰 도메인에서 토큰 발행이력을 조회하는 쿼리가 존재한다.
> 현재 토큰 발행이력을 사용하는 API가 존재하지 않는다. 서비스 고도화 이후 발행이력을 사용하게 되면
> 해당 쿼리를 분석한다.

서비스 이용을 위한 3가지 API에서 사용되는 조회 쿼리를 다음과 같이 확인할 수 있다.

i. 스케쥴 일정 목록

예약가능한 공연일정 조회 API를 호출하면 위 공연 도메인의 `2.`번 쿼리를 호출한다.

```sql
SELECT * 
FROM 
    concert_schedule cs 
LEFT JOIN 
    concert c 
ON 
    cs.concert_id = c.concert_id 
WHERE 
    cs.concert_id = 1 
ORDER BY 
    cs.concert_date;

-- [참고]: DB에서 조회한 목록에서 모든 좌석의 예약이 완료된 일자는 비즈니스로직에서 필터링한다.
```

concert_schedule 테이블의 conert_id는 concert 테이블의 기본키(concert_id)를 참조하는 외래키로 사용된다.
> 외래키 인덱스를 사용해서 WHERE절까지는 인덱스를 잘 활용하지만, ORDER BY 절의 `concert_date` 컬럼 때문에 filesort 가 예상된다. <br>
> 공연일정은 공연과 함께 추가된 이후 변경 및 삭제되는 케이스보다 조회하는 케이스가 훨씬 더 많다.
> 대부분 일정 조회를 위해 concert_schedule 테이블에 접근할 것이 예상되기 때문에 인덱스를 활용해서 불필요한 filesort를 방지하는 것이 바람직하다.


ii. 예약가능한 좌석 목록 

예약가능한 좌석목록 조회 API 호출 시 위 공연 도메인의 `4.`번 쿼리를 호출한다.

```sql
SELECT * 
FROM 
    seat
WHERE
    concert_schedule_id = 1
AND 
    is_occupied = 0;
ORDER BY
    row_name, seat_no
```

seat 테이블의 concert_schedule_id는 concert_schedule 테이블의 기본키(concert_schedule_id)를 참조하는 외래키로 사용된다.
> 해당 쿼리 역시 WHERE절까지 외래키 인덱스를 잘 활용하지만 ORDER BY 절 때문에 filesort가 예상된다. <br>
> 좌석은 '예약'과 '예약 결제' 비즈니스 로직때문에 데이터(record)에 대한 변경이 공연 일정보다 잦다.
> INDEX 추가시 SELECT 외 다른 작업에 대한 성능저하가 발생하기 때문에 조회와 처리 성능간의 트레이드 오프(trade-off)를
> 확인해보고 상황에 맞추어 INDEX를 추가하는 것이 바람직하다.


iii. 좌석 예약

좌석 예약 API 호출 시 위 공연 도메인의 `3.`번 쿼리를 호출한다.

```sql
SELECT * FROM concert_schedule cs WHERE cs.concert_id = 1 and concert_date = '2024-11-01';
```

에약을 위해 특정 스케쥴을 조회하기 위한 쿼리이다. 외래키 인덱스를 사용해서 데이터에 접근하고 1건의 레코드를 가져온다.
> 외래키 인덱스를 활용하여 DB에 접근 후 정상적으로 데이터를 가져오는 프로세스가 예상된다.

<br>


## 인덱스 적용 및 결과 확인

'스케쥴 일정 목록'과 '예약가능한 좌석 목록' 쿼리에 인덱스를 적용하는 테스트를 진행한다.
- 이전 분석을 통해서 두 쿼리에 인덱스를 적용하면 성능이 향상될 것으로 기대되었다.
- 기대대로 성능 개선이 이루어지는지 인덱스 적용 전/후의 실행계획과 쿼리 수행결과를 비교한다.
- 테스트를 위한 데이터 설정은 다음과 같다.
  - 전체 콘서트 : 35개
  - 전체 콘서트 스케쥴 : 235개 (콘서트당 스케쥴을 4 ~ 10개로 설정하여 콘서트 스케쥴 생성)
  - 전체 콘서트 좌석 : 11750개 (콘서트 스케쥴별로 50개 할당)

<br>

**스케쥴 일정 목록**

```sql
-- SQL
SELECT * 
FROM 
    concert_schedule cs
LEFT JOIN 
    concert c
ON 
    cs.concert_id = c.concert_id
WHERE
    cs.concert_id = 1
ORDER BY
    cs.concert_date;

```

인덱스 적용 전

- 실행계획

| select_type | table | type   | possible_keys                  | key                            | key_len | ref    | rows | Extra                       |
|-------------|-------|--------|--------------------------------|--------------------------------|---------|--------|------|-----------------------------|
| SIMPLE      | cs    | ref    | FK_concert_schedule_concert_id | FK_concert_schedule_concert_id | 9       | const  | 8    | Using where; Using filesort |
| SIMPLE      | c     | const  | PRIMARY                        | PRIMARY                        | 8       | const  | 1    |                             |

- 쿼리 결과
```text
8 rows retrieved starting from 1 in 57 ms (execution: 8 ms, fetching: 49 ms)
8 rows retrieved starting from 1 in 67 ms (execution: 9 ms, fetching: 58 ms)
8 rows retrieved starting from 1 in 66 ms (execution: 10 ms, fetching: 56 ms)
8 rows retrieved starting from 1 in 63 ms (execution: 12 ms, fetching: 51 ms)
8 rows retrieved starting from 1 in 69 ms (execution: 9 ms, fetching: 60 ms)
8 rows retrieved starting from 1 in 68 ms (execution: 13 ms, fetching: 55 ms)
```

<br>

인덱스 적용 후
```sql
-- INDEX
CREATE INDEX `idx_concert_schedule_concert_id_concert_date` ON `concert_schedule`(`concert_id`, `concert_date`);
```

- 실행계획

| select_type | table | type   | possible_keys                                | key                                          | key_len | ref    | rows | Extra       |
|-------------|-------|--------|----------------------------------------------|----------------------------------------------|---------|--------|------|-------------|
| SIMPLE      | cs    | ref    | idx_concert_schedule_concert_id_concert_date | idx_concert_schedule_concert_id_concert_date | 9       | const  | 8    | Using where |
| SIMPLE      | c     | const  | PRIMARY                                      | PRIMARY                                      | 8       | const  | 1    |             |


- 쿼리 결과
```text
8 rows retrieved starting from 1 in 53 ms (execution: 6 ms, fetching: 47 ms)
8 rows retrieved starting from 1 in 34 ms (execution: 5 ms, fetching: 29 ms)
8 rows retrieved starting from 1 in 49 ms (execution: 8 ms, fetching: 41 ms)
8 rows retrieved starting from 1 in 99 ms (execution: 11 ms, fetching: 88 ms)
8 rows retrieved starting from 1 in 49 ms (execution: 5 ms, fetching: 44 ms)
8 rows retrieved starting from 1 in 41 ms (execution: 7 ms, fetching: 34 ms)
```

<br>

---

<br>

**예약 가능한 좌석 목록**

```sql
-- SQL
SELECT *
FROM
    seat
WHERE
    concert_schedule_id = 1
AND
    is_occupied = 0
ORDER BY
    row_name, seat_no;
```

인덱스 적용 전
- 실행계획

| select_type | table | type | possible_keys                | key                          | key_len | ref    | rows | Extra                        |
|-------------|-------|------|------------------------------|------------------------------|---------|--------|------|------------------------------|
| SIMPLE      | seat  | ref  | FKjtsoupodivwr6xa4vk02e2qol  | FKjtsoupodivwr6xa4vk02e2qol  | 9       | const  | 50   | Using where; Using filesort  | 

- 쿼리 결과
```text
50 rows retrieved starting from 1 in 153 ms (execution: 17 ms, fetching: 136 ms)
50 rows retrieved starting from 1 in 62 ms (execution: 9 ms, fetching: 53 ms)
50 rows retrieved starting from 1 in 67 ms (execution: 9 ms, fetching: 58 ms)
50 rows retrieved starting from 1 in 110 ms (execution: 72 ms, fetching: 38 ms)
50 rows retrieved starting from 1 in 75 ms (execution: 8 ms, fetching: 67 ms)
50 rows retrieved starting from 1 in 77 ms (execution: 9 ms, fetching: 68 ms)
```

<br>

인덱스 적용 후

```sql
CREATE INDEX `idx_seat_schedule_id_row_no` ON `seat` (`concert_schedule_id`, `row_name`, `seat_no`);
```

- 실행계획

| select_type | table | type | possible_keys                | key                          | key_len | ref    | rows | Extra        |
|-------------|-------|------|------------------------------|------------------------------|---------|--------|------|--------------|
| SIMPLE      | seat  | ref  | idx_seat_schedule_id_row_no  | idx_seat_schedule_id_row_no  | 9       | const  | 50   | Using where  |

- 쿼리결과
```text
50 rows retrieved starting from 1 in 52 ms (execution: 6 ms, fetching: 46 ms)
50 rows retrieved starting from 1 in 46 ms (execution: 3 ms, fetching: 43 ms)
50 rows retrieved starting from 1 in 55 ms (execution: 6 ms, fetching: 49 ms)
50 rows retrieved starting from 1 in 42 ms (execution: 6 ms, fetching: 36 ms)
50 rows retrieved starting from 1 in 37 ms (execution: 7 ms, fetching: 30 ms)
50 rows retrieved starting from 1 in 52 ms (execution: 5 ms, fetching: 47 ms)
```

<br>

---

<br>

## Summary

concert_schedule 테이블과 seat 테이블에 Index를 적용하고 쿼리 속도 개선이 확인된다.
- '스케쥴 일정 목록' 조회 쿼리 수행 속도 평균값* 변화 : 10.17 ms -> 7 ms
- '예약 가능한 좌석 목록' 조회 쿼리 수행 속도 평균값 변화 : 20.7 ms ->  5.5 ms

＊ 평균값은 실제 쿼리 수행(execution)시간을 기준으로 계산된다.

<br>