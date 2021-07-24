package org.playuniverse.minecraft.mcs.spigot.event;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;

import org.bukkit.event.Event;
import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitPriority;
import org.playuniverse.minecraft.mcs.spigot.event.base.holder.HandlerHolder;
import org.playuniverse.minecraft.mcs.spigot.event.base.resolve.MethodResolver;

import com.syntaxphoenix.syntaxapi.event.EventListener;
import com.syntaxphoenix.syntaxapi.reflection.AbstractReflect;
import com.syntaxphoenix.syntaxapi.reflection.Reflect;

public final class BukkitEventAnalyser {

    private final EventListener listener;
    private final AbstractReflect reflect;

    public BukkitEventAnalyser(EventListener listener) {
        this.listener = listener;
        this.reflect = new Reflect(listener.getClass()).searchMethodsByArguments("event", Event.class);
    }

    public AbstractReflect getReflect() {
        return reflect;
    }

    public EventListener getListener() {
        return listener;
    }

    @SuppressWarnings("unchecked")
    protected int registerEvents(BukkitEventManager manager) {
        String base = "event-";
        int current = 0;
        String name;
        HashMap<Class<? extends Event>, EnumMap<BukkitPriority, BukkitEventExecutor>> executors = new HashMap<>();
        while (reflect.containsMethod(name = (base + current))) {
            current++;
            Method method = reflect.getMethod(name);
            HandlerHolder<?> handler = null;
            for (MethodResolver<?> resolver : MethodResolver.RESOLVERS) {
                handler = resolver.resolve(method);
                if (handler != null) {
                    break;
                }
            }
            if (handler == null) {
                continue;
            }
            Class<? extends Event> clazz = (Class<? extends Event>) method.getParameterTypes()[0];
            BukkitPriority priority = handler.bukkitPriority();
            EnumMap<BukkitPriority, BukkitEventExecutor> map = executors.computeIfAbsent(clazz,
                (ignore) -> new EnumMap<>(BukkitPriority.class));
            BukkitEventExecutor executor = map.computeIfAbsent(priority,
                (ignore) -> new BukkitEventExecutor(manager, listener, priority, clazz));
            executor.add(handler.priority(), new BukkitEventMethod(listener, method, handler));
        }
        manager.registerExecutors(executors.values().stream().flatMap(map -> map.values().stream()).toList());
        return current;
    }

}
