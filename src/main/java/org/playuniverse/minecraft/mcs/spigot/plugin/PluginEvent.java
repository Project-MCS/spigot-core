package org.playuniverse.minecraft.mcs.spigot.plugin;

import org.pf4j.PluginWrapper;

import com.syntaxphoenix.syntaxapi.event.Event;

public abstract class PluginEvent extends Event {

	private final SafePluginManager manager;
	private final PluginWrapper wrapper;

	public PluginEvent(SafePluginManager manager, PluginWrapper wrapper) {
		this.manager = manager;
		this.wrapper = wrapper;
	}

	/*
	 * Getter
	 */

	public final SafePluginManager getPluginManager() {
		return manager;
	}

	public final PluginWrapper getPlugin() {
		return wrapper;
	}

}
