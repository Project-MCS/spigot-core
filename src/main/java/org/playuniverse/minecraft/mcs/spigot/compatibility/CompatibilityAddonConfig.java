package org.playuniverse.minecraft.mcs.spigot.compatibility;

import java.io.File;

import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.config.Migration;
import org.playuniverse.minecraft.mcs.spigot.config.base.yaml.YamlConfigBase;
import org.playuniverse.minecraft.mcs.spigot.utils.plugin.PluginPackage;

public abstract class CompatibilityAddonConfig<A extends CompatibilityAddon> extends YamlConfigBase {

    private final A addon;
    private final String name;

    public CompatibilityAddonConfig(A addon, PluginPackage pluginPackage, Class<? extends Migration> clazz, int latestVersion) {
        super(new File(SpigotCore.get().getCompatDirectory(), pluginPackage.getName() + ".yml"), clazz, latestVersion);
        this.addon = addon;
        this.name = pluginPackage.getName();
    }

    public A getAddon() {
        return addon;
    }

    public final String getAddonName() {
        return name;
    }

}
