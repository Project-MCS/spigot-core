package org.playuniverse.minecraft.vcompat.reflection;

import org.playuniverse.minecraft.vcompat.reflection.provider.VersionControlImpl;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public abstract class VersionControl {

    public static final String CLASSPATH = "org.playuniverse.minecraft.vcompat.reflection.provider.VersionControlImpl";
    public static Container<VersionControl> CURRENT = Container.of();

    public static VersionControl get() {
        if (CURRENT.isPresent()) {
            return CURRENT.get();
        }
        return CURRENT.replace(new VersionControlImpl()).lock().get();
    }

    protected final DataProvider dataProvider = new DataProvider(this);

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public abstract ToolProvider<?> getToolProvider();

    public abstract EntityProvider<?> getEntityProvider();

    public abstract PlayerProvider<?> getPlayerProvider();

    public abstract TextureProvider<?> getTextureProvider();

    public abstract BukkitConversion<?> getBukkitConversion();
    
    public void shutdown() {}

}