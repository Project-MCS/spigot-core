package org.playuniverse.minecraft.mcs.spigot.config.base.yaml;

import java.io.File;

import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.config.Migration;

import com.syntaxphoenix.syntaxapi.config.yaml.YamlConfig;
import com.syntaxphoenix.syntaxapi.config.yaml.YamlConfigSection;

public abstract class YamlConfigBase extends ConfigBase<YamlConfig, YamlConfigSection> {
    
    protected YamlConfig config = new YamlConfig();

    public YamlConfigBase(File file, Class<? extends Migration> clazz, int latestVersion) {
        super(file, clazz, latestVersion);
    }

    @Override
    public YamlConfig getConfig() {
        return config;
    }

    @Override
    public YamlConfigSection getStorage() {
        return config;
    }

}
