package org.playuniverse.minecraft.mcs.spigot.language;

import java.util.function.IntFunction;

import org.playuniverse.minecraft.mcs.spigot.language.placeholder.DefaultPlaceholderStore;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.Placeholder;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.PlaceholderParser;
import org.playuniverse.minecraft.mcs.spigot.registry.ITyped;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;

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
        if (objects.length == 0 || getType().isInstance(objects[0])) {
            return null;
        }
        T[] array = arrayBuilder.apply(objects.length);
        System.arraycopy(objects, 0, array, 0, objects.length);
        return buildMessage(data, array);
    }

    @SuppressWarnings("unchecked")
    public abstract IMessage<T> buildMessage(NbtCompound data, T... objects);

    public final String apply(NbtCompound placeholderData, String content) {
        DefaultPlaceholderStore store = new DefaultPlaceholderStore();
        PlaceholderParser.parse(store, content);
        for (Placeholder placeholder : store.placeholderArray()) {
            if (!placeholderData.hasKey(placeholder.getKey())) {
                continue;
            }
            placeholder.setValue(placeholderData.get(placeholder.getKey()).getValue().toString());
        }
        return PlaceholderParser.apply(store, content);
    }

}
