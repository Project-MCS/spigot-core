package org.playuniverse.minecraft.mcs.spigot.utils.wait;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.syntaxphoenix.syntaxapi.utils.general.Status;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class Awaiter<T> {

	private static final ConcurrentHashMap<Class<?>, WaitFunction<?>> FUNCTIONS = new ConcurrentHashMap<>();

    static {
        register(Status.class, WaitFunction.STATUS);
        register(Future.class, WaitFunction.FUTURE);
    }

	@SuppressWarnings("unchecked")
	public static <T> Awaiter<T> of(T waited) {
		Class<?> clazz = waited.getClass();
		if (!FUNCTIONS.containsKey(clazz)) {
			return null;
		}
		return new Awaiter<>(waited, (WaitFunction<T>) FUNCTIONS.get(clazz));
	}

	public static <E> void register(Class<E> clazz, WaitFunction<E> function) {
		if (FUNCTIONS.containsKey(clazz)) {
			return;
		}
		FUNCTIONS.put(clazz, function);
	}

	private final Container<T> waited = Container.of();
	private final WaitFunction<T> function;

	private Awaiter(T waited, WaitFunction<T> function) {
		this.waited.replace(waited);
		this.function = function;
	}

	public boolean now(T object) {
		if (waited.isPresent()) {
			return false;
		}
		waited.replace(object);
		return true;
	}

	public boolean isAvailable() {
		return waited.isPresent();
	}

	public boolean isDone() {
		if (!isAvailable()) {
			return true;
		}
		return function.isDone(waited.get());
	}

	public boolean await() {
		if (!isAvailable()) {
			return true;
		}
		function.await(waited.get());
		return done();
	}

	public boolean await(long interval) {
		if (!isAvailable()) {
			return true;
		}
		function.await(waited.get(), interval);
		return done();
	}

	public boolean await(long interval, int length) {
		if (!isAvailable()) {
			return true;
		}
		function.await(waited.get(), interval, length);
		return done();
	}

	private boolean done() {
		try {
			return isDone();
		} finally {
			waited.replace(null);
		}
	}

}
