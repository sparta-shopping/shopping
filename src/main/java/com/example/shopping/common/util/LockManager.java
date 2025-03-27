package com.example.shopping.common.util;

public interface LockManager {
	void executeWithFairLock(Long key, Runnable task) throws InterruptedException;
	void executeWithDistributedLock(Long key, Runnable task) throws InterruptedException;
}
