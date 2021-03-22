package org.playuniverse.minecraft.mcs.spigot.language;

import org.playuniverse.minecraft.mcs.spigot.constant.Singleton;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.Placeholder;
import org.playuniverse.minecraft.mcs.spigot.registry.Registry;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.utils.key.NamespacedKey;

public final class MessageWrapper<T> {

    private final T receiver;
    private final IMessageHandler<T> handler;

    public MessageWrapper(T receiver, IMessageHandler<T> handler) {
        this.receiver = receiver;
        this.handler = handler;
    }

    /*
     * Language
     */

    public Language getLanguage() {
        return handler.getLanguage(receiver);
    }

    public void setLanguage(Language language) {
        handler.setLanguage(receiver, language);
    }

    public void setLanguageIfNotExists(Language language) {
        handler.setLanguageIfNotExists(receiver, language);
    }

    /*
     * Getter
     */

    public T getReceiver() {
        return receiver;
    }

    public IMessageHandler<T> getHandler() {
        return handler;
    }

    /*
     * Message sending
     */

    public <M> boolean send(M message) {
        return send(MessageType.CHAT, null, null, message);
    }

    @SuppressWarnings("unchecked")
    public <M> boolean send(M... messages) {
        return send(MessageType.CHAT, null, null, messages);
    }

    public <M> boolean send(Placeholder[] placeholders, M message) {
        return send(MessageType.CHAT, null, placeholders, message);
    }

    @SuppressWarnings("unchecked")
    public <M> boolean send(Placeholder[] placeholders, M... messages) {
        return send(MessageType.CHAT, null, placeholders, messages);
    }

    public <M> boolean send(NbtCompound properties, Placeholder[] placeholders, M message) {
        return send(MessageType.CHAT, properties, placeholders, message);
    }

    @SuppressWarnings("unchecked")
    public <M> boolean send(NbtCompound properties, Placeholder[] placeholders, M... messages) {
        return send(MessageType.CHAT, properties, placeholders, messages);
    }

    public <M> boolean send(MessageType type, M message) {
        return send(type, null, null, message);
    }

    @SuppressWarnings("unchecked")
    public <M> boolean send(MessageType type, M... messages) {
        return send(type, null, null, messages);
    }

    public <M> boolean send(MessageType type, Placeholder[] placeholders, M message) {
        return send(type, null, placeholders, message);
    }

    @SuppressWarnings("unchecked")
    public <M> boolean send(MessageType type, Placeholder[] placeholders, M... messages) {
        return send(type, null, placeholders, messages);
    }

    public <M> boolean send(MessageType type, NbtCompound properties, Placeholder[] placeholders, M message) {
        MessageBuilder<?> builder = Singleton.Registries.MESSAGE_BUILDER.getFor(message);
        if (builder == null) {
            return false;
        }
        handler.handle(receiver, type, builder.buildMessageOfObjects(buildData(properties, placeholders), message));
        return true;
    }

    @SuppressWarnings("unchecked")
    public <M> boolean send(MessageType type, NbtCompound properties, Placeholder[] placeholders, M... messages) {
        MessageBuilder<?> builder = Singleton.Registries.MESSAGE_BUILDER.getFor(messages);
        if (builder == null) {
            return false;
        }
        handler.handle(receiver, type, builder.buildMessageOfObjects(buildData(properties, placeholders), messages));
        return true;
    }

    /*
     * Message retrieving and sending
     */

    public int send(NamespacedKey... keys) {
        return send(MessageType.CHAT, null, null, keys);
    }

    public int send(Placeholder[] placeholders, NamespacedKey... keys) {
        return send(MessageType.CHAT, null, placeholders, keys);
    }

    public int send(NbtCompound properties, Placeholder[] placeholders, NamespacedKey... keys) {
        return send(MessageType.CHAT, properties, placeholders, keys);
    }

    public int send(MessageType type, NamespacedKey... keys) {
        return send(type, null, null, keys);
    }

    public int send(MessageType type, Placeholder[] placeholders, NamespacedKey... keys) {
        return send(type, null, placeholders, keys);
    }

    public int send(MessageType type, NbtCompound properties, Placeholder[] placeholders, NamespacedKey... keys) {
        int sent = 0;
        NbtCompound data = buildData(properties, placeholders);
        Registry<NamespacedKey, Object> registry = Singleton.Registries.TRANSLATIONS.getOrElse(getLanguage(),
            Singleton.Registries.MESSAGES);
        boolean isDefault = registry == Singleton.Registries.MESSAGES;
        for (NamespacedKey key : keys) {
            Object message = registry.get(key);
            if (message == null) {
                if (isDefault) {
                    continue;
                }
                message = Singleton.Registries.MESSAGES.get(key);
                if (message == null) {
                    continue;
                }
            }
            MessageBuilder<?> builder = Singleton.Registries.MESSAGE_BUILDER.getFor(message);
            if (builder == null) {
                continue;
            }
            if (message.getClass().isArray()) {
                handler.handle(receiver, type, builder.buildMessageOfObjects(data, (Object[]) message));
            } else {
                handler.handle(receiver, type, builder.buildMessageOfObjects(data, message));
            }
        }
        return sent;
    }

    /*
     * Data helper
     */

    private NbtCompound buildData(NbtCompound properties, Placeholder[] placeholders) {
        NbtCompound data = new NbtCompound();
        if (properties != null) {
            data.set("properties", properties);
        }
        if (placeholders != null) {
            NbtCompound compound = new NbtCompound();
            for (Placeholder placeholder : placeholders) {
                if (placeholder == null || placeholder.getKey() == null) {
                    continue;
                }
                compound.set(placeholder.getKey(), placeholder.getValue());
            }
            if (!compound.isEmpty()) {
                data.set("placeholders", compound);
            }
        }
        return data;
    }

    /*
     * Static builder
     */

    @SuppressWarnings("unchecked")
    public static <S> MessageWrapper<S> of(S receiver) {
        IMessageHandler<?> handler = Singleton.Registries.MESSAGE_HANDLER.getFor(receiver);
        if (handler == null) {
            return null;
        }
        return new MessageWrapper<>(receiver, (IMessageHandler<S>) handler);
    }

}
