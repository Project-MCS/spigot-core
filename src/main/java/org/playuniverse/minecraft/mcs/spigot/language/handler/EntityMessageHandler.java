package org.playuniverse.minecraft.mcs.spigot.language.handler;

import org.bukkit.entity.Entity;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageType;

public final class EntityMessageHandler implements ICommandMessageHandler<Entity> {
    
    public static final EntityMessageHandler INSTANCE = new EntityMessageHandler();
    
    private EntityMessageHandler() {}

    @Override
    public Class<Entity> getType() {
        return Entity.class;
    }

    @Override
    public void handle(Entity receiver, MessageType type, IMessage<?> message) {
        message.send(original -> receiver.spigot().sendMessage(original));
    }

}
