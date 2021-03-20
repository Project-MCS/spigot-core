package org.playuniverse.minecraft.mcs.spigot.language;

import org.bukkit.util.Consumer;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;

import net.md_5.bungee.api.chat.BaseComponent;

public interface IMessage<T> extends ISendable {
    
    MessageBuilder<T> getBuilder();

    BaseComponent[] asComponents();

    NbtCompound getProperties();
    
    default IMessage<T> send(Consumer<BaseComponent[]> sender) {
        sender.accept(asComponents());
        return this;
    }
    
    @Override
    default IMessage<?> getMessage() {
        return this;
    }

}
