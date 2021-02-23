package org.playuniverse.minecraft.mcs.spigot.plugin;

import org.pf4j.PluginWrapper;

public class PluginDisableEvent extends PluginEvent {

	public PluginDisableEvent(SafePluginManager manager, PluginWrapper wrapper) {
		super(manager, wrapper);
	}

}
