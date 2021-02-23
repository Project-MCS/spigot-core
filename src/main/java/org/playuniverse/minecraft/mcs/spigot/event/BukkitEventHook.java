package org.playuniverse.minecraft.mcs.spigot.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.utils.wait.Awaiter;

public class BukkitEventHook implements Listener, EventExecutor {

    private final BukkitEventManager manager;

    public BukkitEventHook(BukkitEventManager manager) {
        this.manager = manager;
    }

    public void registerEvent(Class<? extends Event> event) {
        Bukkit.getPluginManager().registerEvent(event, this, EventPriority.HIGHEST, this, SpigotCore.get());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        Awaiter.of(manager.call(event)).await();
    }

}
