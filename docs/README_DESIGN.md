## 프로젝트 설계

'**콘서트 예약 서비스**'의 요구사항을 토대로 프로젝트의 구조 및 API 명세를 설계한다.

<br>

### introduction 

- 기술스택
```text
- Application
  spring boot (v 3.3.4)
  spring web
  spring security
  spring data jpa
  querydsl (설정 예정)
  
- DB
  H2 
```
<br>

- 패키지 구조 <br>
```text
application/
  ㄴ 도메인/
domain/
  ㄴ 도메인/
infra/
  ㄴ db/
    ㄴ 도메인/
interfaces/
  ㄴ api/
    ㄴ 도메인/
config/
```
계층 구조(Layered Architecture)를 기반으로 구현하지만, 비즈니스로직을 보호하기 위해 Repository를 추상화하여 사용한다. <br>
비즈니스 로직이 DB와 실제 상호작용하는 부분을 모르게 처리함으로서 application이 수행해야할 기능에 집중하도록 한다.

<br>

### ERD 설계
<img src="https://github.com/user-attachments/assets/c9e412f0-4a54-436c-a3a9-5054b865400e" width="700">

- 사용자(users): 이름과 고유 식별값인 UUID를 갖는다.
- 포인트(user_point): 일대일 연관관계를 맺는 사용자 ID와 결제시 사용하는 사용자의 point를 갖는다.
- 공연(concert): 속성 정보인 공연자, 장소, 타이틀을 갖는다.
- 공연 일정(concert_schedule): 다대일 관계를 맺는 공연의 ID, 공연일정(concert_date), 판매 수(sold_count), 공연의 가능 좌석 수를 갖는다.
- 좌석(seat): 다대일 관계를 맺는 공연 일정의 ID, 위치정보(rowName, seatNo), 점유정보(is_occupied)를 갖는다.
- 예약(booking): 다대일 연관관계를 갖는 사용자와 좌석의 ID, 예약상태 (status), 예약일시와 예약 수정일시를 갖는다.
  이때, 예약상태 값으로 'RESERVED', 'PAID', 'CANCELLED'를 갖는다.
- 결제(payment): 다대일 관계를 갖는 사용자와 일대일 관계를 맺는 예약의 ID, 결제시점(paid_at), 결제금액(amount)을 갖는다.
- 토큰 대기열(token_queue): 사용자가 서비스를 이용할 때 필요한 토큰을 생성하기 위해 사용하는 대기열 테이블이다.
    - 콘서트 ID, 사용자 ID, 대기열 순위(position), 대기열 상태, 대기열 생성 / 만료 일시 정보를 담는다.

※ '결제'가 이루어지는 경우 예약(booking)의 상태를 변경한다.


<br><br>

### API 명세

#### 유저 토큰 발급 API
<details>
 <summary><code>POST</code> <code><b>/v1/token/generation</b></code> <code>(유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 )가 담긴 토큰 발급)</code></summary>

##### Parameters

| name   |  type     | data type | description |
|--------|-----------|-----------|-------------|
| userId |  required | Long      | N/A         |

##### Responses
| http code | content-type           | response                                                                                                                                                                        |
|-----------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `200`     | `application/json`     | `{"jwt": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"}` |
| `400`     | `application/json`     | `{"code": "400","message":"Bad Request"}`                                                                                                                                       |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}`                                                                                                                                     |

##### Example cURL

```html
  curl -X POST -H "Content-Type: application/json" -d '{"userId": 12345}' http://localhost:8080/
```

</details>

<br>

#### 대기번호 조회 API
<details>
 <summary><code>GET</code> <code><b>/v1/token/check</b></code> <code>(토큰 대기열 대기순서)</code></summary>

##### Headers
| Key           | value                                                                                                                                                               |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  |

##### Parameters

| name | type | data type | description |
|------|------|-----------|-------------|
| -    | -    | -         | -           |

##### Responses
| http code | content-type           | response                                    |
|-----------|------------------------|---------------------------------------------|
| `200`     | `application/json`     | `{"queuePosition": 1`                       |
| `403`     | `application/json`     | `{"code": "403","message":"Forbidden"}`     |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}` |

##### Example cURL

```html
  curl -X GET \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
       -d '{"userId": 12345}' \
       http://localhost:8080/
```

</details>

<br>


#### 예약 가능 날짜 API
<details>
 <summary><code>GET</code> <code><b>/v1/concerts/{id}/available-dates</b></code> <code>(예약 가능한 콘서트 날짜 목록)</code></summary>

##### Headers
| Key           | value                                                                                                                                                               |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  |

##### Parameters

| name  | type      | data type | description |
|-------|-----------|-----------|-------------|
| id    | required  | Long      | 콘서트 Id      |

##### Responses
| http code | content-type           | response                                                                                         |
|-----------|------------------------|--------------------------------------------------------------------------------------------------|
| `200`     | `application/json`     | `{"concertId": 1,"concertName": "Cold Play", "dates": [{"date": "2024-10-11", "capacity": 50}]}` |
| `403`     | `application/json`     | `{"code": "403","message":"Forbidden"}`                                                          |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}`                                                      |

##### Example cURL

```html
  curl -X GET \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
       http://localhost:8080/v1/concerts/1/available-dates
