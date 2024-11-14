## 시나리오 분석

'**콘서트 예약 서비스**'의 요구사항 분석하고 필요한 기능을 설계한다.

<br>

### 비즈니스 요구사항
- 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있어야한다.
- 사용자는 좌석예약 시에 미리 충전한 잔액을 이용한다.
- 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 한다.
- 좌석예약 정보를 데이터 플랫폼에 전달하거나 이력 데이터(외부 API 호출, 메세지 발행 등)를 저장한다. (+ 8주차 추가)

<br>

### 요구사항 분석

**기능목록**
- 인증
    - 서비스 이용을 위한 사용자 접근 토큰 발급
- 콘서트
    - (콘서트의) 예약가능한 일정 조회
    - (특정 날짜의) 예약가능한 좌석 조회
- 예약
    - 콘서트 좌석 예약
- 포인트
    - 포인트 충전
    - 포인트 조회
- 결제
    - 예약 결제

(+ 사용자)

---

`콘서트 예약 서비스`의 요구사항(Requirements)에서 구현을 요구하는 5가지 API는
위의 '기능목록'으로 나타낼 수 있다. <br>
기능 목록은 이용자의 처리를 담당하는 사용자 도메인을 추가하여 크게 6개의 도메인으로 나누어 지며 <br>
이들의 협력관계를 통해 기능이 구현된다.

서비스를 구현하는데 있어 주의를 기울여야하는 부분은 `대기열`이다. <br>
해당 서비스는 대기열이 존재하며 대기열을 통과한 사람들만 예약관련 서비스를 이용할 수 있다.

<br>

**1. 유저 토큰 발급 API**

![image](https://github.com/user-attachments/assets/09bc94c9-391f-4664-9f87-38b80c89c6a8)

- 1-1: userId와 concertId는 양수만 가능하다.
- 3-1: 사용자 혹은 공연은 존재해야한다.

<br>

**2. 예약 가능 날짜 / 좌석 API**

![image](https://github.com/user-attachments/assets/2495bb3c-8072-4eba-a624-17278c56f235)

- 1-1: 해당 API는 대기열 검증을 통과한 사용자만 허락된다.
- 2-1: concertId는 양수만 가능하다.

![image](https://github.com/user-attachments/assets/badbfc8b-48a9-4c3d-80ea-d325a9ab7e5d)

- 1-1: 해당 API는 대기열 검증을 통과한 사용자만 허락된다.
- 2-1: concertId는 양수만 가능하고 date는 null이 아닌 'yyyyMMdd' 패턴의 문자열만 허락된다.

<br>

**3. 좌석 예약 요청 API**

![image](https://github.com/user-attachments/assets/745f7a64-077c-4613-b63e-7674b7816205)

- 1-1: 해당 API는 대기열 검증을 통과한 사용자만 허락된다.
- 2-1: concertId와 seatId는 양수만 가능하고 date는 null아닌 'yyyyMMdd' 패턴의 문자열만 허락된다.

<br>

**4. 잔액 충전 / 조회 API**

![image](https://github.com/user-attachments/assets/3422c4f1-df23-4f8c-b8de-3bf0fa7a6169)

- 1-1: 해당 API는 대기열 검증을 통과한 사용자만 허락된다.
- 2-1: userId와 amount는 양수만 가능하다.

![image](https://github.com/user-attachments/assets/a390b923-f14f-4220-8f6f-336d9d33fbbc)

- 1-1: 해당 API는 대기열 검증을 통과한 사용자만 허락된다.
- 2-1: userId는 양수만 가능하다.

<br>

**5. 결제 API**

![image](https://github.com/user-attachments/assets/113baf4b-cba9-47e0-ac84-b030af21a76e)

- 1-1: 해당 API는 대기열 검증을 통과한 사용자만 허락된다.
- 2-1: bookingId 양수만 가능하다.