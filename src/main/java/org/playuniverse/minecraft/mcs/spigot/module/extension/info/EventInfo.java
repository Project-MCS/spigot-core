package org.playuniverse.minecraft.mcs.spigot.module.extension.info;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.playuniverse.minecraft.mcs.spigot.module.extension.IListenerExtension;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE
})
public @interface EventInfo {

    public IListenerExtension.Target target();

}