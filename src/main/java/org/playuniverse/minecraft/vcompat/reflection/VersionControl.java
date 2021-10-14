package org.playuniverse.minecraft.vcompat.reflection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.playuniverse.minecraft.vcompat.reflection.provider.VersionControlImpl;

import com.syntaxphoenix.syntaxapi.event.EventManager;
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

    protected final Properties properties = new Properties();
    protected final Container<EventManager> eventManager = Container.of();

    public VersionControl() {
        try (FileReader reader = new FileReader(new File("server.properties"))) {
            properties.load(reader);
        } catch (IOException e) {
            // Who cares
        }
    }

    public Properties getServerProperties() {
        return properties;
    }

    public final void setEventManager(EventManager eventManager) {
        if (this.eventManager.isPresent()) {
            return;
        }
        this.eventManager.replace(eventManager).lock();
    }

    public final Container<EventManager> getEventManager() {
        return eventManager;
    }

    public abstract void rehook();

    public abstract DataProvider<?> getDataProvider();

    public abstract ToolProvider<?> getToolProvider();

    public abstract EntityProvider<?> getEntityProvider();

    public abstract PlayerProvider<?> getPlayerProvider();

    public abstract TextureProvider<?> getTextureProvider();

    public abstract BukkitConversion<?> getBukkitConversion();

    public void shutdown() {}

}