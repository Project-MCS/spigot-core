package org.playuniverse.minecraft.mcs.spigot.bukkit.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inventory.handler.BasicClickHandler;

public abstract class BasicGuiHandler implements BasicClickHandler, GuiHandler {

	@Override
	public boolean onClick(GuiInventory inventory, InventoryClickEvent event) {
		return handleClickAction(inventory, event);
	}

}
