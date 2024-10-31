package io.hhplus.conbook.infra.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLockRepositoryTest {

    @Autowired
    RedisLockRepository redisLockRepository;

    @Test
    @DisplayName("[정상]: 분산락(Simple Lock)을 정상적으로 획득")
    void getDistributedLock() {
        // given
        String key = "lock:seatId:4";

        // when
        boolean hasLock = redisLockRepository.lock(key);

        // then
        assertThat(hasLock).isTrue();
    }

    @Test
    @DisplayName("[정상]: 분산락(Simple Lock)을 정상적으로 반환")
    void returnDistributedLock() {
        // given
        String key = "lock:returntest:1";
        redisLockRepository.lock(key);

        // when
        boolean unlock = redisLockRepository.unlock(key);

        //then
        assertThat(unlock).isTrue();
    }
}