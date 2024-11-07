# Redis 기반 비즈니스 로직 성능 개선 보고서

## 서론

현재 서비스는 운영을 위해서 데이터베이스(이하 DB)와 긴밀히 연결되어 있다. <br>
API 응답을 위한 정보뿐만 아니라 사용자 토큰 역시 DB로 관리된다. <br>
사용자 토큰(대기열 / 액세스) 정보를 위한 DB 접근은 불필요한 I/O와 서버 부하를 가져올 수 있다. <br>
일반적으로 서비스 애플리케이션과 함께 사용자 인증정보를 다루기 위해 사용되는 Redis는 <br>
메모리상에서 데이터를 저장하고 다루기 때문에 빠른 처리속도를 자랑함과 동시에 DB에 대한 부하를 낮추는 역할을 한다. <br> 
본 문서는 Redis에 대해 간략히 살핀 후 기존 로직에 적용하면 어떻게 될 지 이를 살핀다.

<br><br>

## Redis 자료구조 타입
Redis를 이용해 대기열을 생성할 때 일반적으로 두가지 자료형을 활용할 수 있다. <br>
'Lists'와 'Sorted Sets'이 해당하는 자료형이며 구현하고자하는 대기열의 성격에 따라 <br>
사용하도록 한다.

\* 그 외 자료형으로 Redis Streams 또한 대기열 구현에 어울린다고 생각할 수 있으나, <br>
   Stream은 메시지 기반 구조로 유효성이 사라지면 토큰을 제거해야하는 이번 과제의 <br>
   대기열과 어울리지 않는다.

<br>

### Lists
가장 기본적인 형태의 대기열을 구현하는테 사용하기 적합한 자료형이다. 자바의 LinkedList와 <br>
유사한 형태를 지닌다. 이 덕분에 자연스럽게 FIFO(First-In & First-Out) 구조를 제공하고 <br>
내부 요소의 추가 / 삭제 시 빠른 속도로 처리가 가능하다.

<br>

### Sorted Sets
우선순위가 필요한 대기열을 구현하는데 적합한 자료형이다. 단순히 먼저 넣은 데이터가 상위 인덱스를 <br>
(낮은 숫자) 갖는 Lists와 달리 내부에 데이터 저장 시 적용할 우선순위(score)를 지정할 수 있다. <br>
우선순위를 보장하는 것과 더불어 가장 큰 특징 중 하나는 저장된 데이터(member)의 위치(순위)를 <br>
바로 확인할 수 있는 기능(rank)을 제공한다. 이러한 특징들 덕분에 Sorted Sets은 <br>
주로 지연 작업 스케쥴링이나 점수 및 순위 기반 처리에 사용된다.

<br>

### 비교
본 시나리오에서 구성하고자하는 대기열은 먼저 진입한 사용자가 더 일찍 권한을 얻어야하기 때문에 <br>
우선순위가 보장되어야 한다. 이 관점에서보면 두 가지 자료형 모두 필요한 대기열을 구현하는데 적합해 보인다. <br>
하지만, 서비스에서 대기열 순위를 제공하는 API가 존재하기 때문에 Sorted Sets 자료구조를 사용하는 것이 <br>
적합하다.

<br><br>

## 비즈니스 로직 구조
서비스의 중심에는 대기열 토큰이 존재하며 서비스에서 제공하는 API를 이용하기 위해서는 이를 통과해야한다. <br>

위의 요구사항을 충족시키기 위하여 대기열 토큰은 대기 토큰(waiting token)과 접근 토큰(access token) <br>
두가지 유형으로 나뉘며 접근 토큰을 지닌 사용자들만이 서비스에서 제공하는 API 기능(콘서트 조회, 예약, 결제 <br>
등)을 이용할 수 있다.

대기열 토큰은 사용자의 인증(Authentication)과 인가(Authorization)을 처리하기 때문에 <br>
어플리케이션 서버 앞단에서 필터를 통해 해당토큰의 유효성을 검증한다. 두 토큰 모두 발행 후 각각 일정 시간이 <br>
지나면 만료 및 삭제 처리가 되어 더이상 사용할 수 없다.

적절한 동시 접속자 수준까지 토큰을 요청하면 접근 토큰을 받을 수 있으며 그 이후로는 대기 토큰을 받게된다. <br>
서버는 일정 주기(5분) 마다 서버내 접속자 수를 확인 후 우선순위를 갖고 있는 대기 사용자에게 접근토큰을 발행한다.* <br>
\* 대기토큰 제거 후 접근 토큰 발행


<br><br>

## DB vs Redis
현재 서버는 DB에 의존적인 상태이기 때문에 모든 요청이 부하를 가져올 수 있다. <br>
대기열의 경우 DB가 아닌 인메모리 모듈(Redis, etc) 충분히 구현 가능하다. Redis를 활용할 경우 <br>
메모리 기반이기 때문에 빠른 속도를 제공할 뿐만 아니라 서버에 가해지는 부하도 줄일 수 있다는 장점이 있다. <br>
아래는 대기열 관련 주요 로직에 대한 비교 테스트 결과다.

[비고] <br>
비교테스트 케이스 대부분 DB와 Redis 모두 비슷한 결과를 확인하여 단 한 케이스만*을 제외하고 유의미한 결과는 얻지 못하였다. <br>

\* 사용자 100명이 동시에 토큰 발행을 요청한 상황을 가정했을 때는 Redis가 더 빠른 결과를 확인할 수 있었다.

**토큰 생성** 

- 단순 10회 반복 <br>
<img src="https://github.com/user-attachments/assets/98c99ecc-4441-4e4e-971a-b3783cc11dcb" width="350px" height="400px">
<img src="https://github.com/user-attachments/assets/cf8249b3-8929-469b-9940-0df6d559bcba" width="350px" height="400px"> 
<br>

- 동시 생성 테스트 (100명 가정) <br>
![image](https://github.com/user-attachments/assets/a2c8c127-056a-4873-982c-013df3451bd2) <br>
![image](https://github.com/user-attachments/assets/a48c2b19-121a-453f-b383-f3fccbe5661f)

<br>

**토큰 검증**
- 단순 5회 반복 <br>
![image](https://github.com/user-attachments/assets/e7f728b8-e366-4d17-81c7-c2f22c0acfc6) <br>
![image](https://github.com/user-attachments/assets/3ae383c1-1aa5-43ee-9413-7d1c3f7fab98)

<br>

**100번 동시 처리** <br>
![image](https://github.com/user-attachments/assets/bf8b9661-3b21-47e1-b3c3-2f527cf671c3) <br>
![image](https://github.com/user-attachments/assets/915bf87a-8107-4765-b0cf-09d600061b71)



## References

---
- Redis Data type
  https://redis.io/docs/latest/develop/data-types/