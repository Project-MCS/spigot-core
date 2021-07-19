package org.playuniverse.minecraft.mcs.spigot.language.handler;

import org.bukkit.command.ProxiedCommandSender;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageType;

public final class ProxiedMessageHandler implements ICommandMessageHandler<ProxiedCommandSender> {
    
    public static final ProxiedMessageHandler INSTANCE = new ProxiedMessageHandler();
    
    private ProxiedMessageHandler() {}

    @Override
    public Class<ProxiedCommandSender> getType() {
        return ProxiedCommandSender.class;
    }

    @Override
    public void handle(ProxiedCommandSender receiver, MessageType type, IMessage<?> message) {
        message.send(original -> receiver.spigot().sendMessage(original));
    }

}
