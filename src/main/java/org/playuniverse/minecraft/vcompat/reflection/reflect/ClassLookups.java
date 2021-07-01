package org.playuniverse.minecraft.vcompat.reflection.reflect;

import java.util.function.Consumer;

import org.playuniverse.minecraft.vcompat.reflection.reflect.provider.GeneralReflections;

public abstract class ClassLookups {

    public static void globalSetup(ClassLookupProvider provider) {
        GeneralReflections.INSTANCE.setup(provider);
    }

    public abstract void setup(ClassLookupProvider provider);

    public static <T> T predicate(boolean condition, T value, Consumer<T> action) {
        if (condition) {
            action.accept(value);
        }
        return value;
    }

    public int priority() {
        return 0;
    }

}