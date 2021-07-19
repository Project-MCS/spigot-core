package org.playuniverse.minecraft.mcs.spigot.language.handler;

import org.bukkit.command.RemoteConsoleCommandSender;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageType;

public final class RemoteConsoleMessageHandler implements ICommandMessageHandler<RemoteConsoleCommandSender> {
    
    public static final RemoteConsoleMessageHandler INSTANCE = new RemoteConsoleMessageHandler();
    
    private RemoteConsoleMessageHandler() {}

    @Override
    public Class<RemoteConsoleCommandSender> getType() {
        return RemoteConsoleCommandSender.class;
    }

    @Override
    public void handle(RemoteConsoleCommandSender receiver, MessageType type, IMessage<?> message) {
        message.send(original -> receiver.spigot().sendMessage(original));
    }

}
