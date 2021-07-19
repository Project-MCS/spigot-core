package org.playuniverse.minecraft.mcs.spigot.command.listener;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.Node;

public abstract class AbstractRedirect {

    protected abstract Node<MinecraftInfo> handleComplete(String command);

    protected List<String> handleNullComplete(MinecraftCommand root, CommandSender sender, String[] args) {
        return null;
    }

    protected abstract Node<MinecraftInfo> handleCommand(String command);

    protected abstract boolean hasGlobal();
    
    protected abstract String getGlobal();
    
    protected abstract boolean isValid();
    
    protected abstract int argBuildStart();
}
