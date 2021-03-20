package org.playuniverse.minecraft.mcs.spigot.bukkit.event.language;

import org.bukkit.event.HandlerList;
import org.playuniverse.minecraft.mcs.spigot.bukkit.event.SpigotEvent;

public class McsTranslationEvent extends SpigotEvent {

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
