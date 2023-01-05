package org.playuniverse.minecraft.mcs.spigot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.BukkitCommand;
import org.playuniverse.minecraft.mcs.spigot.command.BukkitSource;
import org.playuniverse.minecraft.mcs.spigot.config.ConfigBase;
import org.playuniverse.minecraft.mcs.spigot.config.config.AddonConfig;
import org.playuniverse.minecraft.mcs.spigot.config.config.DebugConfig;
import org.playuniverse.minecraft.mcs.spigot.helper.task.TaskHelper;
import org.playuniverse.minecraft.mcs.spigot.module.SpigotCoreModule;
import org.playuniverse.minecraft.mcs.spigot.utils.wait.Awaiter;

import com.syntaxphoenix.avinity.command.connection.ManagerConnection;
import com.syntaxphoenix.avinity.command.node.Root;
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

    private BukkitCommand command;

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
        getInjections().inject(
            command = new BukkitCommand(this, new ManagerConnection<>(getCommandManager()), "system", getModuleId(), "sys", "core"));
        getCommandManager().register(Root.<BukkitSource>of("reload").execute(context -> {
            ModuleManager<?> manager = getModuleManager();
            BukkitSource source = context.getSource();
            if(!source.isConsole()) {
                source.getSender().sendMessage("Reloading... (0 / 4)");
            }
            getPluginLogger().log("Reloading... (0 / 4)");
            manager.disableModules();
            if(!source.isConsole()) {
                source.getSender().sendMessage("Reloading... (1 / 4)");
            }
            getPluginLogger().log("Reloading... (1 / 4)");
            manager.unloadModules();
            System.gc();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            if(!source.isConsole()) {
                source.getSender().sendMessage("Reloading... (2 / 4)");
            }
            getPluginLogger().log("Reloading... (2 / 4)");
            get().loadPlugins();
            if(!source.isConsole()) {
                source.getSender().sendMessage("Reloading... (3 / 4)");
            }
            getPluginLogger().log("Reloading... (3 / 4)");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.closeInventory();
            }
            get().readyPlugins();
            if(!source.isConsole()) {
                source.getSender().sendMessage("Reloading... (4 / 4)");
                source.getSender().sendMessage("Reload complete!");
            }
            getPluginLogger().log("Reloading... (4 / 4)");
            getPluginLogger().log("Reload complete!");
        }).build());
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
