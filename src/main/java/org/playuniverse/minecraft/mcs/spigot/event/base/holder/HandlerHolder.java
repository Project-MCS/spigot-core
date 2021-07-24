package org.playuniverse.minecraft.mcs.spigot.event.base.holder;

import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitPriority;

import com.syntaxphoenix.syntaxapi.event.EventPriority;

public abstract class HandlerHolder<E> {
    
    public abstract E getHandle();
    
    public abstract EventPriority priority();
    
    public abstract BukkitPriority bukkitPriority();
    
    public abstract boolean ignoreCancel();

}
