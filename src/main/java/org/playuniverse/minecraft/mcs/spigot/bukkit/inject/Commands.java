package org.playuniverse.minecraft.mcs.spigot.bukkit.inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.command.BukkitCommand;
import org.playuniverse.minecraft.mcs.spigot.registry.Registry;
import org.playuniverse.minecraft.mcs.spigot.registry.UniqueRegistry;
import org.playuniverse.minecraft.mcs.spigot.utils.java.JavaHelper;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

public class Commands extends Injector<BukkitCommand> {

    private final UniqueRegistry<BukkitCommand> registry = new UniqueRegistry<>();
    private final Registry<BukkitCommand, PluginCommand> commands = new Registry<>();

    @Override
    public Class<BukkitCommand> getType() {
        return BukkitCommand.class;
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
    protected void inject0(ClassLookupProvider provider, BukkitCommand transfer) {
        if (transfer == null || registry.isRegistered(transfer.getName())) {
            return;
        }
        SimpleCommandMap map = (SimpleCommandMap) provider.getLookup("CraftServer").run(Bukkit.getServer(), "commandMap");
        PluginCommand command = (PluginCommand) provider.getLookup("PluginCommand").init("init", transfer.getName(), SpigotCore.get());
        command.setExecutor(transfer);
        command.setTabCompleter(transfer);
        command.setAliases(JavaHelper.fromArray(transfer.getAliases()));
        if (!map.register(transfer.getPrefix(), command)) {
            throw new IllegalStateException("Failed to register command '" + transfer.getPrefix() + ':' + command.getName() + "'!");
        }
        registry.register(transfer);
        commands.register(transfer, command);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void uninject0(ClassLookupProvider provider, BukkitCommand transfer) {
        if (transfer == null || transfer.getName() == null || !registry.isRegistered(transfer.getName())) {
            return;
        }
        SimpleCommandMap commandMap = (SimpleCommandMap) provider.getLookup("CraftServer").run(Bukkit.getServer(), "commandMap");
        Map<String, Command> map = (Map<String, Command>) provider.getLookup("CraftCommandMap").run(commandMap, "sourceMap");
        BukkitCommand command = registry.get(transfer.getName());
        PluginCommand bukkitCommand = commands.get(command);
        registry.unregister(command.getName());
        commands.unregister(command);
        ArrayList<String> aliases = new ArrayList<>(bukkitCommand.getAliases());
        aliases.add(command.getName());
        Collections.addAll(aliases, aliases.stream().map(string -> command.getPrefix() + ':' + string).toArray(String[]::new));
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
        BukkitCommand[] array = registry.values().toArray(BukkitCommand[]::new);
        for (BukkitCommand transfer : array) {
            uninject0(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
        commands.dispose();
    }

}
