package org.playuniverse.minecraft.mcs.spigot.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitPriority;
import org.playuniverse.minecraft.mcs.spigot.utils.wait.Awaiter;

public class BukkitEventHookMethod implements EventExecutor {

    private final BukkitPriority priority;
    private final BukkitEventManager manager;

    BukkitEventHookMethod(BukkitEventManager manager, BukkitPriority priority) {
        this.manager = manager;
        this.priority = priority;
    }

    public BukkitPriority getPriority() {
        return priority;
    }

    public BukkitEventManager getManager() {
        return manager;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        Awaiter.of(manager.call(event, priority)).await();
    }

}
