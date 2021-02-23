package org.playuniverse.minecraft.mcs.spigot.bukkit.event;

import org.bukkit.event.HandlerList;

public abstract class AsyncSpigotEvent extends SpigotEvent {

	public AsyncSpigotEvent() {
		super(true);
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
