package org.playuniverse.minecraft.mcs.spigot;

import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.utils.plugin.PluginSettings;

public class SpigotCore extends PluginBase<SpigotCore> {
    
    public static final PluginSettings SETTINGS = new PluginSettings();

    public static SpigotCore get() {
        return get(SpigotCore.class);
    }

    @Override
    protected void onStartup() {

    }

    @Override
    protected void onShutdown() {

    }

}
