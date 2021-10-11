package org.playuniverse.minecraft.mcs.spigot.command.listener;

import org.bukkit.command.CommandSender;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.IModule;
import org.playuniverse.minecraft.mcs.spigot.language.MessageWrapper;

public class MinecraftInfo {

    private final PluginBase<?> base;
    private final CommandSender sender;
    private final MessageWrapper<? extends CommandSender> receiver;

    public MinecraftInfo(PluginBase<?> base, CommandSender sender, IModule plugin) {
        this.base = base;
        this.sender = sender;
        this.receiver = MessageWrapper.of(sender.getClass().cast(sender), plugin);
    }

    public MessageWrapper<? extends CommandSender> getReceiver() {
        return receiver;
    }

    public CommandSender getSender() {
        return sender;
    }

    public PluginBase<?> getBase() {
        return base;
    }

}
