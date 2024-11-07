package io.hhplus.conbook.performance.booking;

import io.hhplus.conbook.domain.booking.AlreadyOccupiedException;
import io.hhplus.conbook.domain.booking.BookingRepository;
import io.hhplus.conbook.domain.booking.BookingStatus;
import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import io.hhplus.conbook.infra.db.booking.BookingEntity;
import io.hhplus.conbook.infra.db.booking.BookingJpaRepository;
import io.hhplus.conbook.infra.db.concert.SeatEntity;
import io.hhplus.conbook.infra.db.concert.SeatJpaRepository;
import io.hhplus.conbook.infra.db.user.UserEntity;
import io.hhplus.conbook.infra.db.user.UserJpaRepository;
import io.hhplus.conbook.infra.redis.RedisLockRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"/schema-cleanup.sql", "/schema.sql", "/test-data.sql"})
class BenchmarkBookingServiceTest {
    // test target
    BenchmarkBookingService benchmarkBookingService;

    // helpers
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    RedisLockRepository redisLockRepository;

    @Autowired
    BookingJpaRepository bookingJpaRepository;
    @Autowired
    SeatJpaRepository seatJpaRepository;
    @Autowired
    ConcertService concertService;
    @Autowired
    UserService userService;
    @Autowired
    UserJpaRepository userJpaRepository;

    private static final Logger log = LoggerFactory.getLogger(BenchmarkBookingServiceTest.class);

    @PostConstruct
    void init() {
        benchmarkBookingService = new BenchmarkBookingService(seatRepository, bookingRepository, redisLockRepository);
    }

    @BeforeEach
    void setup() {
        List<UserEntity> userList = new ArrayList<>();

        for (int i=0; i < 10000; i++) {
            userList.add(new UserEntity(new User("user" + i, UUID.randomUUID().toString())));
        }
        userJpaRepository.saveAll(userList);
        userJpaRepository.flush();
    }

    // =============================================================================================

    @Test
    @DisplayName("분산락 성능 테스트 - 10명 동시 예약")
    void distributedLockTest10() {
        // given
        long concertId = 1L;
        String date = "20241001";
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);

        // 10000명
        List<User> users = userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .limit(10)
                .toList();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    benchmarkBookingService.createBookingUsingDistributedLock(schedule, seat.getId(), user);
                } catch (AlreadyOccupiedException | IllegalStateException e) {
                    log.error("[FAIL] 예약 실패 - 사용자 id: {}, 좌석 id: {}", user.getId(), seat.getId());
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start) + "ms");
    }

    @Test
    @DisplayName("비관적 락 성능 테스트 - 10명 동시 예약")
    void pessimisticLockTest10() {
        // given
        long concertId = 1L;
        String date = "20241001";
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);

        // 10000명
        List<User> users = userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .limit(10)
                .toList();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    benchmarkBookingService.createBookingUsingPessimisticLock(schedule, seat.getId(), user);
                } catch (AlreadyOccupiedException e) {
                    log.error("[FAIL] 예약 실패 - 사용자 id: {}, 좌석 id: {}", user.getId(), seat.getId());
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start) + "ms");
    }

    @Test
    @DisplayName("분산락 성능 테스트 - 5000명 동시 예약")
    void distributedLockTest5000() {
        // given
        long concertId = 1L;
        String date = "20241001";
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);

        // 10000명
        List<User> users = userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .limit(5000)
                .toList();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    benchmarkBookingService.createBookingUsingDistributedLock(schedule, seat.getId(), user);
                } catch (AlreadyOccupiedException | IllegalStateException e) {
                    log.error("[FAIL] 예약 실패 - 사용자 id: {}, 좌석 id: {}", user.getId(), seat.getId());
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start) + "ms");
    }

    @Test
    @DisplayName("비관적 락 성능 테스트 - 5000명 동시 예약")
    void pessimisticLockTest5000() {
        // given
        long concertId = 1L;
        String date = "20241001";
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);

        // 10000명
        List<User> users = userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .limit(5000)
                .toList();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    benchmarkBookingService.createBookingUsingPessimisticLock(schedule, seat.getId(), user);
                } catch (AlreadyOccupiedException e) {
                    log.error("[FAIL] 예약 실패 - 사용자 id: {}, 좌석 id: {}", user.getId(), seat.getId());
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start) + "ms");
    }

    @Test
    @DisplayName("분산락 성능 테스트 - 10000명 동시 예약")
    void distributedLockTest() {
        // given
        long concertId = 1L;
        String date = "20241001";
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);

        // 10000명
        List<User> users = userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .limit(10000)
                .toList();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    benchmarkBookingService.createBookingUsingDistributedLock(schedule, seat.getId(), user);
                } catch (AlreadyOccupiedException | IllegalStateException e) {
                    log.error("[FAIL] 예약 실패 - 사용자 id: {}, 좌석 id: {}", user.getId(), seat.getId());
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start) + "ms");


    }

    @Test
    @DisplayName("비관적 락 성능 테스트 - 10000명 동시 예약")
    void pessimisticLockTest() {
        // given
        long concertId = 1L;
        String date = "20241001";
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);

        // 10000명
        List<User> users = userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .limit(10000)
                .toList();

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    benchmarkBookingService.createBookingUsingPessimisticLock(schedule, seat.getId(), user);
                } catch (AlreadyOccupiedException e) {
                    log.error("[FAIL] 예약 실패 - 사용자 id: {}, 좌석 id: {}", user.getId(), seat.getId());
                }
            });
        }

        long start = System.currentTimeMillis();
        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();
        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start) + "ms");
    }

    // =============================================================================================

    @AfterEach
    @Transactional
    void clear() {
        System.out.println("*** 예약정보 초기화 ***");

        List<BookingEntity> deleteTarget = bookingJpaRepository.findAll()
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.RESERVED))
                .toList();
        bookingJpaRepository.deleteAll(deleteTarget);

        List<SeatEntity> seatList = seatJpaRepository.findAll();
        for (SeatEntity seat : seatList) {
            ReflectionTestUtils.setField(seat, "isOccupied", false);
            seatJpaRepository.save(seat);
        }

        List<UserEntity> list = userJpaRepository.findAll()
                .stream()
                .filter(u -> u.getId() > 2L)
                .toList();

        userJpaRepository.deleteAll(list);
    }

}