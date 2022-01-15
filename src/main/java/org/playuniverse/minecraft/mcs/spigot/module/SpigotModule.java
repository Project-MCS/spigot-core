package org.playuniverse.minecraft.mcs.spigot.module;

import java.util.Optional;

import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inject.Injector;
import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.event.BukkitEventManager;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.DefaultPlaceholderStore;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.PlaceholderStore;
import org.playuniverse.minecraft.mcs.spigot.module.extension.ICommandExtension;
import org.playuniverse.minecraft.mcs.spigot.module.extension.IListenerExtension;
import org.playuniverse.minecraft.mcs.spigot.module.extension.ISystemCommandExtension;

import com.syntaxphoenix.avinity.module.Module;
import com.syntaxphoenix.avinity.module.ModuleWrapper;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

public abstract class SpigotModule<P extends PluginBase<P>> extends Module implements ModuleIndicator {

    @SuppressWarnings("unchecked")
    private static final Class<? extends ConfigBase<?, ?>>[] EMPTY_CONFIG = new Class[0];

    public static Optional<SpigotModule<?>> getAsOptional(String name) {
        return Optional.ofNullable(get(name));
    }

    public static SpigotModule<?> get(String name) {
        return SpigotCore.get().getModuleManager().getModule(name).map(SpigotModule::getByWrapper).orElse(null);
    }

    public static Optional<SpigotModule<?>> getByWrapperAsOptional(ModuleWrapper<?> wrapper) {
        return Optional.ofNullable(getByWrapper(wrapper));
    }

    public static SpigotModule<?> getByWrapper(ModuleWrapper<?> wrapper) {
        if (wrapper == null) {
            return null;
        }
        Module plugin = wrapper.getModule();
        if (plugin instanceof SpigotModule) {
            return (SpigotModule<?>) plugin;
        }
        return null;
    }

    private final ILogger logger;

    private final DefaultPlaceholderStore placeholders = new DefaultPlaceholderStore();

    public SpigotModule() {
        this.logger = new ModuleLogger(getBase().getPluginLogger(), this);
    }
    
    @Override
    public final String getModuleId() {
        return getId();
    }

    public String getPrefix() {
        return "[" + getId() + "]";
    }

    public abstract P getBase();

    public final ILogger getLogger() {
        return logger;
    }

    public Class<? extends ConfigBase<?, ?>>[] getConfigurations() {
        return EMPTY_CONFIG;
    }

    @Override
    public final void enable() {
        logger.log("Starting...");
        Files.createFolder(getDataLocation());
        logger.log("Loading logic...");
        onLoad();
        logger.log("Loading configs...");
        ConfigBase.ACCESS.load(getWrapper());
        logger.log("Starting logic...");
        onStart();
        logger.log("Setting up injections...");
        getBase().getInjections().setup();
        logger.log("Registering commands... (Plugin commands) [0 / 2]");
        registerPluginCommands();
        logger.log("Registering commands... (System commands) [1 / 2]");
        registerSystemCommands();
        logger.log("Regisering listeners...");
        registerListeners();
        
        logger.log("Successfully started!");
    }

    private final void registerListeners() {
        int[] info = IListenerExtension.register(this);
        logger.log("Registered listeners (" + info[0] + " of " + info[1] + ")!");
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
    public final void disable() {
        logger.log("Stopping...");
        logger.log("Stopping logic...");
        onStop();
        logger.log("Unloading configs...");
        ConfigBase.ACCESS.unload(getWrapper());
        logger.log("Unloading logic...");
        onUnload();
        logger.log("Successfully stopped!");
    }

    public final void ready() {
        logger.log("Readying...");
        onServerReady();
        logger.log("Successfully readied!");
    }

    protected void onLoad() {}

    protected void onStart() {}

    protected void onServerReady() {}

    protected void onStop() {}

    protected void onUnload() {}

    public final BukkitEventManager getBukkitManager() {
        return getBase().getBukkitEventManager();
    }

    public final EventManager getGeneralManager() {
        return getBase().getEventManager();
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
