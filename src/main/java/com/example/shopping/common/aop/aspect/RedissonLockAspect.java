package com.example.shopping.common.aop.aspect;

import com.example.shopping.common.aop.annotation.RedissonLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;

import static com.example.shopping.common.exception.ErrorCode.FAILED_TO_GAIN_LOCK;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

	private final RedissonClient redissonClient;
	private final TransactionalAspect transactionalAspect;
	
	@Around("@annotation(com.example.shopping.common.aop.annotation.RedissonLock)")
	public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		final RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
		
		String key = createKey(signature.getParameterNames(), joinPoint.getArgs(), redissonLock.value());
		RLock rLock = redissonClient.getFairLock(key);
		
		try {
			boolean available = rLock.tryLock(
				redissonLock.waitTime(), redissonLock.leaseTime(), redissonLock.timeUnit()
			);
			if(!available) {
				log.warn("Redisson Get Lock Timeout");
			}
			
			log.info("Redisson Lock Key = {}", key);
			return transactionalAspect.proceed(joinPoint);
//			return joinPoint.proceed();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ResponseStatusException(FAILED_TO_GAIN_LOCK.getStatus(), "Interrupted while waiting for lock.");
		
		} finally {
			if(rLock.isLocked() && rLock.isHeldByCurrentThread()) {
				rLock.unlock();
				log.info("Redisson unLock Key = {}", key);
			} else {
				log.warn("Redisson Lock Already Unlock = {}", key);
			}
		}
	}
	
	private String createKey(String[] paramNames, Object[] args, String key) {
		String resultKey = key;
		
		for (int i = 0; i < paramNames.length; i++) {
			if (paramNames[i].equals(key)) {
				resultKey += args[i];
				break;
			}
		}
		return resultKey;
	}
}
