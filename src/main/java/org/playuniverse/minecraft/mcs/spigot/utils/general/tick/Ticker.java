package org.playuniverse.minecraft.mcs.spigot.utils.general.tick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public final class Ticker {

    public static final long NANO_TIME = TimeUnit.SECONDS.toNanos(1);

    public static final long DEFAULT_DELAY = 0L;
    public static final long DEFAULT_TICK_TIME = 50L;

    private static long ID = 0;

    private static String nextName() {
        return "Ticker " + (ID++);
    }

    private final List<ITickReceiver> receivers = Collections.synchronizedList(new ArrayList<>());
    private final Thread thread;

    private final long delay;
    private long length;

    private long emptyLength;

    private long time;
    private long delta = 0L;

    private long counter = 0L;

    private int ticks = 0;
    private int tps = 0;

    private int state = 0;

    public Ticker() {
        this(nextName(), DEFAULT_TICK_TIME, DEFAULT_DELAY);
    }

    public Ticker(long length) {
        this(nextName(), length, DEFAULT_DELAY);
    }

    public Ticker(long length, long delay) {
        this(nextName(), length, delay);
    }

    public Ticker(String name) {
        this(name, DEFAULT_TICK_TIME, DEFAULT_DELAY);
    }

    public Ticker(String name, long length) {
        this(name, length, DEFAULT_DELAY);
    }

    public Ticker(String name, long length, long delay) {
        thread = new Thread(this::timeTick);
        thread.setName(name);
        thread.setDaemon(true);
        this.delay = delay;
        setLength(length);
        thread.start();
    }

    public boolean add(ITickReceiver receiver) {
        if (receivers.contains(receiver)) {
            return false;
        }
        return receivers.add(receiver);
    }

    public boolean remove(ITickReceiver receiver) {
        return receivers.remove(receiver);
    }

    public ITickReceiver find(Predicate<ITickReceiver> predicate) {
        return receivers.stream().filter(predicate).findAny().orElse(null);
    }

    public long getLength() {
        return length;
    }

    public Ticker setLength(long length) {
        this.length = length;
        this.emptyLength = (length + 1) * 3;
        return this;
    }

    public long getTime() {
        return time;
    }

    public long getDelta() {
        return delta;
    }

    public int getTps() {
        return tps;
    }

    private void timeTick() {
        try {
            Thread.sleep(delay);
        } catch (IllegalArgumentException | InterruptedException ignore) {
            // Delay Sleep Error
        }
        while (state != 2) {
            if (receivers.isEmpty() || state == 1) {
                time = System.nanoTime();
                try {
                    Thread.sleep(emptyLength);
                } catch (IllegalArgumentException | InterruptedException ignore) {
                    // Empty Sleep Error
                }
                continue;
            }
            delta = System.nanoTime() - time;
            updateTps();
            ITickReceiver[] receivers = this.receivers.toArray(ITickReceiver[]::new);
            for (ITickReceiver receiver : receivers) {
                receiver.onTick(delta);
            }
            time = System.nanoTime();
            try {
                if (length == 0) {
                    continue;
                }
                Thread.sleep(length);
            } catch (IllegalArgumentException | InterruptedException ignore) {
                // Sleep Error
            }
        }
    }

    private void updateTps() {
        if (counter >= NANO_TIME) {
            tps = ticks;
            ticks = 0;
            counter = counter - NANO_TIME;
        }
        ticks++;
    }

    public void pause() {
        state = 1;
        thread.interrupt();
    }

    public void stop() {
        state = 2;
        thread.interrupt();
    }

    public void start() {
        state = 0;
        thread.interrupt();
    }

    public boolean isRunning() {
        return state == 0;
    }

    public boolean isPaused() {
        return state == 1;
    }

    public boolean isStopped() {
        return state == 2;
    }

}
