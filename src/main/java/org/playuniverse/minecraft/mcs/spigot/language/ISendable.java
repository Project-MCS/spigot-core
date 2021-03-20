package org.playuniverse.minecraft.mcs.spigot.language;

import java.util.function.Consumer;

import org.playuniverse.minecraft.mcs.spigot.constant.Singleton;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;

public interface ISendable {

    IMessage<?> getMessage();

    default boolean send(Object receiver) {
        return send(MessageType.CHAT, receiver, (Consumer<NbtCompound>) null);
    }

    default boolean send(MessageType type, Object receiver) {
        return send(type, receiver, (Consumer<NbtCompound>) null);
    }

    default boolean send(MessageType type, Object receiver, Consumer<NbtCompound> properties) {
        IMessageHandler<?> handler = Singleton.Registries.MESSAGE_HANDLER.getFor(receiver);
        if (handler == null) {
            return false;
        }
        IMessage<?> message = getMessage();
        if (properties != null) {
            properties.accept(message.getProperties());
        }
        handler.handleObject(receiver, MessageType.CHAT, message);
        return true;
    }

    default <O> void send(O receiver, IMessageHandler<O> handler) {
        send(MessageType.CHAT, receiver, handler, null);
    }

    default <O> void send(MessageType type, O receiver, IMessageHandler<O> handler) {
        send(type, receiver, handler, null);
    }

    default <O> void send(MessageType type, O receiver, IMessageHandler<O> handler, Consumer<NbtCompound> properties) {
        IMessage<?> message = getMessage();
        if (properties != null) {
            properties.accept(message.getProperties());
        }
        handler.handle(receiver, MessageType.CHAT, message);
    }

}
