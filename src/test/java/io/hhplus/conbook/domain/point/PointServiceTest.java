package io.hhplus.conbook.domain.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointServiceTest {

    // test target
    @Autowired
    PointService pointService;

    // helpers
    @Autowired
    UserPointRepository userPointRepository;

    // =============================================================================================

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
                    System.out.printf("[FAIL] charge - reqTime: %s\n", reqTime);
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
                    System.out.printf("[FAIL] charge - reqTime: %s\n", reqTime);
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