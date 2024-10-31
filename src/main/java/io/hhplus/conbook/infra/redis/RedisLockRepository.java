package io.hhplus.conbook.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean lock(String key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key, "1");
    }

    public boolean unlock(String key) {
        return redisTemplate.delete(key);
    }

    // Q: simple lock 구현시 의미있는 사용자 식별값을 value에 넣어서
    // 락을 생성하고 지우기 전 검증하는 프로세스를 넣어야할까?
}
