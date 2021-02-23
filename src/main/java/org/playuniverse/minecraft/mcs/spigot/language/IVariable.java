package org.playuniverse.minecraft.mcs.spigot.language;

import org.playuniverse.minecraft.mcs.spigot.utils.general.Placeholder;

public interface IVariable extends ITranslatable {

    @Override
    public default TranslationType type() {
        return TranslationType.VARIABLE;
    }

    @Override
    public default String translate(String language) {
        return Translations.translate(language, this);
    }

    @Override
    public default String translate(String language, Placeholder... placeholders) {
        return Translations.translate(language, this, placeholders);
    }

}
