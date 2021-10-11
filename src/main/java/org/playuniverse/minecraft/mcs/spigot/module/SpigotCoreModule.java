package org.playuniverse.minecraft.mcs.spigot.module;

import org.playuniverse.minecraft.mcs.spigot.SpigotCore;

public abstract class SpigotCoreModule extends SpigotModule<SpigotCore> {

    @Override
    public final SpigotCore getBase() {
        return SpigotCore.get();
    }

}
