package org.playuniverse.minecraft.mcs.spigot.config.config;

import java.io.File;

import org.playuniverse.minecraft.mcs.spigot.config.base.json.JsonConfigBase;
import org.playuniverse.minecraft.mcs.spigot.config.migration.DebugMigration;

public final class DebugConfig extends JsonConfigBase {

    public DebugConfig(File folder) {
        super(new File(folder, "debug.json"), DebugMigration.class, 2);
    }

    @Override
    protected String getName() {
        return "debug";
    }

    @Override
    protected void onLoad() {

    }

}
