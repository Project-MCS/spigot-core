package org.playuniverse.minecraft.mcs.spigot.language;

import static org.playuniverse.minecraft.mcs.spigot.language.TranslationType.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.playuniverse.minecraft.mcs.spigot.bukkit.event.language.SnowyTranslationEvent;
import org.playuniverse.minecraft.mcs.spigot.language.Translations.TranslationManager.TranslationStorage;

public class FallbackHandler implements Listener {

    @EventHandler
    public void onLoad(SnowyTranslationEvent event) {

        TranslationStorage storage = event.getStorage();

        for (Message message : Message.values()) {
            storage.set(message.translationId(), MESSAGE, message.value());
        }

        for (Variable variable : Variable.values()) {
            storage.set(variable.translationId(), VARIABLE, variable.value());
        }

    }

}
