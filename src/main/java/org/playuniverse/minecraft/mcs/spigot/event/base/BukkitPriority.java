package org.playuniverse.minecraft.mcs.spigot.event.base;

import org.bukkit.event.EventPriority;

public enum BukkitPriority {

    MONITOR(EventPriority.MONITOR),
    LOWEST(EventPriority.HIGHEST),
    LOW(EventPriority.HIGH),
    NORMAL(EventPriority.NORMAL),
    HIGH(EventPriority.LOW),
    HIGHEST(EventPriority.LOWEST);

    /*
     * 
     */

    private EventPriority bukkit;

    private BukkitPriority(EventPriority bukkit) {
        this.bukkit = bukkit;
    }
    
    public EventPriority asBukkit() {
        return bukkit;
    }

}
