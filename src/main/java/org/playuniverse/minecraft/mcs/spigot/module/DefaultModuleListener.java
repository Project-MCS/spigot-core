package org.playuniverse.minecraft.mcs.spigot.module;

import org.playuniverse.minecraft.mcs.spigot.command.BukkitSource;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventManager;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

import com.syntaxphoenix.avinity.command.CommandManager;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.service.ServiceManager;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class DefaultModuleListener extends SafeModuleListener {

    public DefaultModuleListener(ILogger logger, Container<ClassLookupProvider> provider, CommandManager<BukkitSource> command,
        EventManager event, BukkitEventManager bukkitEvent, ServiceManager service) {
        super(logger, provider, command, event, bukkitEvent, service);
    }

}
