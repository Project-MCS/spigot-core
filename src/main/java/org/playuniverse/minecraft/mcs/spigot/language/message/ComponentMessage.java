package org.playuniverse.minecraft.mcs.spigot.language.message;

import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.language.message.builder.ComponentMessageBuilder;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;

import net.md_5.bungee.api.chat.BaseComponent;

public final class ComponentMessage implements IMessage<BaseComponent> {

    private final NbtCompound properties;
    private final BaseComponent[] components;
    
    public ComponentMessage(BaseComponent... components) {
        this(null, components);
    }

    public ComponentMessage(NbtCompound properties, BaseComponent... components) {
        this.properties = properties == null ? new NbtCompound() : properties;
        this.components = components;
    }

    @Override
    public MessageBuilder<BaseComponent> getBuilder() {
        return ComponentMessageBuilder.INSTANCE;
    }

    @Override
    public BaseComponent[] asComponents() {
        return components;
    }

    @Override
    public NbtCompound getProperties() {
        return properties;
    }

}
