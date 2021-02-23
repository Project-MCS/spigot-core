package org.playuniverse.minecraft.mcs.spigot.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class SpigotEvent extends Event {

	public SpigotEvent() {
		this(false);
	}

	public SpigotEvent(boolean async) {
		super(async);
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
