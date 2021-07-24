package org.playuniverse.minecraft.mcs.spigot.event.base.resolve;

import java.lang.reflect.Method;

import org.playuniverse.minecraft.mcs.spigot.event.base.holder.HandlerHolder;
import org.playuniverse.minecraft.mcs.spigot.event.base.holder.SyntaxHolder;

import com.syntaxphoenix.syntaxapi.event.EventHandler;

public final class SyntaxResolver extends MethodResolver<EventHandler> {

    SyntaxResolver() {}

    @Override
    public Class<EventHandler> getIndicator() {
        return EventHandler.class;
    }

    @Override
    public HandlerHolder<EventHandler> resolve(Method method) {
        EventHandler handler;
        try {
            handler = method.getAnnotation(EventHandler.class);
        } catch (NullPointerException e) {
            return null;
        }
        if (handler == null) {
            return null;
        }
        return new SyntaxHolder(handler);
    }

}
