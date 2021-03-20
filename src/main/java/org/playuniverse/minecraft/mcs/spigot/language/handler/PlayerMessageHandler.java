package org.playuniverse.minecraft.mcs.spigot.language.handler;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageType;

import com.syntaxphoenix.syntaxapi.nbt.NbtByte;
import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtInt;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public final class PlayerMessageHandler implements ICommandMessageHandler<Player> {

    @Override
    public Class<Player> getType() {
        return Player.class;
    }

    @Override
    public void handle(Player receiver, MessageType type, IMessage<?> message) {
        switch (type) {
        case CHAT:
            message.send(original -> receiver.spigot().sendMessage(original));
            return;
        case TITLE:
            message.send(original -> {
                NbtCompound properties = message.getProperties();
                boolean subtitleOnly = Optional.of(properties.get("subOnly")).filter(tag -> tag.getType() == NbtType.BYTE)
                    .map(tag -> ((NbtByte) tag).getValue() == 1).orElse(false);
                int subtitleOffset = Optional.of(properties.get("subOffset")).filter(tag -> tag.getType() == NbtType.INT)
                    .map(tag -> ((NbtInt) tag).getValue()).orElse(-1);
                int fadeIn = Optional.of(properties.get("fadeIn")).filter(tag -> tag.getType() == NbtType.INT)
                    .map(tag -> ((NbtInt) tag).getValue()).orElse(10);
                int stay = Optional.of(properties.get("stay")).filter(tag -> tag.getType() == NbtType.INT)
                    .map(tag -> ((NbtInt) tag).getValue()).orElse(80);
                int fadeOut = Optional.of(properties.get("fadeOut")).filter(tag -> tag.getType() == NbtType.INT)
                    .map(tag -> ((NbtInt) tag).getValue()).orElse(10);
                if (subtitleOffset < 0 && !subtitleOnly) {
                    receiver.sendTitle(TextComponent.toLegacyText(original), null, fadeIn, stay, fadeOut);
                    return;
                }
                if (subtitleOnly) {
                    receiver.sendTitle(null, TextComponent.toLegacyText(original), fadeIn, stay, fadeOut);
                    return;
                }
                BaseComponent[] title = new BaseComponent[subtitleOffset + 1];
                BaseComponent[] subtitle = new BaseComponent[original.length - title.length];
                System.arraycopy(original, 0, title, 0, title.length);
                System.arraycopy(original, subtitleOffset, subtitle, 0, subtitle.length);
                receiver.sendTitle(TextComponent.toLegacyText(title), TextComponent.toLegacyText(subtitle), fadeIn, stay, fadeOut);
            });
            return;
        case ACTION_BAR:
            message.send(original -> receiver.spigot().sendMessage(ChatMessageType.ACTION_BAR, original));
            return;
        }
    }

}
