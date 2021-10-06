package org.playuniverse.minecraft.mcs.spigot.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Commands;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Injections;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Injector;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inventory.GuiListener;
import org.playuniverse.minecraft.mcs.spigot.command.CommandManager;
import org.playuniverse.minecraft.mcs.spigot.command.IModule;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftInfo;
import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.config.ConfigTimer;
import org.playuniverse.minecraft.mcs.spigot.constant.Singleton;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventManager;
import org.playuniverse.minecraft.mcs.spigot.language.handler.BlockMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.ConsoleMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.EntityMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.PlayerMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.ProxiedMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.handler.RemoteConsoleMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.message.builder.ComponentMessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.language.message.builder.StringMessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.listener.ServerLoadListener;
import org.playuniverse.minecraft.mcs.spigot.module.DefaultModuleListener;
import org.playuniverse.minecraft.mcs.spigot.module.SafeModuleListener;
import org.playuniverse.minecraft.mcs.spigot.module.SpigotModule;
import org.playuniverse.minecraft.mcs.spigot.utils.java.JavaHelper;
import org.playuniverse.minecraft.mcs.spigot.utils.log.AbstractLogger;
import org.playuniverse.minecraft.mcs.spigot.utils.log.BukkitLogger;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

import com.syntaxphoenix.avinity.module.ModuleManager;
import com.syntaxphoenix.avinity.module.ModuleState;
import com.syntaxphoenix.avinity.module.ModuleWrapper;
import com.syntaxphoenix.avinity.module.util.DependencyVersion;
import com.syntaxphoenix.avinity.module.util.DependencyVersionParser;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.service.ServiceManager;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;
import com.syntaxphoenix.syntaxapi.version.Version;
import com.syntaxphoenix.syntaxapi.random.Keys;

public abstract class PluginBase<P extends PluginBase<P>> extends JavaPlugin implements IModule {

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
    private ModuleManager<?> moduleManager;

    private BukkitEventManager bukkitEventManager;
    private EventManager eventManager;
    private Injections injections;

    private CommandManager<MinecraftInfo> commandManager;

    private boolean init = false;

    private AbstractLogger<?> logger;

    protected final File directory;
    protected final File pluginDirectory;
    protected final File compatDirectory;

    private final Class<? extends SpigotModule<P>> moduleClass;
    private final DependencyVersion version;

    /*
     * 
     */

