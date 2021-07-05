package org.playuniverse.minecraft.mcs.spigot.config.config;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.playuniverse.minecraft.mcs.spigot.compatibility.CompatibilityHandler;
import org.playuniverse.minecraft.mcs.spigot.config.base.yaml.YamlConfigBase;
import org.playuniverse.minecraft.mcs.spigot.config.migration.AddonMigration;
import org.playuniverse.minecraft.mcs.spigot.constant.Singleton;

public final class AddonConfig extends YamlConfigBase {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ArrayList<String> disabled = new ArrayList<>();

    private final ReadLock read = lock.readLock();
    private final WriteLock write = lock.writeLock();

    private boolean skip = true;

    protected AddonConfig(File folder) {
        super(new File(folder, "compat.yml"), AddonMigration.class, 1);
    }

    /*
     * Handle
     */

    @Override
    protected String getName() {
        return "compat";
    }

    @Override
    protected void onSetup() {
        load(false);
    }

    @Override
    protected void onLoad() {
        if (skip) {
            skip = false;
            return;
        }
        load(true);
    }

    @Override
    protected void onUnload() {

    }

    private void load(boolean refresh) {
        write.lock();
        try {
            disabled.clear();
            for (String compat : CompatibilityHandler.getCompatibilityNames()) {
                if (check(compat)) {
                    disabled.add(compat);
                }
            }
            read.lock();
        } finally {
            write.unlock();
        }
        try {
            if (refresh) {
                CompatibilityHandler.handleSettingsUpdate(Singleton.General.SETTINGS);
            }
        } finally {
            read.unlock();
        }
    }

    /*
     * Method
     */

    private boolean check(String compat) {
        return !check("addons." + compat, true);
    }

    public boolean isDisabled(String name) {
        read.lock();
        try {
            return this.disabled.contains(name);
        } finally {
            read.unlock();
        }
    }

}
