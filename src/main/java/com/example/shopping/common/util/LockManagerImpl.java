package com.example.shopping.common.util;

import java.util.concurrent.TimeUnit;

import static com.example.shopping.common.exception.ErrorCode.*;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LockManagerImpl implements LockManager {

	private static final String FAIR_LOCK_PREFIX = "fair-lock:";
	private static final String DISTRIBUTED_LOCK_PREFIX = "distributed-lock:";

	private final RedissonClient redissonClient;
	private final RedisUtil redisUtil;

	@Override
	public void executeWithFairLock(Long key, Runnable task) throws InterruptedException {
		String lockKey = FAIR_LOCK_PREFIX + key;
		RLock lock = redissonClient.getFairLock(lockKey);

		// 10초동안 락 획득 재시도, TTL 3초(3초 후 락 해체)
		boolean gain = lock.tryLock(10, 10, TimeUnit.SECONDS);
		if (gain) {
			try {
				task.run();
			} finally {
				lock.unlock();
			}
		} else {
			throw new ResponseStatusException(FAILED_TO_GAIN_LOCK.getStatus(), FAILED_TO_GAIN_LOCK.getMessage());
		}
	}

	@Override
	public void executeWithDistributedLock(Long key, Runnable task) throws InterruptedException {
		String lockKey = DISTRIBUTED_LOCK_PREFIX + key;

		// 10초동안 락 획득 재시도, TTL 3초(3초 후 락 해체)
		long startTime = System.currentTimeMillis();
		boolean gain = false;
		while (System.currentTimeMillis() - startTime < 10_000) {
			Boolean result = redisUtil.setIfAbsent(lockKey, "LOCK", 10, TimeUnit.SECONDS);
			if (result != null && result) {
				gain = true;
				break;
			}
			Thread.sleep(100); // 100ms 후 재시도
		}
		if (!gain) {
			throw new ResponseStatusException(FAILED_TO_GAIN_LOCK.getStatus(), FAILED_TO_GAIN_LOCK.getMessage());
		}
		try {
			task.run();
		} finally {
			redisUtil.delete(lockKey);
		}
	}
}
