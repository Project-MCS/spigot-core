package org.playuniverse.minecraft.mcs.spigot.compatibility;

import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.utils.plugin.PluginPackage;

public abstract class CompatibilityAddon {

	public abstract void onEnable(PluginPackage pluginPackage, SpigotCore core) throws Exception;

	public abstract void onDisable(SpigotCore core) throws Exception;

	public CompatibilityAddonConfig<?> getConfig() {
		return null;
	}

}
