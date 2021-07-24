package org.playuniverse.minecraft.mcs.spigot.event.base;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.syntaxphoenix.syntaxapi.event.EventPriority;

@Retention(RUNTIME)
@Target(METHOD)
public @interface BukkitEventHandler {
    
    BukkitPriority bukkitPriority() default BukkitPriority.NORMAL;

    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancel() default false;

}
