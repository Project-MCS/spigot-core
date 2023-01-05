package org.playuniverse.minecraft.vcompat.reflection.provider.tools;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.playuniverse.minecraft.vcompat.reflection.tools.ServerTools;

import jline.console.ConsoleReader;

public class ServerToolsImpl extends ServerTools {

    @Override
    public void setMotd(String text) {
        ((CraftServer) Bukkit.getServer()).getServer().setMotd(text);
    }

    @Override
    public String getMotd() {
        return ((CraftServer) Bukkit.getServer()).getServer().getMotd();
    }
    
    @SuppressWarnings("resource")
    @Override
    public ConsoleReader getConsole() {
        return ((CraftServer) Bukkit.getServer()).getServer().reader;
    }

}