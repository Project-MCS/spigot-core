package org.playuniverse.minecraft.mcs.spigot.module;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.playuniverse.minecraft.mcs.spigot.command.BukkitSource;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventExecutor;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventManager;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

import com.syntaxphoenix.avinity.command.CommandManager;
import com.syntaxphoenix.avinity.command.node.RootNode;
import com.syntaxphoenix.avinity.module.Module;
import com.syntaxphoenix.avinity.module.ModuleWrapper;
import com.syntaxphoenix.avinity.module.event.ModuleDisableEvent;
import com.syntaxphoenix.syntaxapi.event.Event;
import com.syntaxphoenix.syntaxapi.event.EventExecutor;
import com.syntaxphoenix.syntaxapi.event.EventHandler;
import com.syntaxphoenix.syntaxapi.event.EventListener;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.service.ServiceManager;
import com.syntaxphoenix.syntaxapi.utils.java.Collect;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public abstract class SafeModuleListener implements EventListener {

    protected final Container<ClassLookupProvider> provider;

    protected final ServiceManager service;

    protected final CommandManager<BukkitSource> command;

    protected final BukkitEventManager bukkitEvent;
    protected final EventManager event;

    protected final ILogger logger;

    public SafeModuleListener(ILogger logger, Container<ClassLookupProvider> provider, CommandManager<BukkitSource> command,
        EventManager event, BukkitEventManager bukkitEvent, ServiceManager service) {
        this.event = event;
        this.logger = logger;
        this.command = command;
        this.service = service;
        this.provider = provider;
        this.bukkitEvent = bukkitEvent;
        event.registerEvents(this);
    }

    @EventHandler
    public void onModuleDisable(ModuleDisableEvent event) {
        ModuleWrapper<?> wrapper = event.getWrapper();

        List<Class<? extends EventListener>> owners = this.event.getOwnerClasses();
        int size = owners.size();
        for (int index = 0; index < size; index++) {
            if (wrapper.isFromModule(owners.get(index))) {
                continue;
            }
            owners.remove(index);
            index--;
            size--;
        }

        owners.stream().forEach(clazz -> this.event.unregisterEvents(clazz));
        List<Class<? extends Event>> events = this.event.getEvents().stream().filter(clazz -> isFromPlugin(wrapper, clazz))
            .collect(Collectors.toList());
        this.event.unregisterExecutors(events.stream().collect(collectExecutor()));
        events.forEach(clazz -> this.event.unregisterEvent(clazz));

        owners = this.bukkitEvent.getOwnerClasses();
        size = owners.size();
        for (int index = 0; index < size; index++) {
            if (wrapper.isFromModule(owners.get(index))) {
                continue;
            }
            owners.remove(index);
            index--;
            size--;
        }

        owners.stream().forEach(clazz -> this.bukkitEvent.unregisterEvents(clazz));
        List<Class<? extends org.bukkit.event.Event>> discordEvents = this.bukkitEvent.getEvents().stream()
            .filter(clazz -> isFromPlugin(wrapper, clazz)).collect(Collectors.toList());
        this.bukkitEvent.unregisterExecutors(discordEvents.stream().collect(collectBukkitExecutor()));
        discordEvents.forEach(clazz -> this.bukkitEvent.unregisterEvent(clazz));

        service.getContainers().stream().filter(service -> isFromPlugin(wrapper, service.getOwner()))
            .forEach(container -> service.unsubscribe(container));
        service.getServices().stream().filter(service -> isFromPlugin(wrapper, service.getOwner()))
            .forEach(service -> this.service.unregister(service));

        Module plugin = wrapper.getModule();
        if (plugin instanceof ModuleIndicator) {
            String[] aliases = command.getAliases();
            for(String alias : aliases) {
                RootNode<BukkitSource> node = command.get(alias);
                if(node == null) {
                    continue;
                }
                
            }
        }

        ClassLoader loader = wrapper.getLoader();
        Package[] packages = loader.getDefinedPackages();
        ClassLookupProvider current = provider.get();
        for (int index = 0; index < packages.length; index++) {
            current.deleteByPackage(packages[index].getName());
        }

    }

    /*
     * Utilities
     */

    private Collector<Class<? extends Event>, List<EventExecutor>, List<EventExecutor>> collectExecutor() {
        return Collect.collectList((output, clazz) -> this.event.getExecutorsForEvent(clazz, true).stream().forEach(executor -> {
            if (output.contains(executor)) {
                return;
            }
            output.add(executor);
        }));
    }

    private Collector<Class<? extends org.bukkit.event.Event>, List<BukkitEventExecutor>, List<BukkitEventExecutor>> collectBukkitExecutor() {
        return Collect.collectList((output, clazz) -> this.bukkitEvent.getExecutorsForEvent(clazz, true).stream().forEach(executor -> {
            if (output.contains(executor)) {
                return;
            }
            output.add(executor);
        }));
    }

    public boolean isFromPlugin(ModuleWrapper<?> wrapper, Object object) {
        return wrapper.isFromModule(!(object instanceof Class) ? object.getClass() : (Class<?>) object);
    }

}
