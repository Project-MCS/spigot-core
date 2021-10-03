package org.playuniverse.minecraft.mcs.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.pf4j.PluginManager;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;
import org.playuniverse.minecraft.mcs.spigot.command.listener.redirect.ManagerRedirect;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.CommandNode;
import org.playuniverse.minecraft.mcs.spigot.helper.task.TaskHelper;

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

    @Override
    protected void onLoadup() {
        NAMESPACE.replace(Namespace.of("system")).lock();
        TaskHelper.TASK.start();
    }

    @Override
    protected void onStartup() {
        getCommandManager().setGlobal("help");
        getInjections()
            .inject(command = new MinecraftCommand(new ManagerRedirect(getCommandManager(), this), this, "system", "sys", "core"));
        getCommandManager().register(new CommandNode<>("reload", context -> {
            PluginManager manager = getPluginManager();
            getPluginLogger().log("Reloading... (0 / 5)");
            manager.stopPlugins();
            getPluginLogger().log("Reloading... (1 / 5)");
            manager.unloadPlugins();
            System.gc();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            getPluginLogger().log("Reloading... (2 / 5)");
            get().loadPlugins();
            getPluginLogger().log("Reloading... (3 / 5)");
            manager.startPlugins();
            getPluginLogger().log("Reloading... (4 / 5)");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.closeInventory();
            }
            get().readyPlugins();
            getPluginLogger().log("Reloading... (5 / 5)");
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

}
