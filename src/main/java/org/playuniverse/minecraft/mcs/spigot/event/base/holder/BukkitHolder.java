package org.playuniverse.minecraft.mcs.spigot.event.base.holder;

import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitPriority;
import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitEventHandler;

import com.syntaxphoenix.syntaxapi.event.EventPriority;

public class BukkitHolder extends HandlerHolder<BukkitEventHandler> {
    
    private final BukkitEventHandler handler;
    
    public BukkitHolder(BukkitEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public BukkitEventHandler getHandle() {
        return handler;
    }

    @Override
    public EventPriority priority() {
        return handler.priority();
    }

    @Override
    public BukkitPriority bukkitPriority() {
        return handler.bukkitPriority();
    }

    @Override
    public boolean ignoreCancel() {
        return handler.ignoreCancel();
    }

}
