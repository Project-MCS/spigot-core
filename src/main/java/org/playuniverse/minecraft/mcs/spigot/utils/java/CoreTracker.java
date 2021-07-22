package org.playuniverse.minecraft.mcs.spigot.utils.java;

import java.util.Optional;

import org.pf4j.PluginManager;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.command.IPlugin;
import org.playuniverse.minecraft.mcs.spigot.plugin.SpigotPlugin;

import com.syntaxphoenix.syntaxapi.reflection.ClassCache;
import com.syntaxphoenix.syntaxapi.utils.java.Arrays;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class CoreTracker {

    private final static Container<PluginManager> CACHE = Container.of();

    private CoreTracker() {}

    public static Optional<Class<?>> getClassFromStack(int offset) {
        StackTraceElement element = getStack()[3 + offset];
        return element == null ? Optional.empty() : ClassCache.getOptionalClass(element.getClassName());
    }

    public static Optional<Class<?>> getCallerClass() {
        return getClassFromStack(1);
    }

    public static Optional<IPlugin> getCallerCommandPlugin() {
        return getCallerPlugin().map(plugin -> (IPlugin) plugin);
    }

    public static Optional<IPlugin> getCommandPlugin(Optional<Class<?>> option) {
        return getPlugin(option).map(plugin -> (IPlugin) plugin);
    }

    public static Optional<SpigotPlugin<?>> getCallerPlugin() {
        StackTraceElement[] elements = Arrays.subArray(StackTraceElement[]::new, getStack(), 3);
        for (StackTraceElement element : elements) {
            Optional<SpigotPlugin<?>> plugin = getPlugin(ClassCache.getOptionalClass(element.getClassName()));
            if (plugin.isPresent()) {
                return plugin;
            }
        }
        return Optional.empty();
    }

    public static Optional<SpigotPlugin<?>> getPlugin(Optional<Class<?>> option) {
        return option.map(clazz -> getPluginManager().whichPlugin(clazz)).map(SpigotPlugin::getByWrapper);
    }
    
    private static StackTraceElement[] getStack() {
        return new Throwable().getStackTrace();
    }

    private static PluginManager getPluginManager() {
        return CACHE.isPresent() ? CACHE.get() : CACHE.replace(SpigotCore.get().getPluginManager()).get();
    }

}