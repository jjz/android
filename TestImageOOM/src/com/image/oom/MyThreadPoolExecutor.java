package com.image.oom;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutor {
	int poolSize = 2;
	int maxPoolSize = 2;
	long keepAliveTime = 10;
	ThreadPoolExecutor threadPool = null;
	final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(
			10);

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
