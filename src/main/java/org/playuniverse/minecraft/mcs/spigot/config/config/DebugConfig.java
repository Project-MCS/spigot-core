package org.playuniverse.minecraft.mcs.spigot.config.config;

import java.io.File;

import org.pf4j.RuntimeMode;
import org.playuniverse.minecraft.mcs.spigot.config.base.json.JsonConfigBase;

public final class DebugConfig extends JsonConfigBase {

    private RuntimeMode mode = RuntimeMode.DEPLOYMENT;

    public DebugConfig(File folder) {
        super(new File(folder, "debug.json"), null, 1);
    }

    @Override
    protected String getName() {
        return "debug";
    }

    @Override
    protected void onLoad() {

        this.mode = parseMode(check("mode", mode.toString().toLowerCase()));

    }

    private RuntimeMode parseMode(String name) {
        if (name == null) {
            return this.mode;
        }
        name = name.toUpperCase();
        for (RuntimeMode mode : RuntimeMode.values()) {
            if (mode.name().equals(name)) {
                return mode;
            }
        }
        return this.mode; // Return current if not parseable
    }

    /*
     * 
     */

    public final RuntimeMode getMode() {
        return mode;
    }

}
