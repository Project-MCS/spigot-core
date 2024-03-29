package org.playuniverse.minecraft.mcs.spigot.config;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.playuniverse.minecraft.mcs.spigot.SpigotCore;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

public final class ConfigTimer extends Thread {

    public static final ConfigTimer TIMER = new ConfigTimer();

    private final ArrayList<ConfigBase<?, ?>> reload = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ReadLock read = lock.readLock();
    private final WriteLock write = lock.writeLock();

    private boolean alive = true;
    
    private short cycle = 0;
    
    private ConfigTimer() {
        setName("Config");
        setDaemon(true);
        start();
    }
    
    public void shutdown() {
        alive = false;
    }

    public boolean load(ConfigBase<?, ?> config) {
        boolean output;
        read.lock();
        if (output = !reload.contains(config)) {
            read.unlock();
            write.lock();
            try {
                reload.add(config);
            } finally {
                write.unlock();
            }
        }
        return output;
    }

    public boolean unload(ConfigBase<?, ?> config) {
        boolean output;
        read.lock();
        if (output = reload.contains(config)) {
            read.unlock();
            write.lock();
            try {
                reload.remove(config);
            } finally {
                write.unlock();
            }
        }
        return output;
    }

    @Override
    public void run() {
        while (alive) {
            read.lock();
            try {
                ILogger logger = SpigotCore.get().getPluginLogger();
                for (ConfigBase<?, ?> config : reload) {
                    if (config.loaded < config.file.lastModified() && !config.isBusy()) {
                        logger.log(LogTypeId.INFO, "Loading config '" + config.getName() + "'...");
                        try {
                            config.reload();
                        } catch (Exception exp) {
                            logger.log(LogTypeId.WARNING, "Failed to load config '" + config.getName() + "'!", "================================");
                            logger.log(LogTypeId.WARNING, exp);
                            continue;
                        }
                        logger.log(LogTypeId.INFO, "Config '" + config.getName() + "' was successfully loaded!");
                    }
                }
                cycle++;
            } finally {
                read.unlock();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public short getCycle() {
        return cycle;
    }

    public void waitForNextCycle() {
        int current = cycle;
        while(current == cycle) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
