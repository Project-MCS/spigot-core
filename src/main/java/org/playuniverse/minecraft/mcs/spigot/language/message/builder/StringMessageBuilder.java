package org.playuniverse.minecraft.mcs.spigot.language.message.builder;

import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.language.message.StringMessage;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

public class StringMessageBuilder extends MessageBuilder<String> {

    public static final StringMessageBuilder INSTANCE = new StringMessageBuilder();

    private StringMessageBuilder() {
        super(String.class, String[]::new);
    }

    @Override
    public IMessage<String> buildMessage(NbtCompound data, String... strings) {
        if (strings.length == 0) {
            return null;
        }
        String[] output = strings.clone();
        if (data.hasKey("placeholders", NbtType.COMPOUND)) {
            NbtCompound placeholders = data.getCompound("placeholders");
            for (int index = 0; index < output.length; index++) {
                output[index] = apply(placeholders, output[index]);
            }
        }
        return new StringMessage(data.getCompound("properties"), output);
    }

}
