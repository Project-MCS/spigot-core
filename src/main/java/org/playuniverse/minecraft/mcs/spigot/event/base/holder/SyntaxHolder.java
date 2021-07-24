package org.playuniverse.minecraft.mcs.spigot.event.base.holder;

import com.syntaxphoenix.syntaxapi.event.EventPriority;

import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitPriority;

import com.syntaxphoenix.syntaxapi.event.EventHandler;

public class SyntaxHolder extends HandlerHolder<EventHandler> {

    private final EventHandler handler;

    public SyntaxHolder(EventHandler handler) {
        this.handler = handler;
    }

    @Override
    public EventHandler getHandle() {
        return handler;
    }

    @Override
    public EventPriority priority() {
        return handler.priority();
    }

    @Override
    public BukkitPriority bukkitPriority() {
        return BukkitPriority.NORMAL;
    }

    @Override
    public boolean ignoreCancel() {
        return handler.ignoreCancel();
    }

}
