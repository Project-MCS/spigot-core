package org.playuniverse.minecraft.mcs.spigot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;
import org.playuniverse.minecraft.mcs.spigot.command.listener.redirect.ManagerRedirect;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.CommandNode;
import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.config.config.AddonConfig;
import org.playuniverse.minecraft.mcs.spigot.config.config.DebugConfig;
import org.playuniverse.minecraft.mcs.spigot.helper.task.TaskHelper;
import org.playuniverse.minecraft.mcs.spigot.module.SpigotCoreModule;
import org.playuniverse.minecraft.mcs.spigot.utils.wait.Awaiter;

import com.syntaxphoenix.avinity.module.ModuleManager;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;
import com.syntaxphoenix.syntaxapi.utils.key.Namespace;

public class SpigotCore extends PluginBase<SpigotCore> {

    private static final Container<Namespace> NAMESPACE = Container.of();

    public static Namespace getNamespace() {
        return NAMESPACE.get();
    }

    public static SpigotCore get() {
        return get(SpigotCore.class);
    }

    private MinecraftCommand command;

    public SpigotCore() {
        super(SpigotCoreModule.class);
    }

    @Override
    protected void onLoadup() {
        NAMESPACE.replace(Namespace.of("system")).lock();
        TaskHelper.TASK.start();
        Awaiter.class.getClass(); // Initialize
    }

    @Override
    protected void onStartup() {
        getCommandManager().setGlobal("help");
        getInjections()
            .inject(command = new MinecraftCommand(new ManagerRedirect(getCommandManager(), this), this, "system", "sys", "core"));
        getCommandManager().register(new CommandNode<>("reload", context -> {
            ModuleManager<?> manager = getModuleManager();
            getPluginLogger().log("Reloading... (0 / 4)");
            manager.disableModules();
            getPluginLogger().log("Reloading... (1 / 4)");
            manager.unloadModules();
            System.gc();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            getPluginLogger().log("Reloading... (2 / 4)");
            get().loadPlugins();
            getPluginLogger().log("Reloading... (3 / 4)");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.closeInventory();
            }
            get().readyPlugins();
            getPluginLogger().log("Reloading... (4 / 4)");
            getPluginLogger().log("Reload complete!");
        }));
    }

    @Override
    protected void onStarted() {

    }

    @Override
    protected void onShutdown() {
        getInjections().uninject(command);
        TaskHelper.TASK.shutdown();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
    }

    @Override
    public void createConfigs(ArrayList<Class<? extends ConfigBase<?, ?>>> list) {
        list.add(AddonConfig.class);
        list.add(DebugConfig.class);
    }

}
