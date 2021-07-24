package org.playuniverse.minecraft.mcs.spigot.event.base.resolve;

import java.lang.reflect.Method;

import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitEventHandler;
import org.playuniverse.minecraft.mcs.spigot.event.base.holder.BukkitHolder;
import org.playuniverse.minecraft.mcs.spigot.event.base.holder.HandlerHolder;

public final class BukkitResolver extends MethodResolver<BukkitEventHandler> {

    BukkitResolver() {}

    @Override
    public Class<BukkitEventHandler> getIndicator() {
        return BukkitEventHandler.class;
    }

    @Override
    public HandlerHolder<BukkitEventHandler> resolve(Method method) {
        BukkitEventHandler handler;
        try {
            handler = method.getAnnotation(BukkitEventHandler.class);
        } catch (NullPointerException e) {
            return null;
        }
        if (handler == null) {
            return null;
        }
        return new BukkitHolder(handler);
    }

}
