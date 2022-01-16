package org.playuniverse.minecraft.mcs.spigot.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.mcs.spigot.language.MessageWrapper;
import org.playuniverse.minecraft.mcs.spigot.module.ModuleIndicator;

import com.syntaxphoenix.avinity.command.ISource;

public final class BukkitSource implements ISource {

    private final CommandSender sender;
    private final MessageWrapper<?> wrapper;

    public BukkitSource(CommandSender sender, ModuleIndicator indicator) {
        this.sender = sender;
        this.wrapper = MessageWrapper.of(sender, indicator);
    }

    public MessageWrapper<?> getWrapper() {
        return wrapper;
    }
    
    public CommandSender getSender() {
        return sender;
    }

    public boolean isEntity() {
        return sender instanceof Entity;
    }

    public Entity getEntity() {
        return isEntity() ? (Entity) sender : null;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player getPlayer() {
        return isPlayer() ? (Player) sender : null;
    }

    public boolean isConsole() {
        return sender instanceof ConsoleCommandSender;
    }

    public ConsoleCommandSender getConsole() {
        return isConsole() ? (ConsoleCommandSender) sender : null;
    }

    public boolean isBlock() {
        return sender instanceof BlockCommandSender;
    }

    public BlockCommandSender getBlock() {
        return isBlock() ? (BlockCommandSender) sender : null;
    }

    @Override
    public boolean hasPermission(String id) {
        return sender.hasPermission(id);
    }

    // TODO: Add translation system and send message

}
