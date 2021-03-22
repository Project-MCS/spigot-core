package org.playuniverse.minecraft.mcs.spigot;

import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;

import com.syntaxphoenix.syntaxapi.utils.key.Namespace;

public class SpigotCore extends PluginBase<SpigotCore> {

    public static final Namespace NAMESPACE = Namespace.of("system");

    public static SpigotCore get() {
        return get(SpigotCore.class);
    }

    private MinecraftCommand command;

    @Override
    protected void onStartup() {
        getInjections().inject(command = new MinecraftCommand(getCommandManager(), this, "system", "sys", "core"));
    }

    @Override
    protected void onShutdown() {
        getInjections().uninject(command);
    }

}
