import http from 'k6/http';
import { check, sleep } from 'k6';

// temp
export const options = {
    vus: 10,  // 가상 사용자 수
    duration: '30s',  // 테스트 실행 시간
};

// temp
export default function () {
    // const response = http.get('http://localhost:8080/v1/token/test'); // 테스트할 API 엔드포인트
    const response = http.get('http://host.docker.internal:8080/v1/token/test'); // 테스트할 API 엔드포인트

    check(response, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}