/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	// My first time working with schedulers, things may not be perfect

	private HashMap<Runnable, ScheduledFuture<?>> scheduledTasks = new HashMap<Runnable, ScheduledFuture<?>>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void runTask(Runnable task, long milliDelay) {
		cleanup();
		if (!isAlreadyScheduled(task)) {
			ScheduledFuture<?> future = scheduler.schedule(task, milliDelay, TimeUnit.MILLISECONDS);
			add(future, task);
		} else {
			synchronized (scheduledTasks) {
				ScheduledFuture<?> future = scheduledTasks.get(task);
				future.cancel(false);
			}
			runTask(task, milliDelay);
		}
	}

	protected void add(ScheduledFuture<?> future, Runnable run) {
		synchronized (scheduledTasks) {
			scheduledTasks.put(run, future);
		}
	}

	protected boolean isAlreadyScheduled(Runnable task) {
		synchronized (scheduledTasks) {
			return scheduledTasks.containsKey(task);
		}
	}

	@SuppressWarnings("unchecked")
	protected void cleanup() {
		synchronized (scheduledTasks) {
			HashMap<Runnable, ScheduledFuture<?>> copy = (HashMap<Runnable, ScheduledFuture<?>>) scheduledTasks.clone();
			for (Runnable run : copy.keySet()) {
				ScheduledFuture<?> future = copy.get(run);
				if (future.isDone())
					scheduledTasks.remove(run);
			}
		}
	}

	public void shutdown() {
		scheduler.shutdownNow();
		scheduledTasks.clear();
	}

}
