package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.pf4j.DefaultPluginManager;
import org.pf4j.Plugin;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.command.CommandManager;
import org.playuniverse.minecraft.mcs.spigot.command.IPlugin;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftInfo;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventExecutor;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventManager;
import org.playuniverse.minecraft.mcs.spigot.utils.java.ReflectionProvider;
import org.playuniverse.minecraft.mcs.spigot.utils.wait.Awaiter;

import com.syntaxphoenix.syntaxapi.event.Event;
import com.syntaxphoenix.syntaxapi.event.EventExecutor;
import com.syntaxphoenix.syntaxapi.event.EventListener;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.service.ServiceManager;
import com.syntaxphoenix.syntaxapi.utils.java.Collect;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public class SafePluginManager extends DefaultPluginManager implements PluginStateListener {

    private final Container<ReflectionProvider> provider;

    private final ServiceManager service;

    private final CommandManager<MinecraftInfo> command;

    private final BukkitEventManager bukkitEvent;
    private final EventManager event;

    private final ILogger logger;

    public SafePluginManager(ILogger logger, Container<ReflectionProvider> provider, CommandManager<MinecraftInfo> command,
        EventManager event, BukkitEventManager bukkitEvent, ServiceManager service) {
        super();
        this.provider = provider;
        this.event = event;
        this.logger = logger;
        this.service = service;
        this.command = command;
        this.bukkitEvent = bukkitEvent;
        super.addPluginStateListener(this);
    }

    public SafePluginManager(Path pluginsRoot, ILogger logger, Container<ReflectionProvider> provider,
        CommandManager<MinecraftInfo> command, EventManager event, BukkitEventManager bukkitEvent, ServiceManager service) {
        super(pluginsRoot);
        this.provider = provider;
        this.event = event;
        this.logger = logger;
        this.service = service;
        this.command = command;
        this.bukkitEvent = bukkitEvent;
        super.addPluginStateListener(this);
    }

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new YamlPluginDescriptorFinder();
    }

    /*
     * Getter
     */

    public ReflectionProvider getProvider() {
        return provider.get();
    }

    public ServiceManager getServiceManager() {
        return service;
    }

    public BukkitEventManager getDiscordEventManager() {
        return bukkitEvent;
    }

    public EventManager getEventManager() {
        return event;
    }

    public ILogger getLogger() {
        return logger;
    }

    /*
     * Plugin Listener
     */

    @Override
    public synchronized void addPluginStateListener(PluginStateListener listener) {
        return;
    }

    @Override
    public synchronized void removePluginStateListener(PluginStateListener listener) {
        return;
    }

    @Override
    public void pluginStateChanged(PluginStateEvent event) {

        if (event.getPluginState() == PluginState.STARTED) {
            Awaiter.of(this.event.call(new PluginEnableEvent(this, event.getPlugin()))).await();
            return;
        }

        if (event.getOldState() != PluginState.STARTED) {
            return;
        }
        switch (event.getPluginState()) {
        case STOPPED:
        case DISABLED:
            break;
        default:
            return;
        }

        PluginWrapper wrapper = event.getPlugin();

        Awaiter.of(this.event.call(new PluginDisableEvent(this, wrapper))).await();

        List<Class<? extends EventListener>> owners = this.event.getOwnerClasses();
        int size = owners.size();
        for (int index = 0; index < size; index++) {
            if (wrapper.equals(whichPlugin(owners.get(index)))) {
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
            if (wrapper.equals(whichPlugin(owners.get(index)))) {
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

        Plugin plugin = wrapper.getPlugin();
        if (plugin instanceof IPlugin) {
            for (String alias : command.getAliases((IPlugin) plugin)) {
                command.unregisterCommand(alias);
            }
        }

        ClassLoader loader = wrapper.getPluginClassLoader();
        Package[] packages = loader.getDefinedPackages();
        ReflectionProvider current = provider.get();
        for (int index = 0; index < packages.length; index++) {
            current.delete(packages[index].getName());
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

    public boolean isFromPlugin(PluginWrapper wrapper, Object object) {
        return wrapper.equals(whichPlugin(!(object instanceof Class) ? object.getClass() : (Class<?>) object));
    }

}
