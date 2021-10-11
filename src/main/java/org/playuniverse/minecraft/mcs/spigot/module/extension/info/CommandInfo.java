package org.playuniverse.minecraft.mcs.spigot.module.extension.info;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandInfo {

    public String name();

    public String prefix() default "";

    public String[] aliases() default {};

}