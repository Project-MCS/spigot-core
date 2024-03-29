package org.playuniverse.minecraft.mcs.spigot.bukkit.inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;
import org.playuniverse.minecraft.mcs.spigot.registry.Registry;
import org.playuniverse.minecraft.mcs.spigot.registry.UniqueRegistry;
import org.playuniverse.minecraft.mcs.spigot.utils.java.JavaHelper;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

public class Commands extends Injector<MinecraftCommand> {

    private final UniqueRegistry<MinecraftCommand> registry = new UniqueRegistry<>();
    private final Registry<MinecraftCommand, PluginCommand> commands = new Registry<>();

    @Override
    public Class<MinecraftCommand> getType() {
        return MinecraftCommand.class;
    }

    @Override
    public boolean isCompatible(ClassLookupProvider provider) {
        return true;
    }

    @Override
    protected void onSetup(ClassLookupProvider provider) {
        provider.createLookup("PluginCommand", "org.bukkit.command.PluginCommand").searchConstructor("init", String.class, Plugin.class);
        provider.createCBLookup("CraftCommandMap", "command.CraftCommandMap").searchMethod("sourceMap", "getKnownCommands");
        provider.createCBLookup("CraftServer", "CraftServer").searchMethod("commandMap", "getCommandMap");
    }

    @Override
    protected void inject0(ClassLookupProvider provider, MinecraftCommand transfer) {
        if (transfer == null || !transfer.isValid() || registry.isRegistered(transfer.getId())) {
            return;
        }
        SimpleCommandMap map = (SimpleCommandMap) provider.getLookup("CraftServer").run(Bukkit.getServer(), "commandMap");
        PluginCommand command = (PluginCommand) provider.getLookup("PluginCommand").init("init", transfer.getId(), (Plugin) transfer.getOwner());
        command.setExecutor(transfer);
        command.setTabCompleter(transfer);
        command.setAliases(JavaHelper.fromArray(transfer.getAliases()));
        if (!map.register(transfer.getFallbackPrefix(), command)) {
            throw new IllegalStateException("Failed to register command '" + transfer.getFallbackPrefix() + ':' + command.getName() + "'!");
        }
        registry.register(transfer);
        commands.register(transfer, command);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void uninject0(ClassLookupProvider provider, MinecraftCommand transfer) {
        if (transfer == null || transfer.getId() == null || !registry.isRegistered(transfer.getId())) {
            return;
        }
        SimpleCommandMap commandMap = (SimpleCommandMap) provider.getLookup("CraftServer").run(Bukkit.getServer(), "commandMap");
        Map<String, Command> map = (Map<String, Command>) provider.getLookup("CraftCommandMap").run(commandMap, "sourceMap");
        MinecraftCommand command = registry.get(transfer.getId());
        PluginCommand bukkitCommand = commands.get(command);
        registry.unregister(command.getId());
        commands.unregister(command);
        ArrayList<String> aliases = new ArrayList<>(bukkitCommand.getAliases());
        aliases.add(command.getName());
        Collections.addAll(aliases, aliases.stream().map(string -> command.getFallbackPrefix() + ':' + string).toArray(String[]::new));
        for (String alias : aliases) {
            if (map.get(alias) != bukkitCommand) {
                continue;
            }
            map.remove(alias);
        }
        bukkitCommand.unregister(commandMap);
    }

    @Override
    protected void uninjectAll0(ClassLookupProvider provider) {
        if (registry.isEmpty()) {
            return;
        }
        MinecraftCommand[] array = registry.values().toArray(MinecraftCommand[]::new);
        for (MinecraftCommand transfer : array) {
            uninject0(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
        commands.dispose();
    }

}
