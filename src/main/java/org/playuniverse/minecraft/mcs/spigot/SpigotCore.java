package org.playuniverse.minecraft.mcs.spigot;

import org.bukkit.Bukkit;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.CommandNode;
import org.playuniverse.minecraft.mcs.spigot.helper.ColorHelper;

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
    }

    @Override
    protected void onStartup() {
        getInjections().inject(command = new MinecraftCommand(getCommandManager(), this, "system", "sys", "core"));
        getCommandManager().register(new CommandNode<>("reload", context -> {
            getPluginLogger().log("Reloading... (0 / 4)");
            getPluginManager().stopPlugins();
            getPluginLogger().log("Reloading... (1 / 4)");
            get().unloadPlugins();
            getPluginLogger().log("Reloading... (2 / 4)");
            get().loadPlugins();
            getPluginLogger().log("Reloading... (3 / 4)");
            getPluginManager().startPlugins();
            getPluginLogger().log("Reloading... (4 / 4)");
            getPluginLogger().log("Reload complete!");
        }));
    }

    @Override
    protected void onStarted() {
        Bukkit.getConsoleSender().sendMessage(ColorHelper.hexToMinecraftColor("#124F24") + "This is a test");
    }

    @Override
    protected void onShutdown() {
        getInjections().uninject(command);
    }

}
