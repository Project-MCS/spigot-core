package org.playuniverse.minecraft.mcs.spigot.command.listener;

import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;

public abstract class MinecraftInfoAdapter {
    
    private final MinecraftInfo info;
    
    public MinecraftInfoAdapter(MinecraftInfo info) {
        this.info = info;
    }
    
    public PluginBase<?> getBase() {
        return info.getBase();
    }

}
