package org.playuniverse.minecraft.vcompat.reflection.data.persistence;

import java.io.IOException;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtNamedTag;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;
import com.syntaxphoenix.syntaxapi.nbt.tools.NbtDeserializer;
import com.syntaxphoenix.syntaxapi.nbt.tools.NbtSerializer;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

public class DataObserver implements Runnable {

    private final PersistentContainer<?> container;

    private boolean alive = true;
    private boolean stopIndicator = false;

    private long lastModified = 0L;
    private boolean saving = false;
    private boolean loading = false;
    private Thread thread;

    private long wait = 700L;

    protected DataObserver(PersistentContainer<?> container) {
        this.container = container;
        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.setName("Observer-" + container.getKey().toString());
        this.thread.start();
    }

    @Override
    public void run() {
        while (!stopIndicator) {
            handle();
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                // IGNORE
            }
        }
    }

    public long getWaitingTime() {
        return wait;
    }

    public void setWaitingTime(long wait) {
        this.wait = wait;
    }

    private void handle() {
        if (saving || loading) {
            return;
        }
        if (!alive) {
            stopIndicator = true;
        }
        if (alive && checkMod()) {
            load();
        }
        if (alive && container.changed) {
            save();
            container.changed = false;
        }
    }
    
    private boolean checkMod() {
        return (container.location.lastModified() - lastModified) > wait;
    }

    public void load() {
        loading = true;
        container.lock.writeLock().lock();
        if (container.location.exists()) {
            NbtNamedTag tag = null;
            try {
                tag = NbtDeserializer.COMPRESSED.fromFile(container.location);
            } catch (IOException ignore) {
            }
            if (tag != null && tag.getTag().getType() == NbtType.COMPOUND) {
                container.fromNbt((NbtCompound) tag.getTag());
            }
        }
        container.lock.writeLock().unlock();
        if (container.consumer != null) {
            try {
                container.consumer.accept(container);
            } catch (Exception note) {
                System.out.println(Exceptions.stackTraceToString(note));
            }
        }
        lastModified = container.location.lastModified();
        loading = false;
    }

    public void save() {
        saving = true;
        try {
            Files.createFile(container.location);
            NbtSerializer.COMPRESSED.toFile(new NbtNamedTag("root", container.asNbt()), container.location);
        } catch (IOException ignore) {
        }
        lastModified = container.location.lastModified();
        saving = false;
    }

    public void shutdown() {
        alive = false;
    }

    public void shutdownNow() {
        if (!thread.isAlive()) {
            return;
        }
        shutdown();
        thread.interrupt();
    }

    public boolean isAlive() {
        return alive;
    }

    public long lastModified() {
        return lastModified;
    }

}