package org.playuniverse.minecraft.mcs.spigot.plugin;

import org.pf4j.PluginWrapper;

public class PluginEnableEvent extends PluginEvent {

	public PluginEnableEvent(SafePluginManager manager, PluginWrapper wrapper) {
		super(manager, wrapper);
	}

}
