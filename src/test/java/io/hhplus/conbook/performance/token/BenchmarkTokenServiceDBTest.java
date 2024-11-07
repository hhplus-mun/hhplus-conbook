package io.hhplus.conbook.performance.token;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.token.TokenHistoryRepository;
import io.hhplus.conbook.domain.token.TokenInfo;
import io.hhplus.conbook.domain.token.TokenManager;
import io.hhplus.conbook.domain.token.TokenRepository;
import io.hhplus.conbook.domain.token.generation.TokenProvider;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.infra.db.concert.ConcertJpaRepository;
import io.hhplus.conbook.infra.db.user.UserEntity;
import io.hhplus.conbook.infra.db.user.UserJpaRepository;
import io.hhplus.conbook.performance.token.infra.db.TokenJpaRepository;
import io.hhplus.conbook.performance.token.infra.db.TokenRepositoryImpl;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"/schema-cleanup.sql", "/schema.sql", "/test-data.sql"})
public class BenchmarkTokenServiceDBTest {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkTokenServiceDBTest.class);

    private TokenManager tokenManager;

    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    TokenHistoryRepository tokenHistoryRepository;
    @Autowired
    TokenJpaRepository tokenJpaRepository;

    // ---
    @Autowired
    UserJpaRepository userJpaRepository;
    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @PostConstruct
    void init() {
        TokenRepository tokenRepository = new TokenRepositoryImpl(tokenJpaRepository);
        tokenManager = new TokenManager(tokenRepository, tokenHistoryRepository, tokenProvider);
    }

    @BeforeEach
    void clearAndSetup() {
        tokenJpaRepository.deleteAll();

        List<UserEntity> userList = new ArrayList<>();

        for (int i=0; i < 10000; i++) {
            userList.add(new UserEntity(new User("user" + i, UUID.randomUUID().toString())));
        }
        userJpaRepository.saveAll(userList);
        userJpaRepository.flush();
    }

    @DisplayName("[단순 반복] - 토큰 생성")
    @RepeatedTest(10)
    void createToken() {
        // given
        User user = userJpaRepository.findById(1L).get().toDomain();
        Concert concert = concertJpaRepository.findById(1L).get().toDomain();

        long start = System.currentTimeMillis();
        // when
        TokenInfo tokenInfo = tokenManager.creaateToken(user, concert);
        long end = System.currentTimeMillis();

        // then
        log.info("[duration]: {}", end - start);
        assertThat(tokenInfo).isNotNull();
    }

    @Test
    @DisplayName("[동시발생 - DB]: 토큰 여러명 생성 요청")
    void createTokenConcurrency() {
        // given
        Concert concert = concertJpaRepository.findById(1L).get().toDomain();
        // 100명
        List<User> users = userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .limit(100)
                .toList();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    tokenManager.creaateToken(user, concert);
                } catch (Exception e) {
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture<Void> allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(CompletableFuture::runAsync).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

//        log.info("[duration]: {}ms", end - start);
        System.out.println("[duration]: " + (end - start) + "ms");
    }

    @DisplayName("[단순 반복]: 토큰 검증")
    @RepeatedTest(5)
    void validateToken() {
        // given
        User user = userJpaRepository.findById(1L).get().toDomain();
        Concert concert = concertJpaRepository.findById(1L).get().toDomain();
        TokenInfo tokenInfo = tokenManager.creaateToken(user, concert);

        // when
        long start = System.currentTimeMillis();
        try {
            tokenManager.verifyToken(tokenInfo.jwt());
        } catch (Exception e) {}
        long end = System.currentTimeMillis();

        // then
        log.info("[duration]: {}", end - start);
        assertThat(tokenInfo).isNotNull();
    }

    /*
     *  테스트의 편의성을 위하여 하나의 토큰에 대해서 여러명이서 검증을 요청한다고 가정
     */
    @Test
    @DisplayName("[동시발생 - DB]: 토큰 여러번 검증 요청")
    void validationTokenConcurrency() {
        // given
        Concert concert = concertJpaRepository.findById(1L).get().toDomain();
        User user = userJpaRepository.findById(1L).get().toDomain();
        TokenInfo tokenInfo = tokenManager.creaateToken(user, concert);

        // 100 명이 요청한다고 가정
        // when
        List<Runnable> tasks = new ArrayList<>();

        for(int i=0; i < 100; i++) {
            tasks.add(() -> {
                try {
                    tokenManager.verifyToken(tokenInfo.jwt());
                } catch (Exception e) {
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture<Void> allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(CompletableFuture::runAsync).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

//        log.info("[duration]: {}ms", end - start);
        System.out.println("[duration]: " + (end - start) + "ms");
    }
}
