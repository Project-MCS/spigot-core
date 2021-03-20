package org.playuniverse.minecraft.mcs.spigot.language.handler;

import org.bukkit.command.BlockCommandSender;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageType;

public class BlockMessageHandler implements ICommandMessageHandler<BlockCommandSender> {

    @Override
    public Class<BlockCommandSender> getType() {
        return BlockCommandSender.class;
    }

    @Override
    public void handle(BlockCommandSender receiver, MessageType type, IMessage<?> message) {
        message.send(original -> receiver.spigot().sendMessage(original));
    }

}
