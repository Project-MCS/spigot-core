package org.playuniverse.minecraft.mcs.spigot.language;

import java.util.function.IntFunction;

import org.playuniverse.minecraft.mcs.spigot.language.placeholder.DefaultPlaceholderStore;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.Placeholder;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.PlaceholderParser;
import org.playuniverse.minecraft.mcs.spigot.registry.ITyped;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

public abstract class MessageBuilder<T> implements ITyped<T> {

    protected final Class<T> type;
    protected final IntFunction<T[]> arrayBuilder;

    public MessageBuilder(Class<T> type, IntFunction<T[]> arrayBuilder) {
        this.type = type;
        this.arrayBuilder = arrayBuilder;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    public IMessage<T> buildMessageOfObjects(NbtCompound data, Object... objects) {
        if (objects.length == 0 || !getType().isInstance(objects[0])) {
            return emptyMessage();
        }
        T[] array = arrayBuilder.apply(objects.length);
        System.arraycopy(objects, 0, array, 0, objects.length);
        return buildMessage(data, array);
    }

    @SuppressWarnings("unchecked")
    public abstract IMessage<T> buildMessage(NbtCompound data, T... objects);

    public abstract IMessage<T> emptyMessage();

    public final NbtCompound getProperties(NbtCompound data) {
        return data.hasKey("properties", NbtType.COMPOUND) ? data.getCompound("properties") : new NbtCompound();
    }

    public final String apply(NbtCompound placeholderData, String content) {
        DefaultPlaceholderStore store = new DefaultPlaceholderStore();
        PlaceholderParser.parseMessage(store, content);
        for (Placeholder placeholder : store.placeholderArray()) {
            if (!placeholderData.hasKey(placeholder.getKey(), NbtType.STRING)) {
                continue;
            }
            placeholder.setValue(placeholderData.getString(placeholder.getKey()));
        }
        return PlaceholderParser.apply(store, content);
    }

}