```

</details>
<br>

#### 예약 가능 좌석 API
<details>
 <summary><code>GET</code> <code><b>/v1/concerts/{id}/dates/{date}/available-seats</b></code> <code>(예약 가능한 좌석 목록)</code></summary>

##### Headers
| Key           | value                                                                                                                                                               |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  |

##### Parameters

| name | type      | data type | description   |
|------|-----------|-----------|---------------|
| id   | required  | Long      | 콘서트 Id        |
| date | required  | String    | 날짜 (yyyyMMdd) |

##### Responses
| http code | content-type           | response                                                                                                               |
|-----------|------------------------|------------------------------------------------------------------------------------------------------------------------|
| `200`     | `application/json`     | `{"concertId": 1,"concertName": "Cold Play", "date": "2024-10-11", "seats": [{"id": 1, "rowName": "A", "seatNo": 1}]}` |
| `403`     | `application/json`     | `{"code": "403","message":"Forbidden"}`                                                                                |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}`                                                                            |

##### Example cURL

```html
  curl -X GET \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
       http://localhost:8080/v1/concerts/1/dates/20241011/available-seats
```

</details>
<br>

#### 좌석 예약 요청 API
<details>
 <summary><code>POST</code> <code><b>/v1/concerts/{id}/dates/{date}/booking</b></code> <code>(좌석 예약 요청)</code></summary>

##### Headers
| Key           | value                                                                                                                                                               |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  |

##### Parameters

| name   | type      | data type | description   |
|--------|-----------|-----------|---------------|
| id     | required  | Long      | 콘서트 ID        |
| date   | required  | String    | 날짜 (yyyyMMdd) |
| seatId | required  | Long      | 좌석 ID         |

##### Responses
| http code | content-type           | response                                                                                                                             |
|-----------|------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `200`     | `application/json`     | `{"bookingId": 1, "concertId": 1, "date": "2024-10-11", "seatId": 1, "status": "RESERVED", "expirationTime": "2024-10-11 22:00:00"}` |
| `403`     | `application/json`     | `{"code": "403","message":"Forbidden"}`                                                                                              |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}`                                                                                          |

##### Example cURL

```html
  curl -X POST \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
       -d '1' \
       http://localhost:8080/v1/concerts/1/dates/20241011/booking
```

</details>
<br>

#### 잔액 충전 API
<details>
 <summary><code>PATCH</code> <code><b>/v1/point/{id}/charge</b></code> <code>(잔액 충전)</code></summary>

##### Headers
| Key           | value                                                                                                                                                               |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  |

##### Parameters

| name   | type      | data type | description |
|--------|-----------|-----------|-------------|
| id     | required  | Long      | 사용자 ID      |
| amount | required  | Long      | 충전할 양       |

##### Responses
| http code | content-type           | response                                                             |
|-----------|------------------------|----------------------------------------------------------------------|
| `200`     | `application/json`     | `{"userId": 1, "point": 1000, "updateTime": "2024-10-11 11:20:33"}`  |
| `403`     | `application/json`     | `{"code": "403","message":"Forbidden"}`                              |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}`                          |

##### Example cURL

```html
  curl -X PATCH \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
       -d '1000' \
       http://localhost:8080/v1/point/1/charge
```

</details>
<br>

#### 잔액 조회 API
<details>
 <summary><code>GET</code> <code><b>/v1/point/{id}</b></code> <code>(잔액 조회)</code></summary>

##### Headers
| Key           | value                                                                                                                                                               |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  |

##### Parameters

| name   | type      | data type | description |
|--------|-----------|-----------|-------------|
| id     | required  | Long      | 사용자 ID      |

##### Responses
| http code | content-type           | response                                                             |
|-----------|------------------------|----------------------------------------------------------------------|
| `200`     | `application/json`     | `{"userId": 1, "point": 1000, "updateTime": "2024-10-11 11:20:33"}`  |
| `403`     | `application/json`     | `{"code": "403","message":"Forbidden"}`                              |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}`                          |

##### Example cURL

```html
  curl -X PATCH \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
       http://localhost:8080/v1/point/1
```

</details>
<br>

#### 결제 API
<details>
 <summary><code>POST</code> <code><b>/v1/bookings/{id}/payments</b></code> <code>(결제)</code></summary>

##### Headers
| Key           | value                                                                                                                                                               |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  |

##### Parameters

| name   | type      | data type | description |
|--------|-----------|-----------|-------------|
| id     | required  | Long      | 사용자 ID      |

##### Responses
| http code | content-type           | response                                                  |
|-----------|------------------------|-----------------------------------------------------------|
| `200`     | `application/json`     | `{"amount": 1000, "paymentsTime": "2024-10-11 11:20:33"}` |
| `403`     | `application/json`     | `{"code": "403","message":"Forbidden"}`                   |
| `500`     | `application/json`     | `{"code": "500","message": "SERVER ERROR"}`               |

##### Example cURL

```html
  curl -X POST \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
       http://localhost:8080/v1/bookings/1/payments
```

</details>
<br>

<br><br>