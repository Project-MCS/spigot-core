package org.playuniverse.minecraft.mcs.spigot.bukkit.inventory;

import static java.lang.Math.*;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.playuniverse.minecraft.mcs.spigot.bukkit.inventory.utils.ColorString;
import org.playuniverse.minecraft.mcs.spigot.data.properties.IProperties;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class GuiInventory implements InventoryHolder, ItemStorage<GuiInventory> {

    private final IProperties properties;

    private final Container<Inventory> inventory = Container.of();

    private int size;
    private String name;
    private InventoryType type;
    
    private GuiHandler handler;
    
    private boolean inventoryChanged = false;

    protected GuiInventory(GuiBuilder builder) {
        this.properties = builder.properties();
        this.handler = Objects.requireNonNull(builder.handler());
        if (!builder.isInventoryValid()) {
            throw new IllegalStateException("Configured inventory is invalid");
        }
        this.name = builder.nameAsString();
        this.size = builder.size();
        this.type = builder.type();
        updateInventory(false);
        if (inventory.get().getHolder() != this) {
            throw new IllegalStateException("InventoryHolder isn't GuiInventory?");
        }
        handler.onInit(this);
    }

    /*
     * Update
     */
    
    public void updateHandler(GuiHandler handler) {
        this.handler = Objects.requireNonNull(handler);
        handler.onInit(this);
    }

    public void updateType(int size) {
        this.type = InventoryType.CHEST;
        this.size = size;
        updateInventory(true);
    }

    public void updateType(InventoryType type) {
        this.type = type;
        updateInventory(true);
    }

    public void updateName(ColorString name) {
        updateName(name.asColoredString());
    }

    public void updateName(String name) {
        this.name = name;
        updateInventory(true);
    }

    private void updateInventory(boolean trigger) {
        if (type == InventoryType.CHEST) {
            inventory.replace(Bukkit.createInventory(this, size, name));
        } else {
            inventory.replace(Bukkit.createInventory(this, type, name));
        }
        size = inventory.get().getSize();
        if (trigger) {
            inventoryChanged = true;
            update();
            inventoryChanged = false;
        }
    }

    public void update() {
        handler.onUpdate(this);
    }

    /*
     * Getter
     */
    
    public boolean isInventoryChanged() {
        return inventoryChanged;
    }

    public IProperties getProperties() {
        return properties;
    }

    public GuiHandler getHandler() {
        return handler;
    }

    @Override
    public Inventory getInventory() {
        return inventory.get();
    }

    @Override
    public int size() {
        return size;
    }

    /*
     * ItemStorage implementation
     */

    @Override
    public GuiInventory me() {
        return this;
    }

    @Override
    public ItemStack get(int id) {
        return inventory.get().getItem(min(id, size));
    }

    @Override
    public GuiInventory set(int id, ItemStack stack) {
        inventory.get().setItem(min(id, size), stack);
        return this;
    }

}
