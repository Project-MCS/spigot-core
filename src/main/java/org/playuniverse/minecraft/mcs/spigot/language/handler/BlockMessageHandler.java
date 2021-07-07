package org.playuniverse.minecraft.mcs.spigot.language.handler;

import org.bukkit.command.BlockCommandSender;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageType;

public final class BlockMessageHandler implements ICommandMessageHandler<BlockCommandSender> {
    
    public static final BlockMessageHandler INSTANCE = new BlockMessageHandler();
    
    private BlockMessageHandler() {}

    @Override
    public Class<BlockCommandSender> getType() {
        return BlockCommandSender.class;
    }

    @Override
    public void handle(BlockCommandSender receiver, MessageType type, IMessage<?> message) {
        message.send(original -> receiver.spigot().sendMessage(original));
    }

}
