package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.io.File;

import org.playuniverse.minecraft.mcs.spigot.SpigotCore;

public abstract class SpigotCoreModule extends SpigotModule<SpigotCore> {

    public SpigotCoreModule(File dataLocation) {
        super(dataLocation);
    }

    @Override
    public SpigotCore getBase() {
        return SpigotCore.get();
    }

}
