package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.io.File;
import java.util.Optional;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Injector;
import org.playuniverse.minecraft.mcs.spigot.command.CommandState;
import org.playuniverse.minecraft.mcs.spigot.command.IPlugin;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftInfo;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.PluginNode;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.RootNode;
import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventManager;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.DefaultPlaceholderStore;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.PlaceholderStore;
import org.playuniverse.minecraft.mcs.spigot.plugin.extension.ICommandExtension;
import org.playuniverse.minecraft.mcs.spigot.plugin.extension.ISystemCommandExtension;

import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

public abstract class SpigotPlugin<P extends PluginBase<P>> extends Plugin implements IPlugin {

    @SuppressWarnings("unchecked")
    private static final Class<? extends ConfigBase<?, ?>>[] EMPTY_CONFIG = new Class[0];

    public static Optional<SpigotPlugin<?>> getAsOptional(String name) {
        return Optional.ofNullable(get(name));
    }

    public static SpigotPlugin<?> get(String name) {
        return getByWrapper(SpigotCore.get().getPluginManager().getPlugin(name));
    }

    public static Optional<SpigotPlugin<?>> getByWrapperAsOptional(PluginWrapper wrapper) {
        return Optional.ofNullable(getByWrapper(wrapper));
    }

    public static SpigotPlugin<?> getByWrapper(PluginWrapper wrapper) {
        if (wrapper == null) {
            return null;
        }
        Plugin plugin = wrapper.getPlugin();
        if (plugin instanceof SpigotPlugin) {
            return (SpigotPlugin<?>) plugin;
        }
        return null;
    }

    private final File dataLocation;
    private final ILogger logger;

    private final DefaultPlaceholderStore placeholders = new DefaultPlaceholderStore();

    public SpigotPlugin(PluginWrapper wrapper, File dataLocation) {
        super(wrapper);
        this.dataLocation = dataLocation;
        Files.createFolder(dataLocation);
        this.logger = new PluginLogger(getBase().getPluginLogger(), this);
    }

    @Override
    public final String getId() {
        return wrapper.getPluginId();
    }

    public String getPrefix() {
        return "[" + getId() + "]";
    }

    public abstract P getBase();

    public final ILogger getLogger() {
        return logger;
    }

    public final File getDataLocation() {
        return dataLocation;
    }

    public Class<? extends ConfigBase<?, ?>>[] getConfigurations() {
        return EMPTY_CONFIG;
    }

    @Override
    public final void start() {
        logger.log("Starting...");
        logger.log("Loading logic...");
        onLoad();
        logger.log("Loading configs...");
        ConfigBase.ACCESS.load(wrapper);
        logger.log("Starting logic...");
        onStart();
        logger.log("Setting up injections...");
        getBase().getInjections().setup();
        logger.log("Registering commands... (Plugin commands) [0 / 2]");
        registerPluginCommands();
        logger.log("Registering commands... (System commands) [1 / 2]");
        registerSystemCommands();
        logger.log("Successfully started!");
    }
    
    private final void registerPluginCommands() {
        int[] info = ICommandExtension.register(this);
        logger.log("Registered Plugin commands (" + info[0] + " of " + info[1] + ")!");
    }
    
    private final void registerSystemCommands() {
        int[] info = ISystemCommandExtension.register(this);
        logger.log("Registered System commands (" + info[0] + " of " + info[1] + ")!");
    }

    @Override
    public final void stop() {
        logger.log("Stopping...");
        logger.log("Stopping logic...");
        onStop();
        logger.log("Unloading configs...");
        ConfigBase.ACCESS.unload(wrapper);
        logger.log("Unloading logic...");
        onUnload();
        logger.log("Successfully stopped!");
    }

    public final void ready() {
        logger.log("Readying...");
        onServerReady();
        logger.log("Successfully readied!");
    }

    @Override
    public final void delete() {
        logger.log("Deleting...");
        onDelete();
        logger.log("Successfully deleted!");
    }

    protected void onLoad() {}

    protected void onStart() {}

    protected void onServerReady() {}

    protected void onStop() {}

    protected void onUnload() {}

    protected void onDelete() {}

    public final BukkitEventManager getBukkitManager() {
        return getBase().getBukkitEventManager();
    }

    public final EventManager getGeneralManager() {
        return getBase().getEventManager();
    }

    public final CommandState register(RootNode<MinecraftInfo> command, String... aliases) {
        return getBase().getCommandManager().register(new PluginNode<>(this, command), aliases);
    }

    public final void register(Injector<?> injector) {
        getBase().register(injector);
    }

    public final <T> boolean inject(T object) {
        return getBase().getInjections().inject(object);
    }

    public final <T> boolean uninject(T object) {
        return getBase().getInjections().uninject(object);
    }

    public final PlaceholderStore getDefaultPlaceholders() {
        return placeholders;
    }

}
