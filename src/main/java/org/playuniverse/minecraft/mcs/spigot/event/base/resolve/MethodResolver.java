package org.playuniverse.minecraft.mcs.spigot.event.base.resolve;

import java.lang.reflect.Method;

import org.playuniverse.minecraft.mcs.spigot.event.base.holder.HandlerHolder;

public abstract class MethodResolver<E> {

    public static final MethodResolver<?>[] RESOLVERS = new MethodResolver<?>[] {
        new BukkitResolver(),
        new SyntaxResolver()
    };

    public abstract Class<E> getIndicator();

    public abstract HandlerHolder<E> resolve(Method method);

}
