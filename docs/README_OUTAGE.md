# 장애 대응 보고서

진행된 부하테스트 결과를 확인하면 시스템이 제대로 부하를 처리하지 못했음을 확인할 수 있다.

## 이슈 분석

- K6 test results <br>
  ![image](https://github.com/user-attachments/assets/52b68b56-a40f-482e-9bde-dce06f235894) <br>

HTTP 요청 실패율이 73.79%로 높게 나타나, 대다수의 사용자 요청이 서비스에서 정상 처리되지 못했다.

DB 데이터를 확인하면 이보다 더 높은 실패율을 확인할 수 있었으며, 대부분의 사용자가 예매를 완료하지 못한 것으로 확인된다.

- 토큰 발급이력 DB table <br>
  ![image](https://github.com/user-attachments/assets/3a846c83-e025-44a8-8756-54029040f467) <br>

- 콘서트 예약 이력 DB table <br>
  ![image](https://github.com/user-attachments/assets/7e574aba-8144-4649-96d9-e6366292e950) <br>

- 콘서트 결제 이력 DB table <br>
  ![image](https://github.com/user-attachments/assets/8db0441b-0508-4bd8-863b-db15cb248ef6) <br>

---

생성된 이력은 각각 766건, 17건, 6건으로 수용인원을 2만명으로 가정한 콘서트의 예약 및 결제 이력이라 생각하면 매우 낮은 수준이다.

### Unhandled Exception - APP Crash

- Access Token 검증에 실패하는 Exception <br>
  ![image](https://github.com/user-attachments/assets/5db8e771-ca8d-479a-ac55-c1c76d24b1bc) <br>

<br>

- Waiting Token 을 사용해 대기열 순위를 확인할 때 발생하는 Exception - 1 (MalformedJwtException) <br>
  ![image](https://github.com/user-attachments/assets/d1cb365a-cace-4b9c-9f81-14279af3dd93) <br>

<br>

- Waiting Token 을 사용해 대기열 순위를 확인할 때 발생하는 Exception - 2 (NotValidTokenException) <br>
  ![image](https://github.com/user-attachments/assets/1f850e05-9c4c-4383-a902-0875e6d5d777) <br>

<br>

- Waiting Token 을 사용해 대기열 순위를 확인할 때 발생하는 Exception - 3 <br>
  ![image](https://github.com/user-attachments/assets/43c66eb9-4140-4a5c-a149-2a89171be504) <br>

생성된 로그를 확인했을 때 에러의 대부분을 Waiting Token 을 사용해 대기열 순위를 확인할 때 발생하는 Exception 이 차지하고 있었다.

단건으로 처리하고 테스트를 했을 때는 문제가 되지 않았으나 대량의 요청이 발생하면서 `대기 상태 조회 API`를 호출할 때 문제가 생긴 것으로 판단된다.

더불어 Access Token 검증에 실패하는 Exception 도 발생하고 있었으며, 이는 토큰이 만료되어 발생하는 문제로 보인다.

## 장애 긴급 조치 사항

- Access Token 검증실패 오류
  - Redis Insight 을 확인했을 때, TTL 을 설정하지 않았음에도 불구하고 토큰이 저절로 만료되는 문제 확인
  - K6 테스트를 진행할 때 메모리 사용량이 급격히 증가하여 발생하는 메모리로 판단되며 이에대한 확인이 필요 (TODO)
  - (임시방편) 원인 파악이 불분명하여 Token 발급 이력의 만료시간을 축소하여 토큰 만료 시 재발급에 문제 없도록 처리

- Waiting Token parsing 오류
  - 단일 생성 테스트 시 문제가 없었으나 대량 요청 시 발생하는 문제로 판단되며 토큰이 제대로 전달되지 않은 것으로 추정
  - 단계적으로 트래픽을 늘려가며 대기열 토큰을 생성하고 조회하는 테스트를 진행하여 문제 확인이 필요 (TODO)
  - (임시방편) Access Token 과 마찬가지로 원인파악이 불분명하기 때문에 만료시간을 축소 시켜서 Waiting Token 에 문제가 존재하면 새로 발급 받을 수 있도록 처리

- 토큰 만료시간 설정 오류
  - 토큰 생성시 적용되는 만료시간은 Date 인스턴스이지만 Access Token 에만 적용하고, Waiting Token 에는 적용하지 않은 문제 해결

  ``` java
  // Before
  ...
  if (type.equals(TokenType.WAIT)) expiredAt = issuedAt.plusMinutes(WAITING_EXPIRATION_MIN);
  else {
      expiredAt = issuedAt.plusMinutes(ACCESS_EXPIRATION_MIN);
      Date expiration = Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant());
      claimsMap.put(Claims.EXPIRATION, expiration);
  }
  ...

  // After
  ...
  if (type.equals(TokenType.WAIT)) {
      expiredAt = issuedAt.plusMinutes(WAITING_EXPIRATION_MIN);
  }
  else {
      expiredAt = issuedAt.plusMinutes(ACCESS_EXPIRATION_MIN);
  }
  Date expiration = Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant());
  claimsMap.put(Claims.EXPIRATION, expiration); 
  ...
  ```

## 마무리

부하테스트 결과를 실제 장애 상황으로 가정하여 대응을 진행했다.
일정 시간동안 지속적으로 트래픽이 집중되므로 신속한 대응이 필수적이며, 명확한 원인파악이 재발 방지의 핵심이다.

이번 대응을 통해 토큰 발급 및 검증 과정의 문제점을 확인했으며, 위의 긴급 조치 이후 추가 테스트를 실시하여 재발 방지 대책을 수립하도록 한다.
