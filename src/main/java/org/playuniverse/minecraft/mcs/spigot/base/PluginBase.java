package org.playuniverse.minecraft.mcs.spigot.base;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Commands;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Injections;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Injector;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inventory.GuiListener;
import org.playuniverse.minecraft.mcs.spigot.command.CommandManager;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftInfo;
import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.constant.Singleton;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventManager;
import org.playuniverse.minecraft.mcs.spigot.language.handler.BlockMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.ConsoleMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.EntityMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.PlayerMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.message.builder.ComponentMessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.language.message.builder.StringMessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.plugin.SafePluginManager;
import org.playuniverse.minecraft.mcs.spigot.utils.log.AbstractLogger;
import org.playuniverse.minecraft.mcs.spigot.utils.log.BukkitLogger;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.service.ServiceManager;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;
import com.syntaxphoenix.syntaxapi.random.Keys;

public abstract class PluginBase<P extends PluginBase<P>> extends JavaPlugin {

    public static final Keys KEYS = new Keys(73453345478693428L);

    public static <S extends PluginBase<S>> S get(Class<S> clazz) {
        return JavaPlugin.getPlugin(clazz);
    }

    /*
     * 
     */

    protected final Container<ClassLookupProvider> lookupProvider = Container.of();

    private org.bukkit.plugin.PluginManager bukkitManager;

    private ServiceManager serviceManager;
    private PluginManager pluginManager;

    private BukkitEventManager bukkitEventManager;
    private EventManager eventManager;
    private Injections injections;

    private CommandManager<MinecraftInfo> commandManager;

    private boolean init = false;

    private AbstractLogger<?> logger;

    protected final File directory;
    protected final File pluginDirectory;
    protected final File compatDirectory;

    /*
     * 
     */

    public PluginBase() {

        //
        // Initializing variables
        //
        this.directory = getDataFolder();
        this.pluginDirectory = new File(directory, "addons");
        this.compatDirectory = new File(directory, "compatability");

        //
        // Creating bot directory
        //

        if (!directory.exists()) {
            directory.mkdirs();
            return;
        }
        if (!directory.isDirectory() && !directory.delete()) {
            throw new RuntimeException("Can't delete 'directory' file -> directory has to be a folder");
        } else {
            directory.mkdirs();
        }

        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
            return;
        }
        if (!pluginDirectory.isDirectory() && !pluginDirectory.delete()) {
            throw new RuntimeException("Can't delete 'pluginDirectory' file -> directory has to be a folder");
        } else {
            pluginDirectory.mkdirs();
        }

