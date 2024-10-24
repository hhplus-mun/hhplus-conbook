package io.hhplus.conbook;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.concert.ConcertRepository;
import io.hhplus.conbook.domain.token.TokenQueue;
import io.hhplus.conbook.domain.token.TokenQueueRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DummyDataSetup {
    /** [가정] 서버 내부에서 리소스를 이용할 수 있는 사용자 수 (명)*/
    private final int SERVER_MAX_CAPACITY = 1000;

    private final ConcertRepository concertRepository;
    private final TokenQueueRepository queueRepository;

    @PostConstruct
    private void setup() {
        List<Concert> concertList = concertRepository.getConcertList();
        if (concertList.size() == 0) {
            return;
        }

        List<TokenQueue> queueList = queueRepository.getQueueListWithoutItems();

        // 분산서버 환경에서 최초로 띄워진 Application Server 인 경우
        if (queueList.size() == 0) {
            // Default Strategy - 콘서트당 이용가능 사용자 고르게 분포
            // TODO: 콘서트 별 인기에 따라 허용 가능 사용자 분포 전략 설정
            int capacityPerConcert = (SERVER_MAX_CAPACITY * 90 / 100) / concertList.size();

            for (Concert concert : concertList) {
                queueRepository.addQueue(new TokenQueue(concert, capacityPerConcert));
            }
        }
    }

}
