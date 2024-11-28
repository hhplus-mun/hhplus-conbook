import http from 'k6/http';
import { check, sleep } from 'k6';

/*
 * 수용 인원: 2만명 (좌석 수)
 * 대기열 규모: 3만명 (수용 인원 * 1.5)
 */
export const options = {
    stages: [
        { duration: '5m', target: 20000 },
        { duration: '10m', target: 30000 },
        { duration: '10m', target: 30000 },
        { duration: '5m', target: 0 }
    ],
};

const userTokenMap = new Map();
/*
 * 테스트 스크립트가 수행되는 시점을 콘서트 예매가 시작되는 시점이라고 본다.
 * 이 때부터 점진적으로 사용자가 증가하는 시나리오를 구현하기 위해
 * 이미 콘서트 예매를 완료한 사용자의 집합을 관리하여 해당 사용자들은 더 이상 예매를 수행하지 않도록 한다.
 *
 * 적용 Executor: Ramping VUs (다른 Executors는 테스트 시작 전 초기화가 되어 처음부터 고정된 사용자 수로 테스트가 진행된다.)
 */
const finishedUserSet = new Set();

const userId = `${__VU}`;
const concertId = 1;

const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
    console.log(' [USER ID] ', userId);

    if (finishedUserSet.has(userId)) return;

    let token;
    if (userTokenMap.get(userId) === undefined) {
        // 토큰 발급
        const headers = {
            'Content-Type': 'application/json'
        }
        const tokenResponse = http.post(`${BASE_URL}/v1/token/generation`,
            JSON.stringify({
                userId: userId,
                concertId: concertId
            }),
            { headers: headers });

        check(tokenResponse, (r) => r.status === 200);
        token = tokenResponse.json();

        userTokenMap.set(userId, token);
    }
    else token = userTokenMap.get(userId);

    console.log(' [TOKEN]: ', token);

    const jwt = token.jwt;
    const isAccessToken = token.type === 'ACCESS';

    if (isAccessToken) {
        console.log(' [isAccessToken]');
        processConcertUseCase(jwt);

    } else {
        console.log(' [WAITING TOKEN]');

        // WAITING TOKEN을 받은 경우
        const headers = {
            'WaitingToken': jwt
        }
        console.log(' [HEADERS]: ', headers);

        const checkingResponse = http.get(`${BASE_URL}/v1/token/check`, {headers: headers});
        check(checkingResponse, (r) => r.status === 200);
        const position = checkingResponse.json().position;
        console.log(' [POSITION]: ', position);

        if (position == 0) userTokenMap.delete(userId);
    }
}

function processConcertUseCase(jwt) {
    console.log(' [START]: processConcertUseCase() ', jwt);

    // 헤더 설정
    const headers = {
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'application/json'
    };

    // 예약 가능한 날짜 조회
    const datesResponse = http.get(`${BASE_URL}/v1/concerts/${concertId}/available-dates`, { headers: headers });

    check(datesResponse, (r) => r.status === 200);
    console.log(' [DATES_RESPONSE]: ', datesResponse);

    const dateInfoJson = datesResponse.json();
    console.log(' [DATE_INFO_JSON]: ', dateInfoJson);

    // const concertDate = dateInfoJson.dates.find(date => date.date === "2024-08-29");
    const concertDate = dateInfoJson.dates[0].date.replaceAll('-', '');
    console.log(' [CONCERT DATE]: ', concertDate);

    // 예약 가능한 좌석 조회
    const seatsResponse = http.get(`${BASE_URL}/v1/concerts/${concertId}/available-seats?date=${concertDate}`, { headers: headers });

    check(seatsResponse, (r) => r.status === 200);
    console.log(' [SEATS_RESPONSE]: ', seatsResponse);

    const availableSeat = seatsResponse.json().seats[0];
    console.log(' [AVAILABLE SEAT]: ', availableSeat);

    // 3~5분 랜덤 대기 - 예약까지 걸리는 시간
    sleep(Math.random() * 120 + 180);

    const bookingResponse = http.post(`${BASE_URL}/v1/concerts/${concertId}/booking`,
        JSON.stringify({
            date: concertDate,
            seatId: availableSeat.id
        }),
        { headers: headers });

    check(bookingResponse, (r) => r.status === 200);
    console.log(' [BOOKING_RESPONSE]: ', bookingResponse);

    // 예약한 사용자 중 90%만 결제 처리
    if (Math.random() < 0.9) {
        // 3~5분 랜덤 대기 - 예약까지 걸리는 시간
        sleep(Math.random() * 120 + 180);

        const bookingId = bookingResponse.json().bookingId;
        const paymentsResponse = http.post(`${BASE_URL}/v1/bookings/${bookingId}/payment`, '', { headers: headers });

        check(paymentsResponse, (r) => r.status === 200);
        console.log(' [PAYMENTS_RESPONSE]: ', paymentsResponse);
    }
    finishedUserSet.add(userId);
}