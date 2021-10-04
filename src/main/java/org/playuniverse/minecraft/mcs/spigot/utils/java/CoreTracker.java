package org.playuniverse.minecraft.mcs.spigot.utils.java;

import java.util.Optional;

import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.command.IModule;
import org.playuniverse.minecraft.mcs.spigot.module.SpigotModule;

import com.syntaxphoenix.avinity.module.ModuleManager;
import com.syntaxphoenix.syntaxapi.reflection.ClassCache;
import com.syntaxphoenix.syntaxapi.utils.java.Arrays;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class CoreTracker {

    private final static Container<ModuleManager<?>> CACHE = Container.of();

    private CoreTracker() {}

    public static Optional<Class<?>> getClassFromStack(int offset) {
        StackTraceElement element = getStack()[3 + offset];
        return element == null ? Optional.empty() : ClassCache.getOptionalClass(element.getClassName());
    }

    public static Optional<Class<?>> getCallerClass() {
        return getClassFromStack(1);
    }

    public static Optional<IModule> getCallerCommandPlugin() {
        return getCallerPlugin().map(plugin -> (IModule) plugin);
    }

    public static Optional<IModule> getCommandPlugin(Optional<Class<?>> option) {
        return getPlugin(option).map(plugin -> (IModule) plugin);
    }

    public static Optional<SpigotModule<?>> getCallerPlugin() {
        StackTraceElement[] elements = Arrays.subArray(StackTraceElement[]::new, getStack(), 3);
        for (StackTraceElement element : elements) {
            Optional<SpigotModule<?>> plugin = getPlugin(ClassCache.getOptionalClass(element.getClassName()));
            if (plugin.isPresent()) {
                return plugin;
            }
        }
        return Optional.empty();
    }

    public static Optional<SpigotModule<?>> getPlugin(Optional<Class<?>> option) {
        return option.flatMap(clazz -> getModuleManager().getModuleForClass(clazz)).map(SpigotModule::getByWrapper);
    }

    private static StackTraceElement[] getStack() {
        return new Throwable().getStackTrace();
    }

    private static ModuleManager<?> getModuleManager() {
        return CACHE.isPresent() ? CACHE.get() : CACHE.replace(SpigotCore.get().getModuleManager()).get();
    }

}