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

const userId = `${__VU}`;
const concertId = 1;

const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
    // 토큰 발급
    const tokenResponse = http.post(`${BASE_URL}/v1/token/generation`, {
        userId: userId,
        concertId: concertId
    });

    check(tokenResponse, (r) => r.status === 200);

    const token = tokenResponse.json();

    const jwt = token.jwt;
    const isAccessToken = token.type === 'ACCESS';

    if (isAccessToken) {
        processConcertUsecase(jwt);

        // 활성화되어 있는 VU 종료
        return;

    } else {
        // WAITING TOKEN을 받은 경우
        const headers = {
            'WaitingToken': jwt
        }
        let position = -1;

        while (position !== 0) {
            const checkingResponse = http.get(`${BASE_URL}/v1/token/check`, { headers: headers });
            check(checkingResponse, (r) => r.status === 200);
            position = checkingResponse.json().position;
        }
    }
}

function processConcertUsecase(jwt) {
    // 헤더 설정
    const headers = {
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'application/json'
    };

    // 예약 가능한 날짜 조회
    const datesResponse = http.get(`${BASE_URL}/v1/concerts/${concertId}/available-dates`, { headers: headers });

    check(datesResponse, (r) => r.status === 200);

    const dateInfoJson = datesResponse.json();
    const concertDate = dateInfoJson.dates.find(date => date.date === "2024-05-14");

    // 예약 가능한 좌석 조회
    const seatsResponse = http.get(`${BASE_URL}/v1/concerts/${concertId}/available-seats?date=${concertDate}`, { headers: headers });

    check(seatsResponse, (r) => r.status === 200);

    const availableSeat = seatsResponse.json().seats[0];

    // 3~5분 랜덤 대기 - 예약까지 걸리는 시간
    sleep(Math.random() * 120 + 180);

    const bookingResponse = http.post(`${BASE_URL}/v1/concerts/${concertId}/booking`,
        JSON.stringify({
            date: concertDate,
            seatId: availableSeat.id
        }),
        { headers: headers });

    check(bookingResponse, (r) => r.status === 200);

    // 예약한 사용자 중 90%만 결제 처리
    if (Math.random() < 0.9) {
        // 3~5분 랜덤 대기 - 예약까지 걸리는 시간
        sleep(Math.random() * 120 + 180);

        const bookingId = bookingResponse.json().bookingId;
        const paymentsResponse = http.post(`${BASE_URL}/v1/bookings/${bookingId}/payment`, { headers: headers });

        check(paymentsResponse, (r) => r.status === 200);
    }
}