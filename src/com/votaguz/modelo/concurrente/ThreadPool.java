package com.votaguz.modelo.concurrente;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadPool extends ThreadPoolExecutor {

	private String _name;

	public ThreadPool(String name) {
		super(0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>());
		_name = name;
	}

	public String getName() {
		return _name;
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {

		if (r instanceof NamedRunnable) {
			NamedRunnable named = (NamedRunnable) r;
			t.setName(_name + "::" + named.getName());
		}

		super.beforeExecute(t, r);
	}
}
