# 부하 테스트 (Load Test) 계획 보고서

```
- 부하 테스트 대상 선정 및 목적, 시나리오 등의 계획을 세우고 이를 문서로 작성
- 적합한 테스트 스크립트를 작성하고 수행
```

콘서트 예약 서비스의 부하 테스트를 수행하고 결과를 문서로 작성한다.

## 테스트 목표

서비스 운영 시 예상되는 부하를 미리 테스트하여 잠재적인 문제를 사전에 파악하고 대비하기 위해 부하테스트를 진행한다.

## 테스트 시나리오

테스트 시나리오 작성에 앞서 서비스의 API 를 살펴보면 다음과 같다.

- Token
    - POST /v1/token/generation : 토큰 발급 (Access / Waiting)
    - GET /v1/token/check : 대기상태 조회 (대기번호 or Access 토큰)
- Concert
    - GET /v1/concerts/{concertId}/available-dates : 공연 일자 조회
    - GET /v1/concerts/{concertId}/available-dates?date={date} : 공연 일자별 예약 가능한 좌석 조회
    - POST /v1/concerts/{concertId}/booking : 공연 예약
- Booking
    - POST /v1/bookings/{bookingId}/payment : 결제
- Point
    - PATCH /v1/point/{userId}/charge : 포인트 충전
    - GET /v1/point/{userId} : 포인트 조회

위 API 를 기반으로 사용자가 공연 예약 및 결제를 진행하는 흐름은 다음과 같다.
1. 토큰 발급
2. 공연 일자 조회 (Access 토큰을 발급받은 경우 조회 가능)
3. 공연 일자별 예약 가능한 좌석 조회
4. 공연 예약
5. 결제

단순히 생각해봤을 때 서비스 운영시 발생하는 트래픽은 단일 API 호출보다는 위 흐름을 따라 여러 API 를 호출하는 경우가 많을 것이다. 

이러한 흐름을 하나의 테스트 시나리오로 정의하고 이를 기반으로 부하 테스트 환경을 구성한다.

```text
원활한 테스트를 위해 사용자는 충분한 포인트를 미리 충전해두었다고 가정하며, 
테스트 중 별도의 포인트 충전 API 호출은 없는 것으로 한다.
```

### 테스트 환경 구성

부하 테스트 진행을 위한 테스트 환경은 다음과 같다.

- 테스트 도구: K6*
- 테스트 대상: 콘서트 예약 서비스
- 테스트 시나리오: 공연 예약 및 결제 (토큰 발급부터 진행)
- 가정사항
  - 공연 정보
    - 수용인원*: 20,000명 (좌석 수)
    - 허용 사용자 수: 5,000명 (Access Token)
      - 대기열에 5,000명 이상의 사용자가 있을 경우, 추가로 진입하는 사용자에게는 Waiting Token 이 발급된다.
    - 예매 방식: 일자별 분리 예매
      - 공연 일정길이에 영향을 받지 않는다. ex) 1일차 공연: 오전 10시 예매 시작, 2일차 공연: 오후 2시 예매 시작
  - 사용자 수: 30,000명 (수용 인원의 1.5배)
  - 예매 후 결제까지 완료하는 사용자 비율은 90%로 가정
  - 대기열 진입 후 예매 완료까지 소요 시간: 3 ~ 5분
  - 예매 후 결제 완료까지 소요 시간: 5분 이내*
  - 결제 시 사용하는 포인트는 충분하다고 가정
- 변수 설정
  - user_id: 1 ~ 30,000
  - seat_id: 1 ~ 20,000
  - concert_id: 1 (지정)
  - concert_schedule_id: 1 (지정)

[비고] 테스트 환경에대한 자세한 설정은 테스트 수행 스크립트를 참고한다.

[K6 테스트 수행 스크립트](../k6/scripts/load-test.js)

```text
*: K6는 오픈소스로 제공되는 부하 테스트 도구로, 다양한 테스트 시나리오를 작성하고 테스트 결과를 분석할 수 있다.

*: 인터파크 티켓에서 콘서트 월간 랭킹을 확인하면 아래와 같은 공연장의 수용인원을 확인할 수 있다.

- 고척스카이돔 (16744)
- KSPO DOME (15000)
- 올림픽공원 핸드볼경기장 (6500)
- 고양종합운동장 주경기장 (41311)
- 잠실학생체육관 (5400)

위 공연장의 평균 수용인원은 약 16,991명이며 계산의 편의를 위해 테스트 시 수용인원은 20,000명으로 가정한다.

*: 예약 후 결제까지 소요 시간은 5분 이내로 가정하며, 이 시간을 초과하는 경우 등록된 스케쥴러에 의해 예약이 취소된다. 
```

## 테스트 결과

다음은 부하 테스트 결과를 나타내는 그래프와 표이다.

- K6 test results <br>
  ![image](https://github.com/user-attachments/assets/52b68b56-a40f-482e-9bde-dce06f235894) <br>

- Virtual Users: 30,000명 <br>
  ![image](https://github.com/user-attachments/assets/0f0edc5b-1081-43e5-8c52-9e06948714e3) <br>

- HTTP request 응답 메트릭 <br>
  ![image](https://github.com/user-attachments/assets/7d0100cc-0a52-4f25-b9af-ca3a7ed7c413) <br>

- HTTP request 차단 메트릭 <br>
  ![image](https://github.com/user-attachments/assets/0ff4a467-3ae9-49d1-8ace-1680b0501cd6) <br>



## 비고

응답값이 대부분 낮게 유지되어 양호해 보일 수 있으나 http_req_duration 의 최대값이 30초가 되고 일부 요청들이 누락되는 이유의 확인이 필요하다.