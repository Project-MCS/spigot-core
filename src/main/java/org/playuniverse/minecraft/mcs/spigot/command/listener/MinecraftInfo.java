package org.playuniverse.minecraft.mcs.spigot.command.listener;

import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;

public class MinecraftInfo {

    private final PluginBase<?> base;

    public MinecraftInfo(PluginBase<?> base) {
        this.base = base;
    }

    public PluginBase<?> getBase() {
        return base;
    }

}
