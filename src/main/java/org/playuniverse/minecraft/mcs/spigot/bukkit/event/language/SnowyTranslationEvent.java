package org.playuniverse.minecraft.mcs.spigot.bukkit.event.language;

import org.bukkit.event.HandlerList;
import org.playuniverse.minecraft.mcs.spigot.bukkit.event.SpigotEvent;
import org.playuniverse.minecraft.mcs.spigot.language.Translations.TranslationManager;
import org.playuniverse.minecraft.mcs.spigot.language.Translations.TranslationManager.TranslationStorage;

public class SnowyTranslationEvent extends SpigotEvent {

	private final TranslationManager manager;
	private final TranslationStorage storage;

	public SnowyTranslationEvent(TranslationManager manager, TranslationStorage storage) {
		this.manager = manager;
		this.storage = storage;
	}

	public final TranslationManager getManager() {
		return manager;
	}

	public final TranslationStorage getStorage() {
		return storage;
	}

	/*
	 * Bukkit Stuff
	 */

	public static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
