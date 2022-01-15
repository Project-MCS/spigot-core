package org.playuniverse.minecraft.mcs.spigot.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.syntaxphoenix.avinity.command.ISource;

public final class BukkitSource implements ISource {

    private final CommandSender sender;

    public BukkitSource(CommandSender sender) {
        this.sender = sender;
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
