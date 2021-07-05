package org.playuniverse.minecraft.mcs.spigot.config.base.json;

import java.io.File;

import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.config.Migration;

import com.syntaxphoenix.syntaxapi.config.json.JsonConfig;
import com.syntaxphoenix.syntaxapi.config.json.JsonConfigSection;

public abstract class JsonConfigBase extends ConfigBase<JsonConfig, JsonConfigSection> {
    
    protected JsonConfig config = new JsonConfig();

    public JsonConfigBase(File file, Class<? extends Migration> clazz, int latestVersion) {
        super(file, clazz, latestVersion);
    }

    @Override
    public JsonConfig getConfig() {
        return config;
    }

    @Override
    public JsonConfigSection getStorage() {
        return config;
    }

}
