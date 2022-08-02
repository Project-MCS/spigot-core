package org.playuniverse.minecraft.mcs.spigot.utils.wait;

import java.util.concurrent.Future;

import org.playuniverse.minecraft.mcs.spigot.helper.task.TaskFuture;
import org.playuniverse.minecraft.mcs.spigot.helper.task.DoneFuture;

import com.syntaxphoenix.syntaxapi.utils.general.Status;

@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface WaitFunction<E> {

	public static final WaitFunction<Status> STATUS = Status::isDone;
	public static final WaitFunction<Future> FUTURE = Future::isDone;
    public static final WaitFunction<DoneFuture> DONE_FUTURE = DoneFuture::isDone;
    public static final WaitFunction<TaskFuture> BUKKIT_FUTURE = TaskFuture::isDone;

	/*
	 * 
	 */

	public static final long WAIT_INTERVAL = 10L;
	public static final int WAIT_INFINITE = -1;

	default void await(E waited) {
		await(waited, WAIT_INTERVAL);
	}

	default void await(E waited, long interval) {
		await(waited, WAIT_INTERVAL, WAIT_INFINITE);
	}

	default void await(E waited, long interval, int length) {
		while (!isDone(waited)) {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ignore) {
				break;
			}
			if (length == -1) {
				continue;
			}
			if (length-- == 0) {
				break;
			}
		}
	}

	boolean isDone(E waited);

}
