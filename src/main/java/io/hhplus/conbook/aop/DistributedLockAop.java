package io.hhplus.conbook.aop;

import io.hhplus.conbook.infra.redis.RedisLockRepository;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class DistributedLockAop {
    private final RedisLockRepository redisLockRepository;

    @Around("@annotation(distributedLock)")
    public Object executeLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        Long seatId = extractSeatId(joinPoint);
        String key = String.format("lock:seatId:%d", seatId);

        log.debug("Start to request distribution lock for {}", key);
        try {
            if (!redisLockRepository.lock(key)) {
                throw new IllegalStateException(ErrorCode.LOCK_ACQUISITION_FAILED.getCode());
            }
            return joinPoint.proceed();
        } finally {
            redisLockRepository.unlock(key);
            log.debug("Return distribution lock for {}", key);
        }
    }

    private Long extractSeatId(ProceedingJoinPoint joinPoint) {
        Object[] givenArgs = joinPoint.getArgs();

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Parameter[] parameters = method.getParameters();

        for (int i=0; i< parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(SimpleLockKey.class)) {
                return (Long) givenArgs[i];
            }
        }
        throw new IllegalStateException();
    }
}
