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
	
	@Around("@annotation(com.example.shopping.common.aop.annotation.RedissonLock)")
	public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
		
		String key = createKey(signature.getParameterNames(), joinPoint.getArgs(), redissonLock.value());
		
		RLock rLock = redissonClient.getFairLock(key);
		rLock.lock(redissonLock.waitTime(), redissonLock.timeUnit());
		
		try {
			log.info("Redisson Lock Key = {}", key);
			return joinPoint.proceed();
			
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ResponseStatusException(FAILED_TO_GAIN_LOCK.getStatus(), "Interrupted while waiting for lock.");
		
		} finally {
			if(rLock.isHeldByCurrentThread()) {
				rLock.unlock();
				log.info("Redisson unLock Key = {}", key);
			} else {
				log.warn("Redisson Lock Already Unlock = {}", key);
			}
		}
	}
	
	private String createKey(String[] paramNames, Object[] args, String key) {
		StringBuilder resultKey = new StringBuilder(key);
		
		for (int i = 0; i < paramNames.length; i++) {
			if (paramNames[i].equals(key)) {
				resultKey.append(args[i].toString());
				break;
			}
		}
		return resultKey.toString();
	}
}
