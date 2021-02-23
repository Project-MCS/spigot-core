package org.playuniverse.minecraft.mcs.utils.log;

import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class ConsoleLogger implements BiConsumer<Boolean, String> {

    private final ConsoleCommandSender sender = Bukkit.getConsoleSender();
    private String prefix;

    /*
     * 
     */

    public ConsoleLogger() {
        setPrefix("");
    }

    public ConsoleLogger(String prefix) {
        setPrefix(prefix);
    }

    /*
     * 
     */

    public ConsoleLogger setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    /*
     * 
     */

    @Override
    public void accept(Boolean flag, String message) {
        send(message);
    }

    public ConsoleLogger send(String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        return this;
    }

}
