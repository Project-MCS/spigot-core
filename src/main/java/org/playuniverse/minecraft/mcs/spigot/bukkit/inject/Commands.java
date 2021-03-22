package org.playuniverse.minecraft.mcs.spigot.bukkit.inject;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;
import org.playuniverse.minecraft.mcs.spigot.registry.Registry;
import org.playuniverse.minecraft.mcs.spigot.registry.UniqueRegistry;
import org.playuniverse.minecraft.mcs.spigot.utils.java.JavaHelper;

import com.sun.source.util.Plugin;

import net.sourcewriters.minecraft.versiontools.reflection.reflect.ReflectionProvider;

public class Commands extends Injector<MinecraftCommand> {

    private final UniqueRegistry<MinecraftCommand> registry = new UniqueRegistry<>();
    private final Registry<MinecraftCommand, PluginCommand> commands = new Registry<>();

    @Override
    public Class<MinecraftCommand> getType() {
        return MinecraftCommand.class;
    }

    @Override
    public boolean isCompatible(ReflectionProvider provider) {
        return true;
    }

    @Override
    protected void onSetup(ReflectionProvider provider) {
        provider.createReflect("PluginCommand", "org.bukkit.command.PluginCommand").searchConstructor("init", String.class, Plugin.class);
        provider.createCBReflect("CraftCommandMap", "command.CraftCommandMap").searchMethod("sourceMap", "getKnownCommands");
        provider.createCBReflect("CraftServer", "CraftServer").searchMethod("commandMap", "getCommandMap");
    }

    @Override
    public void inject(ReflectionProvider provider, MinecraftCommand transfer) {
        if (transfer == null || !transfer.isValid() || registry.isRegistered(transfer.getId())) {
            return;
        }
        SimpleCommandMap map = (SimpleCommandMap) provider.getReflect("CraftServer").run(Bukkit.getServer(), "commandMap");
        PluginCommand command = (PluginCommand) provider.getReflect("PluginCommand").init("init", transfer.getId(), transfer.getOwner());
        command.setAliases(JavaHelper.fromArray(transfer.getAliases()));
        registry.register(transfer);
        commands.register(transfer, command);
        command.register(map);
    }

    @Override
    public void uninject(ReflectionProvider provider, MinecraftCommand transfer) {
        if (transfer == null || transfer.getId() == null || !registry.isRegistered(transfer.getId())) {
            return;
        }
        SimpleCommandMap map = (SimpleCommandMap) provider.getReflect("CraftServer").run(Bukkit.getServer(), "commandMap");
        MinecraftCommand command = registry.get(transfer.getId());
        PluginCommand bukkitCommand = commands.get(command);
        registry.unregister(command.getId());
        commands.unregister(command);
        bukkitCommand.unregister(map);
    }

    @Override
    public void uninjectAll(ReflectionProvider provider) {
        if (registry.isEmpty()) {
            return;
        }
        MinecraftCommand[] array = registry.values().toArray(MinecraftCommand[]::new);
        for (MinecraftCommand transfer : array) {
            uninject(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
        commands.dispose();
    }

}
