package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.pf4j.Plugin;
import org.pf4j.PluginFactory;
import org.pf4j.PluginWrapper;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

public class SafePluginFactory implements PluginFactory {

    private final ILogger logger;

    public SafePluginFactory(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public Plugin create(PluginWrapper pluginWrapper) {
        String pluginClassName = pluginWrapper.getDescriptor().getPluginClass();

        Class<?> pluginClass;
        try {
            pluginClass = pluginWrapper.getPluginClassLoader().loadClass(pluginClassName);
        } catch (ClassNotFoundException e) {
            logger.log(LogTypeId.ERROR, e);
            return null;
        }

        // once we have the class, we can do some checks on it to ensure
        // that it is a valid implementation of a plugin.
        int modifiers = pluginClass.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || (!SpigotPlugin.class.isAssignableFrom(pluginClass))) {
            logger.log(LogTypeId.ERROR, "The plugin class '" + pluginClassName + "' is not valid!");
            return null;
        }

        // Load plugin data folder
        File folder = pluginWrapper.getPluginPath().toFile().getParentFile();
        if (folder == null || !folder.isDirectory()) {
            logger.log(LogTypeId.ERROR, "Can't retrieve plugin containing folder!");
            return null;
        }

        // create the plugin instance
        try {
            Constructor<?> constructor = pluginClass.getConstructor(PluginWrapper.class, File.class);
            return (Plugin) constructor.newInstance(pluginWrapper, new File(folder, pluginWrapper.getPluginId()));
        } catch (Exception e) {
            logger.log(LogTypeId.ERROR, e);
        }

        return null;

    }

}
