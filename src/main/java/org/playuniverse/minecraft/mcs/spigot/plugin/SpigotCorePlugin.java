package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.io.File;

import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;

public abstract class SpigotCorePlugin extends SpigotPlugin<SpigotCore> {

    public SpigotCorePlugin(PluginWrapper wrapper, File dataLocation) {
        super(wrapper, dataLocation);
    }

    @Override
    public SpigotCore getBase() {
        return SpigotCore.get();
    }

}
