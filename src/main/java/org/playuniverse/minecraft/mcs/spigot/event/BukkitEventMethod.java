package org.playuniverse.minecraft.mcs.spigot.event;

import java.lang.reflect.Method;

import org.bukkit.event.Event;
import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitPriority;
import org.playuniverse.minecraft.mcs.spigot.event.base.holder.HandlerHolder;

import com.syntaxphoenix.syntaxapi.event.EventListener;
import com.syntaxphoenix.syntaxapi.event.EventPriority;
import com.syntaxphoenix.syntaxapi.reflection.ReflectionTools;

public class BukkitEventMethod {

    private final EventListener listener;
    private final Method method;

    private final HandlerHolder<?> handler;

    public BukkitEventMethod(EventListener listener, Method method, HandlerHolder<?> handler) {
        this.listener = listener;
        this.method = method;
        this.handler = handler;
    }

    public boolean isValid() {
        return ReflectionTools.hasSameArguments(new Class<?>[] {
            Event.class
        }, method.getParameterTypes());
    }

    public final boolean hasEventHandler() {
        return getHandler() != null;
    }

    @SuppressWarnings("unchecked")
    public final Class<? extends Event> getEvent() {
        return (Class<? extends Event>) method.getParameterTypes()[0];
    }

    public final HandlerHolder<?> getHandler() {
        return handler;
    }

    public final boolean ignoresCancel() {
        return handler != null ? handler.ignoreCancel() : false;
    }

    public final EventPriority priortiy() {
        return handler != null ? handler.priority() : EventPriority.NORMAL;
    }

    public final BukkitPriority bukkitPriority() {
        return handler != null ? handler.bukkitPriority() : BukkitPriority.NORMAL;
    }

    public final Method getMethod() {
        return method;
    }

    public final EventListener getListener() {
        return listener;
    }

    public void execute(Event event) {
        ReflectionTools.execute(listener, method, event);
    }

}
