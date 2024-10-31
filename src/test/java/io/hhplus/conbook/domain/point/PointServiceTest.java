package io.hhplus.conbook.domain.point;

import io.hhplus.conbook.infra.db.point.UserPointEntity;
import io.hhplus.conbook.infra.db.point.UserPointJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PointServiceTest.class);

    // test target
    @Autowired
    PointService pointService;

    // helpers
    @Autowired
    UserPointRepository userPointRepository;
    @Autowired
    UserPointJpaRepository userPointJpaRepository;

    @BeforeEach
    @Transactional
    void setup() {
        List<UserPointEntity> points = userPointJpaRepository.findAll();

        for (UserPointEntity point : points) {
            ReflectionTestUtils.setField(point, "point", 10000L);
        }
    }

    // =============================================================================================

    @Test
    @DisplayName("[동시성]: 사용자 포인트의 사용과 충전이 동시에 요청된 시나리오")
    void concurrencyOfPaymentAndCharge() {
        // given
        long userId = 1L;
        long payment = 1600;
        long charge = 800;

        LocalDateTime payTime = LocalDateTime.now().plusSeconds(1).truncatedTo(ChronoUnit.MICROS);
        LocalDateTime chargeTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        long startPoint = userPointRepository.getUserPoint(userId).getPoint();

        // when
        List<Runnable> tasks = new ArrayList<>();
        tasks.add(() -> {
            try {
                pointService.spendPoint(userId, payment, payTime);

            } catch (NotValidRequestException e) {
                log.error("[FAIL] charge - reqTime: ", payTime);
            }
        });

        tasks.add(() -> {
            try {
                pointService.chargePoint(userId, charge, chargeTime);

            } catch (NotValidRequestException e) {
                log.error("[FAIL] charge - reqTime: ", chargeTime);
            }
        });

        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();

        // then
        long endPoint = userPointRepository.getUserPoint(userId).getPoint();

        assertThat(800).isEqualTo(startPoint - endPoint);
    }

    @Test
    @DisplayName("[동시성]: 충전 - 예상치 못한 이유로 중복으로 요청하는 시나리오")
    void multipleRequestCharge() {
        // given
        long userId = 1L;
        long amount = 1000;
        LocalDateTime reqTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        long startPoint = userPointRepository.getUserPoint(userId).getPoint();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (int i=1; i<3; i++) {
            tasks.add(() -> {
                try {
                    pointService.chargePoint(userId, amount, reqTime);

                } catch (NotValidRequestException e) {
                    log.error("[FAIL] charge - reqTime: ", reqTime);
                }
            });
        }

        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();

        // then
        long endPoint = userPointRepository.getUserPoint(userId).getPoint();
        assertThat(endPoint - startPoint).isEqualTo(amount);
    }

    @Test
    @DisplayName("[동시성]: 결제 - 예상치 못한 이유로 중복으로 요청하는 시나리오")
    void multipleRequestSpend() {
        // given
        long userId = 1L;
        long amount = 1000;
        LocalDateTime reqTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        long startPoint = userPointRepository.getUserPoint(userId).getPoint();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (int i=1; i<3; i++) {
            tasks.add(() -> {
                try {
                    pointService.spendPoint(userId, amount, reqTime);

                } catch (NotValidRequestException e) {
                    log.error("[FAIL] charge - reqTime: ", reqTime);
                }
            });
        }

        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();

        // then
        long endPoint = userPointRepository.getUserPoint(userId).getPoint();
        assertThat(startPoint - endPoint).isEqualTo(amount);
    }
}