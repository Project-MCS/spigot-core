package org.playuniverse.minecraft.mcs.spigot.language;

import org.playuniverse.minecraft.mcs.utils.general.Placeholder;

public interface IMessage extends ITranslatable {

    @Override
    public default TranslationType type() {
        return TranslationType.MESSAGE;
    }

    public default String translate(String language) {
        return Translations.translate(language, this);
    }

    public default String translate(String language, Placeholder... placeholders) {
        return Translations.translate(language, this, placeholders);
    }

}
