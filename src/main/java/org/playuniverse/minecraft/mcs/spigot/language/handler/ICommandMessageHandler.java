package org.playuniverse.minecraft.mcs.spigot.language.handler;

import org.bukkit.command.CommandSender;
import org.playuniverse.minecraft.mcs.spigot.language.IMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.Language;
import org.playuniverse.minecraft.mcs.spigot.language.LanguageProvider;

public interface ICommandMessageHandler<S extends CommandSender> extends IMessageHandler<S> {

    @Override
    default Language getLanguage(S receiver) {
        return LanguageProvider.getLanguageOf(receiver);
    }

    @Override
    default void setLanguage(S receiver, Language language) {
        LanguageProvider.setLanguageOf(receiver, language);
    }

    @Override
    default void setLanguageIfNotExists(S receiver, Language language) {
        LanguageProvider.setLanguageOfIfNotExists(receiver, language);
    }

}