        if (!compatDirectory.exists()) {
            compatDirectory.mkdirs();
            return;
        }
        if (!compatDirectory.isDirectory() && !compatDirectory.delete()) {
            throw new RuntimeException("Can't delete 'compatDirectory' file -> directory has to be a folder");
        } else {
            compatDirectory.mkdirs();
        }

    }

    /*
     * 
     */

    @Override
    public final void onLoad() {
        onLoadup();
    }

    @Override
    public final void onEnable() {
        initialize();
    }

    @Override
    public final void onDisable() {
        shutdown();
    }

    /*
     * 
     */

    public final File getDirectory() {
        return directory;
    }

    public final File getPluginDirectory() {
        return pluginDirectory;
    }

    public final File getCompatDirectory() {
        return compatDirectory;
    }

    /*
     * 
     */

    public final AbstractLogger<?> getPluginLogger() {
        return logger;
    }

    public final EventManager getEventManager() {
        return eventManager;
    }

    public final BukkitEventManager getBukkitEventManager() {
        return bukkitEventManager;
    }

    public final CommandManager<MinecraftInfo> getCommandManager() {
        return commandManager;
    }

    public final ServiceManager getServiceManager() {
        return serviceManager;
    }

    public final ClassLookupProvider getLookupProvider() {
        return lookupProvider.get();
    }

    public final PluginManager getPluginManager() {
        return pluginManager;
    }

    public final Injections getInjections() {
        return injections;
    }

    /*
     * 
     */

    public final void initialize() {

        if (init) {
            return;
        }
        init = true;

        //
        // Setup Lookup Provider
        //

        lookupProvider.replace(new ClassLookupProvider(provider -> {
            return;
        }));

        //
        // Creating logger
        //

        logger = createLogger();

        //
        // Hooking into shutdown
        //

        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

        //
        // Creating some services
        //

        eventManager = createEventManager(logger);
        bukkitEventManager = createBukkitEventManager(logger);

        commandManager = createCommandManager(logger);

        serviceManager = createServiceManager(logger);

        pluginManager = createPluginManager(pluginDirectory.toPath(), logger, lookupProvider, commandManager, eventManager,
            bukkitEventManager, serviceManager);

        bukkitManager = Bukkit.getPluginManager();
        injections = new Injections(lookupProvider.get());

        //
        // Registering Events
        //

        register(GuiListener.LISTENER);

        //
        // Register Injections and apply reflections
        //

        register(new Commands());
        injections.setup();
        
        //
        // Register default handler for registries
        //

        Singleton.Registries.MESSAGE_BUILDER.register(ComponentMessageBuilder.INSTANCE);
        Singleton.Registries.MESSAGE_BUILDER.register(StringMessageBuilder.INSTANCE);
        
        Singleton.Registries.MESSAGE_HANDLER.register(BlockMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(ConsoleMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(EntityMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(PlayerMessageHandler.INSTANCE);

        //
        // Running the startup of the actual bot logic
        //

        logger.log("Booting plugin...");

        onStartup();

        logger.log("Booted plugin successfully!");

        loadPlugins();

        logger.log("Plugin successfully started!");

        logger.log("Running post startup...");

        onStarted();

        logger.log("Post startup executed successfully!");

    }

    /*
     * 
     */

    public final void shutdown() {

        if (!init) {
            return;
        }
        init = false;

        //
        // Shutdown addons
        //

        pluginManager.stopPlugins();
        unloadPlugins();

        //
        // Shutdown the plugin logic
        //

        onShutdown();

        //
        // Shutdown base logic
        //

        bukkitEventManager.getHook().unregister();

        //
        // Uninject everything
        //

        injections.uninjectAll();
        injections.dispose();

        //
        // Shutdown reflections
        //

        lookupProvider.get().getReflection().clear();
        lookupProvider.replace(null);

        //
        // Flush registries
        //

        Singleton.Registries.flush();

        //
        // Shutdown the logger
        //

        logger.log("Goodbye!");
        logger.close();

    }

    /*
     * 
     */

    public void register(Injector<?> injector) {
        injections.getInjectorRegistry().register(injector);
    }

    public void unregister(Injector<?> injector) {
        injections.getInjectorRegistry().unregister(injector.getType());
    }

    /*
     * 
     */

    public void register(Listener listener) {
        bukkitManager.registerEvents(listener, this);
    }

    public void unregister(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    /*
     * 
     */

    protected final void unloadPlugins() {
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        if (plugins.isEmpty()) {
            return;
        }
        for (PluginWrapper plugin : plugins) {
            pluginManager.unloadPlugin(plugin.getPluginId());
        }
    }

    protected final void loadPlugins() {
        logger.log("Loading functionality addons...");
        try {
            pluginManager.loadPlugins();
        } catch (Throwable throwable) {
            logger.log(throwable);
        }
        int size = pluginManager.getResolvedPlugins().size();
        logger.log("Loaded " + size + " addons to add functionality!");
        if (size != 0) {
            PluginWrapper[] wrappers = pluginManager.getPlugins().stream()
                .filter(wrapper -> wrapper.getPluginState() == PluginState.DISABLED).toArray(PluginWrapper[]::new);
            if (wrappers.length != 0) {
                logger.log(LogTypeId.ERROR, "Some plugins failed to load...");
                logger.log(LogTypeId.ERROR, "");
                for (int index = 0; index < wrappers.length; index++) {
                    PluginWrapper wrapper = wrappers[index];
                    logger.log(LogTypeId.ERROR, "===============================================");
                    logger.log(LogTypeId.ERROR, "");
                    logger.log(LogTypeId.ERROR, "Addon '" + wrapper.getPluginId() + "' by " + wrapper.getDescriptor().getProvider());
                    logger.log(LogTypeId.ERROR, "");
                    logger.log(LogTypeId.ERROR, "-----------------------------------------------");
                    logger.log(LogTypeId.ERROR, wrapper.getFailedException());
                    logger.log(LogTypeId.ERROR, "===============================================");
                    if (index + 1 != wrappers.length) {
                        logger.log(LogTypeId.ERROR, "");
                        logger.log(LogTypeId.ERROR, "");
                    }
                }
                logger.log(LogTypeId.ERROR, "");
                logger.log(LogTypeId.ERROR, "Hope you can fix those soon!");
            }
        }
        if (size != 0) {
            logger.log("Enabling functionality addons...");
            pluginManager.startPlugins();
            size = pluginManager.getStartedPlugins().size();
            logger.log("Enabled " + size + " addons to add functionality!");
            PluginWrapper[] wrappers = pluginManager.getPlugins().stream().filter(wrapper -> wrapper.getPluginState() == PluginState.FAILED)
                .toArray(PluginWrapper[]::new);
            if (wrappers.length != 0) {
                logger.log(LogTypeId.ERROR, "Some plugins failed to start...");
                logger.log(LogTypeId.ERROR, "");
                for (int index = 0; index < wrappers.length; index++) {
                    PluginWrapper wrapper = wrappers[index];
                    logger.log(LogTypeId.ERROR, "===============================================");
                    logger.log(LogTypeId.ERROR, "");
                    logger.log(LogTypeId.ERROR, "Addon '" + wrapper.getPluginId() + "' by " + wrapper.getDescriptor().getProvider());
                    logger.log(LogTypeId.ERROR, "");
                    logger.log(LogTypeId.ERROR, "-----------------------------------------------");
                    logger.log(LogTypeId.ERROR, wrapper.getFailedException());
                    logger.log(LogTypeId.ERROR, "===============================================");
                    if (index + 1 != wrappers.length) {
                        logger.log(LogTypeId.ERROR, "");
                        logger.log(LogTypeId.ERROR, "");
                    }
                }
                logger.log(LogTypeId.ERROR, "");
                logger.log(LogTypeId.ERROR, "Hope you can fix those soon!");
            }
        }
    }

    /*
     * 
     */

    /*
     * 
     * 
     * 
     */

    protected AbstractLogger<?> createLogger() {
        return new BukkitLogger(getLookupProvider()).setPrefix(getName());
    }

    protected EventManager createEventManager(ILogger logger) {
        return new EventManager(logger);
    }

    protected BukkitEventManager createBukkitEventManager(ILogger logger) {
        return new BukkitEventManager(logger);
    }

    protected ServiceManager createServiceManager(ILogger logger) {
        return new ServiceManager(logger);
    }

    protected CommandManager<MinecraftInfo> createCommandManager(ILogger logger) {
        return new CommandManager<>();
    }

    protected PluginManager createPluginManager(Path path, ILogger logger, Container<ClassLookupProvider> provider,
        CommandManager<MinecraftInfo> commandManager, EventManager eventManager, BukkitEventManager discordEventManager,
        ServiceManager serviceManager) {
        return new SafePluginManager(path, logger, provider, commandManager, eventManager, discordEventManager, serviceManager);
    }

    /*
     * 
     */

    public void createConfigs(ArrayList<Class<? extends ConfigBase<?, ?>>> list) {}

    protected abstract void onLoadup();

    protected abstract void onStartup();

    protected abstract void onStarted();

    protected abstract void onShutdown();

}
