package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.config.ScheduledTaskExecutor;
import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.token.generation.TokenType;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import io.hhplus.conbook.infra.db.token.TokenQueueEntity;
import io.hhplus.conbook.infra.db.token.TokenQueueItemJpaRepository;
import io.hhplus.conbook.infra.db.token.TokenQueueJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class TokenManagerTest {
    // Test 대상
    @Autowired
    TokenManager tokenManager;

    // 환경 셋팅
    @Autowired
    UserService userService;
    @Autowired
    ConcertService concertService;
    @Autowired
    TokenQueueJpaRepository tokenQueueJpaRepository;
    @Autowired
    TokenQueueItemJpaRepository tokenQueueItemJpaRepository;

    @Autowired
    ScheduledTaskExecutor scheduledTaskExecutor;

    @BeforeEach
    void init() {
        System.out.println("콘서트 대기열 접근 허용 수 설정: 1");
        long concertId = 1L;

        TokenQueueEntity queue = tokenQueueJpaRepository.findByConcertId(concertId).get();
        ReflectionTestUtils.setField(queue, "accessCapacity", 1);
        tokenQueueJpaRepository.save(queue);
    }

    @AfterEach
    void clear() {
        System.out.println("토큰 대기열 초기화");
        tokenQueueItemJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("[정상]: 액세스 토큰 생성")
    void createAccessToken() {
        // given
        long userId = 1L;
        long concertId = 1L;
        User user = userService.getUser(userId);
        Concert concert = concertService.getConcert(concertId);
        System.out.println("---");

        // when
        Token token = tokenManager.creaateToken(user, concert);

        assertThat(token.type()).isEqualTo(TokenType.ACCESS);
    }

    @Test
    @DisplayName("[예외]: 콘서트에 대한 사용자 토큰이 이미 발급된 경우")
    void tokenAlreadyExists() {
        // given
        long userId = 1L;
        long concertId = 1L;
        User user = userService.getUser(userId);
        Concert concert = concertService.getConcert(concertId);
        tokenManager.creaateToken(user, concert);
        System.out.println("---");

        // when & then
        assertThatThrownBy(() -> tokenManager.creaateToken(user, concert))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("[정상]: 대기열 토큰 생성")
    void createWaitingToken() {
        // given
        long userId1 = 1L;
        long userId2 = 2L;
        long concertId = 1L;

        User user1 = userService.getUser(userId1);
        User user2 = userService.getUser(userId2);
        Concert concert = concertService.getConcert(concertId);
        tokenManager.creaateToken(user1, concert);

        // when
        Token token = tokenManager.creaateToken(user2, concert);

        // then
        assertThat(token.type()).isEqualTo(TokenType.WAIT);
    }

    @Test
    @DisplayName("[정상]: 대기열 순위 반환")
    void returnPosition() {
        //given
        long userId1 = 1L;
        long userId2 = 2L;
        long concertId = 1L;

        User user1 = userService.getUser(userId1);
        User user2 = userService.getUser(userId2);
        Concert concert = concertService.getConcert(concertId);
        tokenManager.creaateToken(user1, concert);
        Token token = tokenManager.creaateToken(user2, concert);

        //when
        TokenStatusInfo waitingInfo = tokenManager.getWaitingStatusInfo(token.jwt());

        //then
        assertAll(
                () -> assertThat(waitingInfo.getPosition()).isGreaterThan(0),
                () -> assertThat(waitingInfo.isPassed()).isFalse()
        );
    }

    @Test
    @DisplayName("[정상]: 대기열 통과에 따른 Access Token 반환")
    void returnAccessToken() {
        //given
        long userId1 = 1L;
        long userId2 = 2L;
        long concertId = 1L;

        User user1 = userService.getUser(userId1);
        User user2 = userService.getUser(userId2);
        Concert concert = concertService.getConcert(concertId);
        tokenManager.creaateToken(user1, concert);
        Token token = tokenManager.creaateToken(user2, concert);

        TokenQueueEntity queue = tokenQueueJpaRepository.findByConcertId(concertId).get();
        ReflectionTestUtils.setField(queue, "accessCapacity", 2);
        tokenQueueJpaRepository.save(queue);

        scheduledTaskExecutor.updateQueueList();

        //when
        TokenStatusInfo waitingInfo = tokenManager.getWaitingStatusInfo(token.jwt());

        //then
        assertAll(
                () -> assertThat(waitingInfo.isPassed()).isTrue(),
                () -> assertThat(waitingInfo.getAccessToken()).isNotNull()
        );
    }
}