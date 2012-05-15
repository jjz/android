package com.test.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutor {
	int poolSize = 5;
	int maxPoolSize = 10;
	long keepAliveTime = 10;
	ThreadPoolExecutor threadPool = null;
	final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(
			);

	public MyThreadPoolExecutor() {
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS, queue);
	}

	public void runTask(Runnable task) {
		threadPool.execute(task);
		System.out.println("task count.." + queue.size());

	}

	public void shutDown() {
		threadPool.shutdown();
	}
}
