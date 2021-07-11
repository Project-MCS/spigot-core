package org.playuniverse.minecraft.mcs.spigot.bukkit.inventory;

import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.TradeSelectEvent;

public interface GuiHandler {

    default void onInit(GuiInventory inventory) {}

    default void onPreAnvil(GuiInventory inventory, PrepareAnvilEvent event) {}

    default void onPreCraft(GuiInventory inventory, PrepareItemCraftEvent event) {}

    default void onPreSmith(GuiInventory inventory, PrepareSmithingEvent event) {}

    default boolean onClose(GuiInventory inventory, InventoryCloseEvent event) {
        return false;
    }

    default boolean onOpen(GuiInventory inventory, InventoryOpenEvent event) {
        return false;
    }

    default boolean onPreEnchant(GuiInventory inventory, PrepareItemEnchantEvent event) {
        return false;
    }

    default boolean onEnchant(GuiInventory inventory, EnchantItemEvent event) {
        return false;
    }

    default boolean onClick(GuiInventory inventory, InventoryClickEvent event) {
        return false;
    }

    default boolean onCraft(GuiInventory inventory, CraftItemEvent event) {
        return false;
    }

    default boolean onCreative(GuiInventory inventory, InventoryCreativeEvent event) {
        return false;
    }

    default boolean onDrag(GuiInventory inventory, InventoryDragEvent event) {
        return false;
    }

    default boolean onTradeSelect(GuiInventory inventory, TradeSelectEvent event) {
        return false;
    }

    default boolean onItemMove(GuiInventory inventory, InventoryMoveItemEvent event, MoveInventory state) {
        return false;
    }

    default void onUpdate(GuiInventory inventory) {
        
    }

}
