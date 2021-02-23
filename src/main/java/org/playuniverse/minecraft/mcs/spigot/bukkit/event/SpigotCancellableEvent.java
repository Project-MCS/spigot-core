package org.playuniverse.minecraft.mcs.spigot.bukkit.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public abstract class SpigotCancellableEvent extends SpigotEvent implements Cancellable {

	public SpigotCancellableEvent() {
		this(false);
	}

	public SpigotCancellableEvent(boolean async) {
		super(async);
	}

	/*
	 * Cancel
	 */

	protected boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
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
