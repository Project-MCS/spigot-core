package org.playuniverse.minecraft.mcs.spigot.language.handler;

import org.bukkit.command.ConsoleCommandSender;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageType;

public final class ConsoleMessageHandler implements ICommandMessageHandler<ConsoleCommandSender> {
    
    public static final ConsoleMessageHandler INSTANCE = new ConsoleMessageHandler();
    
    private ConsoleMessageHandler() {}

    @Override
    public Class<ConsoleCommandSender> getType() {
        return ConsoleCommandSender.class;
    }

    @Override
    public void handle(ConsoleCommandSender receiver, MessageType type, IMessage<?> message) {
        message.send(original -> receiver.spigot().sendMessage(original));
    }

}
