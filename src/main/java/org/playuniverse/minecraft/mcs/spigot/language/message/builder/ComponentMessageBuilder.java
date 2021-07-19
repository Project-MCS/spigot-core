package org.playuniverse.minecraft.mcs.spigot.language.message.builder;

import org.playuniverse.minecraft.mcs.spigot.language.IMessage;
import org.playuniverse.minecraft.mcs.spigot.language.MessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.language.message.ComponentMessage;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;

public final class ComponentMessageBuilder extends MessageBuilder<BaseComponent> {

    public static final ComponentMessageBuilder INSTANCE = new ComponentMessageBuilder();

    private ComponentMessageBuilder() {
        super(BaseComponent.class, BaseComponent[]::new);
    }

    @Override
    public IMessage<BaseComponent> emptyMessage() {
        return new ComponentMessage();
    }

    @Override
    public IMessage<BaseComponent> buildMessage(NbtCompound data, BaseComponent... components) {
        if (components.length == 0) {
            return null;
        }
        BaseComponent[] output = copy(components);
        if (data.hasKey("placeholders", NbtType.COMPOUND)) {
            NbtCompound placeholders = data.getCompound("placeholders");
            for (BaseComponent component : output) {
                applyPlaceholders(placeholders, component);
            }
        }
        return new ComponentMessage(getProperties(data), output);
    }

    private BaseComponent[] copy(BaseComponent[] components) {
        BaseComponent[] output = new BaseComponent[components.length];
        for (int index = 0; index < components.length; index++) {
            if (components[index] == null) {
                output[index] = null;
                continue;
            }
            output[index] = components[index].duplicate();
        }
        return output;
    }

    private void applyPlaceholders(NbtCompound data, BaseComponent component) {
        if (component == null) {
            return;
        }
        if (component.getHoverEvent() != null) {
            applyPlaceholders(data, component.getHoverEvent());
        }
        if (component.getClickEvent() != null) {
            applyPlaceholders(data, component, component.getClickEvent());
        }
        if (component instanceof TextComponent) {
            TextComponent casted = (TextComponent) component;
            casted.setText(apply(data, casted.getText()));
        } else if (component instanceof KeybindComponent) {
            KeybindComponent casted = (KeybindComponent) component;
            casted.setKeybind(apply(data, casted.getKeybind()));
        } else if (component instanceof TranslatableComponent) {
            TranslatableComponent casted = (TranslatableComponent) component;
            casted.setTranslate(apply(data, casted.getTranslate()));
        } else if (component instanceof ScoreComponent) {
            ScoreComponent casted = (ScoreComponent) component;
            casted.setName(apply(data, casted.getName()));
            casted.setObjective(apply(data, casted.getObjective()));
            casted.setValue(apply(data, casted.getValue()));
        } else if (component instanceof SelectorComponent) {
            SelectorComponent casted = (SelectorComponent) component;
            casted.setSelector(casted.getSelector());
        }
        if (component.getExtra() != null) {
            for (BaseComponent extra : component.getExtra()) {
                applyPlaceholders(data, extra);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void applyPlaceholders(NbtCompound data, HoverEvent event) {
        switch (event.getAction()) {
        case SHOW_ACHIEVEMENT:
            return;
        case SHOW_ENTITY:
            for (Content content : event.getContents()) {
                Entity entity = (Entity) content;
                entity.setId(apply(data, entity.getId()));
                entity.setType(apply(data, entity.getType()));
                applyPlaceholders(data, entity.getName());
            }
            return;
        case SHOW_ITEM:
            for (Content content : event.getContents()) {
                Item item = (Item) content;
                item.setId(apply(data, item.getId()));
                item.setTag(ItemTag.ofNbt(apply(data, item.getTag().getNbt())));
            }
            return;
        case SHOW_TEXT:
            Content[] contents = event.getContents().toArray(Content[]::new);
            event.getContents().clear();
            for (int index = 0; index < contents.length; index++) {
                Text text = (Text) contents[index];
                Object value = text.getValue();
                if (value instanceof String) {
                    event.getContents().add(new Text(apply(data, (String) value)));
                    continue;
                }
                BaseComponent[] array = (BaseComponent[]) value;
                for (BaseComponent component : array) {
                    applyPlaceholders(data, component);
                }
                event.getContents().add(text);
            }
            return;
        }
    }

    private void applyPlaceholders(NbtCompound data, BaseComponent component, ClickEvent event) {
        component.setClickEvent(new ClickEvent(event.getAction(), apply(data, event.getValue())));
    }

}
