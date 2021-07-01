package org.playuniverse.minecraft.mcs.spigot.utils.log;

import java.io.PrintStream;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

import com.syntaxphoenix.syntaxapi.logging.color.LogType;

import net.md_5.bungee.api.ChatColor;

public class BukkitLogger extends AbstractLogger<BukkitLogger> {
    
    private final ConsoleCommandSender sender = Bukkit.getConsoleSender();
    private final PrintStream file;

    private String prefix = "Server";
    
    public BukkitLogger(ClassLookupProvider provider, PrintStream file) {
        this.file = file;
        this.colored = true;
        setDefaultTypes();
    }

    public BukkitLogger(ClassLookupProvider provider) {
        this(provider, null);
    }

    public PrintStream getFile() {
        return file;
    }

    public BukkitLogger setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public BukkitLogger close() {
        if (hasStream()) {
            file.flush();
            file.close();
        }
        return this;
    }

    @Override
    public boolean hasCustom() {
        return sender != null;
    }

    @Override
    public boolean hasStream() {
        return file != null;
    }

    @Override
    protected BukkitLogger instance() {
        return this;
    }

    @Override
    protected String format(String type, String thread, String message) {
        return super.format(type, thread, message).replace("%prefix%", prefix);
    }

    @Override
    public BukkitLogger println(LogType type, String message) {
        if (!colored) {
            return println(message);
        }
        if (state.useCustom() && hasCustom()) {
            println(true, type.asColorString(true) + message);
        }
        if (state.useStream() && hasStream()) {
            println(false, type.asColorString(false) + message);
        }
        return this;
    }

    @Override
    public BukkitLogger print(LogType type, String message) {
        if (!colored) {
            return print(message);
        }
        if (state.useCustom() && hasCustom()) {
            print(true, type.asColorString(true) + message);
        }
        if (state.useStream() && hasStream()) {
            print(false, type.asColorString(false) + message);
        }
        return this;
    }

    @Override
    protected void println(boolean custom, String message) {
        message = color(message);
        if (custom) {
            sender.sendMessage(message);
            return;
        }
        file.println(strip(message));
        file.flush();
    }

    @Override
    protected void print(boolean custom, String message) {
        message = color(message);
        if (custom) {
            sender.sendMessage(message);
            return;
        }
        file.print(strip(message));
        file.flush();
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String strip(String message) {
        return ChatColor.stripColor(message);
    }

}
