package org.playuniverse.minecraft.mcs.spigot.language.message;

import org.bukkit.util.Consumer;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.language.message.builder.StringMessageBuilder;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.utils.java.Strings;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public final class StringMessage implements IMessage<String> {

    private final NbtCompound properties;
    private final String[] messages;

    private ChatColor defaultColor = ChatColor.GRAY;

    public StringMessage(String... messages) {
        this(new NbtCompound(), messages);
    }

    public StringMessage(NbtCompound properties, String... messages) {
        this.properties = properties;
        this.messages = messages;
    }

    public boolean isMerged() {
        return properties.getBoolean("merge");
    }

    public ChatColor getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(ChatColor defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String[] getMessagesRaw() {
        return messages;
    }

    public String[] getMessages() {
        if (isMerged()) {
            return new String[] {
                Strings.toString(messages, "")
            };
        }
        return messages;
    }

    @Override
    public MessageBuilder<String> getBuilder() {
        return StringMessageBuilder.INSTANCE;
    }

    @Override
    public BaseComponent[] asComponents() {
        return asComponents(Strings.toString(messages, ""));
    }

    private BaseComponent[] asComponents(String string) {
        return TextComponent.fromLegacyText(string, defaultColor);
    }

    @Override
    public NbtCompound getProperties() {
        return properties;
    }

    @Override
    public IMessage<String> send(Consumer<BaseComponent[]> sender) {
        if (isMerged()) {
            return IMessage.super.send(sender);
        }
        for (String message : messages) {
            sender.accept(asComponents(message));
        }
        return this;
    }

}
