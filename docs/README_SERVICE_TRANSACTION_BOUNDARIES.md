# 서비스 트랜잭션 범위 분석 및 확장 전략

서비스가 제공하는 기능은 UseCase별로 트랜잭션 범위가 다 다르다.

기능별로 다른 트랜잭션 범위는 서비스가 확장될 때 예상하지 못한 문제를 발생시킬 수 있다.
따라서 해당 보고서는 UseCase에 따른 트랜잭션 범위를 이해하고 서비스가 확장 전략 수립을 목표로한다.

<br>

## 기능별 트랜잭션 범위

### 예약가능한 공연 일정 조회 case - ConcertFacade

- 단일 도메인 서비스를 사용해서 콘서트 일정을 제공한다.

### 예약가능한 좌석 조회 case - ConcertFacade

- 단일 도메인 서비스를 사용해서 콘서트 일정을 확인하고 좌석정보를 제공한다.

### 콘서트 예약 case - ConcertBookingFacade

- 두 개 이상의 도메인 서비스를 복합적으로 사용한다.
- UseCase의 트랜잭션 범위는 여러 서비스 작업들이 모여 구성된 하나의 논리적 작업 단위이다.
- UseCase 처리를 위한 작업 순서는 다음과 같다.
  1. 사용자 도메인 서비스를 사용해서 사용자 정보를 조회한다.
  2. 콘서트 도메인 서비스를 사용해서 요청한 콘서트 일정을 조회한다.
  3. 예약 도메인 서비스를 사용해서 좌석을 조회하고 예약 처리를 한다. (DistributedLock - simple)
  4. 예약 도메인의 스케쥴러를 사용해서 정해진 시간동안 결제가 안이루어지면 취소처리하도록 한다.
  5. 콘서트 도메인 서비스를 사용해서 해당 공연일자의 예약 가능 좌석 수를 수정한다.

### 포인트 충전 / 조회 case - PointFacade

- 단일 도메인 서비스를 사용해서 포인트를 충전하거나 포인트를 조회한다.

### 결제 case - BookingPaymentFacade

- 두 개 이상의 도메인 서비스를 복합적으로 사용한다.
- UseCase의 트랜잭션 범위는 여러 서비스 작업들이 모여 구성된 하나의 논리적 작업 단위이다. 
- UseCase 처리를 위한 작업 순서는 다음과 같다.
  1. 예약 도메인 서비스를 사용해서 예약 건을 결제상태로 변경한다.
  2. 사용자 도메인 서비스를 사용해서 결제를 한 사용자를 조회한다.
  3. 포인트 도메인 서비스를 사용해서 해당하는 사용자의 포인트를 차감한다.
  4. 포인트 도메인 서비스를 사용해서 포인트 차감 이력을 저장한다.
  5. 토큰 도메인 서비스를 사용해서 액세스 토큰의 권한을 만료시킨다.

<br>

## 서비스 분리 전략

트랜잭션 범위는 서비스가 제공하는 기능(UseCase)에 대한 논리적 범위와 동일하다.

논리적 범위라는 관점에서 트랜잭션은 다음의 특성을 갖는다.
- 도메인 영역과 밀접한 관련이 있다.
- 도메인 간의 협력으로 구성되더라도 각 도메인 영역은 독립적일 수 있다.

단일 도메인 영역과 연결되는 트랜잭션과 복합적인 도메인 영역과 연결되는 트랜잭션 처리의 복잡도는 다르다.

서비스 규모가 작을 때는 트랜잭션 범위가 길고 처리 복잡도가 높더라도 문제가 되지 않으나, 서비스 사용자 규모가 달라지고
트래픽 및 사용 데이터가 증가하면 서버에 부하를 발생시켜 예상하지 못한 이슈가 발생할 수 있다.

현재 구조에서 발생할 수 있는 문제를 방지하기 위해 서비스 규모가 확장되어 서비스를 분리한다고 가정한다.

<br>

### 도메인 별 분리

기능별 트랜잭션 범위를 분석할 때 확인한 것처럼 서비스는 도메인의 협력으로 구성되어있다. 따라서 하나의 거대 서비스를
각각의 도메인 서비스 간의 협력으로 분리할 수 있다.

도메인들이 독립적인 서비스로 분리될 경우 스프링에서 제공하는 트랜잭션 관리 기능을 쓸 수 없다는 단점이 존재한다.

```java
@Transactional
public ConcertBookingResult.BookingSeat bookConcertSeat(ConcertBookingCommand.BookingSeat booking) {
    User user = userService.getUserByUUID(booking.userUUID());
    ConcertSchedule concertSchedule = concertService.getConcertSchedule(booking.concertId(), booking.date());

    Booking bookingResult = bookingService.createBooking(concertSchedule, booking.seatId(), user);
    bookingScheduler.addSchedule(bookingResult.getId(), DEFAULT_BOOKING_STAGING_MIN);
    concertService.updateScheduleStatus(booking.concertId(), booking.date());
    ...
}
```

단일 서비스는 위처럼 @Transactional 어노테이션만 추가하면 별도의 작업없이 예외처리를 할 수 있다. 하지만 도메인별로 
서비스를 분리하면 이는 불가능하다.

<br>

### 보상 트랜잭션 - Application Event

도메인 서비스간의 협력으로 서비스를 구성하는 것처럼 협력을 통해 트랜잭션 문제를 해결한다.

도메인을 기준으로 분리된 이후 UseCase를 제공할 때 각 서비스를 각각 작업 후 트랜잭션을 커밋한다. 선행작업의 커밋 이후
후행 작업에서 실패가 발생하면 이전에 완료된 작업을 취소시켜 논리적 트랜잭션의 원자성을 준수하는데 이를
'보상 트랜잭션'이라 한다.

도메인 서비스에서 작업을 실패하면 롤백처리해야하는 작업을 요청하는 Event를 발행하고 다른 도메인 서비스에게 작업을 요청한다.

'좌석 예약'과 '결제'의 UseCase로 살펴보면 다음과 같다. (편의상 조회 작업제외) 

```text
- 좌석예약
1. 예약도메인 서비스
   좌석 예약 처리 완료 - DB commit
   예약관련 스케쥴러 등록 - memory

2. 콘서트 도메인 서비스
   콘서트 일정의 예약가능한 좌석 수 수정 - UnExpected Exception
   ConcertScheduleAvailableSeatFailEvent 발생 및 발행
   ConcertScheduleAvailableSeatFailEventListener로 예약 서비스에 예약처리 롤백 및 스케쥴러 제거 요청
```

---

```text
- 결제
i) 포인트 차감에 실패할 경우
1. 예약 도메인 서비스
   예약 상태를 결제 상태로 변경 - DB commit

2. 포인트 도메인 서비스
   사용자 포인트 차감 - UnExpected Exception
   UserPointUpdateFailEvent 발생 및 발행
   UserPointUpdateFailEventListener로 예약 도메인 서비스에 상태변경 작업의 롤백 요청

ii) 액세스 토큰 권한 만료에 실패할 경우
1. 예약 도메인 서비스
   예약 상태를 결제 상태로 변경 - DB commit

2. 포인트 도메인 서비스
   사용자 포인트 차감 - DB commit
   
3. 토큰 도메인 서비스
   액세스 토큰 권한 만료 - UnExpected Exception
   ExpireTokenFailEvent 발생 및 발행
   ExpireTokenFailEventListener로 포인트 서비스와 예약 서비스에게 작업의 롤백을 요청한다.

* 포인트 차감 이력을 저장하는 기능은 관심사 분리를 통해 메인 비즈니스 로직과 분리시킨다.
```

<br>