    public PluginBase(Class<? extends SpigotModule<P>> moduleClass) {

        //
        // Initializing variables
        //

        this.moduleClass = moduleClass;
        this.directory = getDataFolder();
        this.pluginDirectory = new File(directory, "addons");
        this.compatDirectory = new File(directory, "compatability");

        this.version = DependencyVersionParser.INSTANCE.analyze(getDescription().getVersion());

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

    @Override
    public final String getId() {
        return getName();
    }

    public final Version getVersion() {
        return version;
    }

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

    public final ModuleManager<?> getModuleManager() {
        return moduleManager;
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

        bukkitManager = Bukkit.getPluginManager();
        injections = new Injections(lookupProvider.get());

        //
        // Create ModuleListener for disabling modules
        //

        createModuleListener(logger, lookupProvider, commandManager, eventManager, bukkitEventManager, serviceManager);

        //
        // Loading configs before creation of PluginManager
        //

        ConfigBase.ACCESS.getClass();
        ConfigTimer.TIMER.waitForNextCycle();

        //
        // Creating module manager
        //

        moduleManager = createModuleManager(moduleClass, eventManager, version);

        //
        // Load system extensions
        //

        Optional<String> extensionData = JavaHelper.getResourceStringFromJar(getFile(), "extensions.json");
        if (extensionData.isPresent()) {
            moduleManager.getExtensionManager().loadSystemExtensions(extensionData.get());
        }

        //
        // Registering Events
        //

        register(GuiListener.LISTENER);
        register(new ServerLoadListener(this));

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

        Singleton.Registries.MESSAGE_HANDLER.register(RemoteConsoleMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(ConsoleMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(ProxiedMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(PlayerMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(EntityMessageHandler.INSTANCE);
        Singleton.Registries.MESSAGE_HANDLER.register(BlockMessageHandler.INSTANCE);

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

        //
        // Setup Injections again because plugins possibly added more injectors
        //

        injections.setup();

        if (Bukkit.getWorlds().size() != 0) {
            logger.log("Server is already started!");

            readyPlugins();

            logger.log("Everything is ready and setup now!");
        }

    }

    /*
     * 
     */

    public final void shutdown() {

        if (!init) {
            return;
        }
        init = false;

        ConfigTimer.TIMER.shutdown();

        //
        // Shutdown addons
        //

        moduleManager.disableModules();
        moduleManager.unloadModules();

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

    @SuppressWarnings("unchecked")
    public final void loadPlugins() {
        final ModuleManager<SpigotModule<?>> moduleManager = (ModuleManager<SpigotModule<?>>) this.moduleManager;
        logger.log("Loading functionality addons...");
        try {
            moduleManager.loadModules(pluginDirectory);
        } catch (Throwable throwable) {
            logger.log(throwable);
        }
        int size = moduleManager.getModules(ModuleState.CREATED).size();
        logger.log("Loaded " + size + " addons to add functionality!");
        if (size != 0) {
            ArrayList<ModuleWrapper<SpigotModule<?>>> wrappers = moduleManager.getModules(ModuleState.FAILED_LOAD);
            if (wrappers.size() != 0) {
                logger.log(LogTypeId.ERROR, "Some plugins failed to load...");
                logger.log(LogTypeId.ERROR, "");
                for (int index = 0; index < wrappers.size(); index++) {
                    ModuleWrapper<?> wrapper = wrappers.get(index);
                    logger.log(LogTypeId.ERROR, "===============================================");
                    logger.log(LogTypeId.ERROR, "");
                    logger.log(LogTypeId.ERROR, "Addon '" + wrapper.getId() + "' by " + wrapper.getDescription().getAuthors());
                    logger.log(LogTypeId.ERROR, "");
                    logger.log(LogTypeId.ERROR, "-----------------------------------------------");
                    logger.log(LogTypeId.ERROR, wrapper.getFailedException());
                    logger.log(LogTypeId.ERROR, "===============================================");
                    if (index + 1 != wrappers.size()) {
                        logger.log(LogTypeId.ERROR, "");
                        logger.log(LogTypeId.ERROR, "");
                    }
                }
                logger.log(LogTypeId.ERROR, "");
                logger.log(LogTypeId.ERROR, "Hope you can fix those soon!");
            }
        }
        if (size != 0) {
            logger.log("Resolving loaded functionality addons...");
            try {
                moduleManager.resolveModules();
            } catch (Throwable throwable) {
                logger.log(throwable);
            }
            size = moduleManager.getModules(ModuleState.RESOLVED).size();
            logger.log("Loaded " + size + " addons to add functionality!");
            if (size != 0) {
                ArrayList<ModuleWrapper<SpigotModule<?>>> wrappers = moduleManager.getModules(ModuleState.FAILED_LOAD);
                if (wrappers.size() != 0) {
                    logger.log(LogTypeId.ERROR, "Some plugins failed to load...");
                    logger.log(LogTypeId.ERROR, "");
                    for (int index = 0; index < wrappers.size(); index++) {
                        ModuleWrapper<?> wrapper = wrappers.get(index);
                        logger.log(LogTypeId.ERROR, "===============================================");
                        logger.log(LogTypeId.ERROR, "");
                        logger.log(LogTypeId.ERROR, "Addon '" + wrapper.getId() + "' by " + wrapper.getDescription().getAuthors());
                        logger.log(LogTypeId.ERROR, "");
                        logger.log(LogTypeId.ERROR, "-----------------------------------------------");
                        logger.log(LogTypeId.ERROR, wrapper.getFailedException());
                        logger.log(LogTypeId.ERROR, "===============================================");
                        if (index + 1 != wrappers.size()) {
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
                moduleManager.enableModules();
                size = moduleManager.getModules(ModuleState.ENABLED).size();
                logger.log("Enabled " + size + " addons to add functionality!");
                ArrayList<ModuleWrapper<SpigotModule<?>>> wrappers = moduleManager.getModules(ModuleState.FAILED_START);
                if (wrappers.size() != 0) {
                    logger.log(LogTypeId.ERROR, "Some plugins failed to start...");
                    logger.log(LogTypeId.ERROR, "");
                    for (int index = 0; index < wrappers.size(); index++) {
                        ModuleWrapper<?> wrapper = wrappers.get(index);
                        logger.log(LogTypeId.ERROR, "===============================================");
                        logger.log(LogTypeId.ERROR, "");
                        logger.log(LogTypeId.ERROR, "Addon '" + wrapper.getId() + "' by " + wrapper.getDescription().getAuthors());
                        logger.log(LogTypeId.ERROR, "");
                        logger.log(LogTypeId.ERROR, "-----------------------------------------------");
                        logger.log(LogTypeId.ERROR, wrapper.getFailedException());
                        logger.log(LogTypeId.ERROR, "===============================================");
                        if (index + 1 != wrappers.size()) {
                            logger.log(LogTypeId.ERROR, "");
                            logger.log(LogTypeId.ERROR, "");
                        }
                    }
                    logger.log(LogTypeId.ERROR, "");
                    logger.log(LogTypeId.ERROR, "Hope you can fix those soon!");
                }
            }
        }
    }

    public final void readyPlugins() {
        ILogger logger = getPluginLogger();
        HashMap<ModuleWrapper<?>, Throwable> map = new HashMap<>();
        logger.log("Readying up plugins...");
        int amount = 0;
        ModuleManager<?> moduleManager = getModuleManager();
        for (ModuleWrapper<?> wrapper : moduleManager.getModules(ModuleState.ENABLED)) {
            SpigotModule<?> plugin = SpigotModule.getByWrapper(wrapper);
            if (plugin == null) {
                continue;
            }
            try {
                plugin.ready();
                amount++;
            } catch (Throwable throwable) {
                map.put(wrapper, throwable);
            }
        }
        logger.log("Readied up " + amount + " plugins!");
        if (map.isEmpty()) {
            return;
        }
        logger.log(LogTypeId.ERROR, "Some plugins failed to ready up and will be unloaded...");
        logger.log(LogTypeId.ERROR, "");
        ModuleWrapper<?>[] wrappers = map.keySet().toArray(ModuleWrapper<?>[]::new);
        for (int index = 0; index < wrappers.length; index++) {
            ModuleWrapper<?> wrapper = wrappers[index];
            logger.log(LogTypeId.ERROR, "===============================================");
            logger.log(LogTypeId.ERROR, "");
            logger.log(LogTypeId.ERROR, "Addon '" + wrapper.getId() + "' by " + wrapper.getDescription().getAuthors());
            logger.log(LogTypeId.ERROR, "");
            logger.log(LogTypeId.ERROR, "-----------------------------------------------");
            logger.log(LogTypeId.ERROR, map.get(wrapper));
            logger.log(LogTypeId.ERROR, "===============================================");
            if (index + 1 != wrappers.length) {
                logger.log(LogTypeId.ERROR, "");
                logger.log(LogTypeId.ERROR, "");
            }
            try {
                moduleManager.unloadModule(wrapper.getId());
            } catch (Throwable throwable) {
                if (!logger.getState().extendedInfo()) {
                    continue;
                }
                logger.log(LogTypeId.ERROR, "");
                logger.log(LogTypeId.ERROR, "Failed to unload Addon '" + wrapper.getId() + "' by " + wrapper.getDescription().getAuthors());
                logger.log(LogTypeId.ERROR, "");
                logger.log(LogTypeId.ERROR, "-----------------------------------------------");
                logger.log(LogTypeId.ERROR, throwable);
                logger.log(LogTypeId.ERROR, "===============================================");
            }
        }
        logger.log(LogTypeId.ERROR, "");
        logger.log(LogTypeId.ERROR, "Hope you can fix those soon!");
    }

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

    protected ModuleManager<?> createModuleManager(Class<? extends SpigotModule<P>> clazz, EventManager eventManager,
        DependencyVersion version) {
        return new ModuleManager<>(clazz, eventManager, version);
    }

    protected SafeModuleListener createModuleListener(ILogger logger, Container<ClassLookupProvider> provider,
        CommandManager<MinecraftInfo> commandManager, EventManager eventManager, BukkitEventManager discordEventManager,
        ServiceManager serviceManager) {
        return new DefaultModuleListener(logger, provider, commandManager, eventManager, discordEventManager, serviceManager);
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
