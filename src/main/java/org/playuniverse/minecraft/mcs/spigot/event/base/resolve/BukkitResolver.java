package org.playuniverse.minecraft.mcs.spigot.event.base.resolve;

import java.lang.reflect.Method;

import org.playuniverse.minecraft.mcs.spigot.event.base.BukkitEventHandler;
import org.playuniverse.minecraft.mcs.spigot.event.base.holder.HandlerHolder;

public final class BukkitResolver extends MethodResolver<BukkitEventHandler> {
    
    BukkitResolver() {}

    @Override
    public Class<BukkitEventHandler> getIndicator() {
        return BukkitEventHandler.class;
    }

    @Override
    public HandlerHolder<BukkitEventHandler> resolve(Method method) {
        // TODO Auto-generated method stub
        return null;
    }

}
