package io.hhplus.conbook.performance.index;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.infra.db.concert.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Sql(scripts = {"/schema-cleanup.sql", "/schema.sql"})
public class DummyDataBuilder {
    @Autowired
    ConcertJpaRepository concertJpaRepository;
    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    SeatJpaRepository seatJpaRepository;

    /**
     * 쿼리 인덱스 결과를 확인하기 위해 테스트 데이터 삽입
     */
    @Test
    @DisplayName("[테스트용 데이터]: 콘서트, 일정, 좌석")
    @Transactional
    @Rollback(false)
    void dataBuilder() {
        // concert 10개
        ConcertEntity concert1 = new ConcertEntity(new Concert("주원 팬 콘서트", "주원", "라이브홀"));
        ConcertEntity concert2 = new ConcertEntity(new Concert("성시경 연말 콘서트", "성시경", "KSPO DOME"));
        ConcertEntity concert3 = new ConcertEntity(new Concert("존 카메론 미첼 내한 공연", "John Cameron Mitchell", "블루스퀘어 마스터카드 홀"));
        ConcertEntity concert4 = new ConcertEntity(new Concert("2024 부활 서울 연말 콘서트", "부활", "스위스 그랜드 호텔 컨벤션 센터 4층"));
        ConcertEntity concert5 = new ConcertEntity(new Concert("자우림 단독 콘서트", "자우림", "장충 체육관"));
        ConcertEntity concert6 = new ConcertEntity(new Concert("2024 나윤권 앵콜 콘서트", "나윤권", "CG아트홀"));
        ConcertEntity concert7 = new ConcertEntity(new Concert("크라잉넛 연말 콘서트", "크라잉넛", "무신사 개러지"));
        ConcertEntity concert8 = new ConcertEntity(new Concert("전유진 팬미팅", "전유진", "명화 라이브홀"));
        ConcertEntity concert9 = new ConcertEntity(new Concert("2024 하동균 단독 콘서트", "하동균", "블루스퀘어 마스터카드 홀"));
        ConcertEntity concert10 = new ConcertEntity(new Concert("Bloom", "볼빨간사춘기", "장충체육관"));
        ConcertEntity concert11 = new ConcertEntity(new Concert("2024 이영현 콘서트 '나의 노래가 필요한 너에게", "", "코엑스 오디토리움"));
        ConcertEntity concert12 = new ConcertEntity(new Concert("78LIVE - 안다영", "안다영", "엠피엠지 2층 LOUNGE M"));
        ConcertEntity concert13 = new ConcertEntity(new Concert("POW 2024 FAN CONCERT", "POW", "신한카드 SOL페이 스퀘어 라이브홀"));
        ConcertEntity concert14 = new ConcertEntity(new Concert("Photay 내한공연 (Photay Live In Seoul)", "Photay", "채널 1969"));
        ConcertEntity concert15 = new ConcertEntity(new Concert("소향 1집 앨범 발매기념 콘서트", "소향", "각 공연장" ));
        ConcertEntity concert16 = new ConcertEntity(new Concert("쏜애플 콘서트 ‘도시전설’", "쏜애플", "YES24 LIVE HALL"));
        ConcertEntity concert17 = new ConcertEntity(new Concert("장기하 단독 공연 ［하기장기하］", "장기하", "블루스퀘어 마스터카드홀"));
        ConcertEntity concert18 = new ConcertEntity(new Concert("2024 볼빨간사춘기 단독 콘서트 ‘Bloom’", "볼빨간사춘기", "장충체육관"));
        ConcertEntity concert19 = new ConcertEntity(new Concert("2024 펭수 펭미팅－홀리 펭나잇", "펭수", "세종대학교 대양홀"));
        ConcertEntity concert20 = new ConcertEntity(new Concert("WONDERLAND FESTIVAL 2024", "WONDERLAND", "경희대학교 평화의전당"));
        ConcertEntity concert21 = new ConcertEntity(new Concert("2024 윤석철트리오 단독공연 ‘나의 여름은 아직 안 끝났어’", "윤석철트리오", "현대카드 언더스테이지"));
        ConcertEntity concert22 = new ConcertEntity(new Concert("2024 변진섭 전국투어 콘서트 : 변천사", "변진섭", "각 공연장"));
        ConcertEntity concert23 = new ConcertEntity(new Concert("김정민 전국투어 콘서트", "김정민", "각 공연장"));
        ConcertEntity concert24 = new ConcertEntity(new Concert("2024 정성하 단독콘서트 〈All The Best〉", "정성하", "신한카드 SOL페이 스퀘어 라이브홀"));
        ConcertEntity concert25 = new ConcertEntity(new Concert("2024 노을 전국투어 콘서트 〈노을이 내린 밤〉", "노을", "전국 각 지역"));
        ConcertEntity concert26 = new ConcertEntity(new Concert("2024 현역가왕 전국투어 콘서트", "가왕", "전국 각 공연장"));
        ConcertEntity concert27 = new ConcertEntity(new Concert("APF CONCERTS PRESENTS - kanekoayano Live in Seoul", "kanekoayano", "무신사 개러지"));
        ConcertEntity concert28 = new ConcertEntity(new Concert("2024 다이나믹 듀오 단독 콘서트 '가끔씩 오래 보자'", "다이나믹 듀오", "서울, 부산"));
        ConcertEntity concert29 = new ConcertEntity(new Concert("2024 오피셜히게단디즘 내한공연", "단디즘", "일산 킨텍스 제1전시장 5홀"));
        ConcertEntity concert30 = new ConcertEntity(new Concert("［20집 발매 기념 조용필＆위대한탄생 Concert - 대구］", "조용필", "대구 엑스코 동관"));
        ConcertEntity concert31 = new ConcertEntity(new Concert("엘르가든 내한공연", "엘르가든", "YES24 LIVE HALL"));
        ConcertEntity concert32 = new ConcertEntity(new Concert("0WAVE in Seoul ［HOME with 0WAVE］", "OWAVE", "TIME AFTER TIME"));
        ConcertEntity concert33 = new ConcertEntity(new Concert("시가렛 애프터 섹스 내한공연", "시가렛", "킨텍스 제1전시장 2홀"));
        ConcertEntity concert34 = new ConcertEntity(new Concert("우주용사멤버쉽클럽 단독 콘서트 〈SPACE ROCK FESTIVAL 2024〉", "우주용사멤버쉽클럽", "벨로주 홍대"));
        ConcertEntity concert35 = new ConcertEntity(new Concert("THE SOLUTIONS(솔루션스) 기획 파티 ‘FUTURE PUNK UNION II’", "솔루션스", "Baby Doll (베이비 돌)"));

        List<ConcertEntity> concertList
                = new ArrayList<>(List.of(
                        concert1, concert2, concert3, concert4, concert5, concert6, concert7, concert8, concert9, concert10,
                        concert11, concert12, concert13, concert14, concert15, concert16, concert17, concert18, concert19, concert20,
                        concert21, concert22, concert23, concert24, concert25, concert26, concert27, concert28, concert29, concert30,
                        concert31, concert32, concert33, concert34, concert35
        ));

        concertJpaRepository.saveAll(concertList);
        Random random = new Random();

        List<ConcertScheduleEntity> scheduleList = new ArrayList<>();
        for (ConcertEntity concert : concertList) {
            int concertRange = random.nextInt(4, 10);

            int month = random.nextInt(3, 13);
            int date = random.nextInt(1, 31);
            LocalDate concertDate = LocalDate.of(2024, month, date);

            for (int i = 0; i < concertRange; i++) {
                ConcertScheduleEntity schedule = new ConcertScheduleEntity(new ConcertSchedule(null, concert.toDomain(), concertDate.plusDays(i), 0, 50));
                scheduleList.add(schedule);
            }
        }
        concertScheduleJpaRepository.saveAll(scheduleList);

        List<String> rowNames = Arrays.asList("A", "B", "C", "D", "E");
        for (ConcertScheduleEntity schedule : scheduleList) {
            for (String row : rowNames) {
                for (int i=1; i<=10; i++) {
                    SeatEntity seat = new SeatEntity(new Seat(null, schedule.toDomain(), false, row, i, 10000));
                    seatJpaRepository.save(seat);
                }
            }
        }
    }
}
