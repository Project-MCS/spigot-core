package org.playuniverse.minecraft.mcs.spigot.event;

import java.util.EnumMap;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitPriority;

public class BukkitEventHook implements Listener {

    private final BukkitEventManager manager;
    private final EnumMap<BukkitPriority, BukkitEventHookMethod> methods = new EnumMap<>(BukkitPriority.class);

    public BukkitEventHook(BukkitEventManager manager) {
        this.manager = manager;
    }

    private BukkitEventHookMethod buildMethod(BukkitPriority priority) {
        return new BukkitEventHookMethod(manager, priority);
    }

    public void registerEvent(Class<? extends Event> event, BukkitPriority priority) {
        Bukkit.getPluginManager().registerEvent(event, this, priority.asBukkit(), methods.computeIfAbsent(priority, this::buildMethod),
            SpigotCore.get());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
