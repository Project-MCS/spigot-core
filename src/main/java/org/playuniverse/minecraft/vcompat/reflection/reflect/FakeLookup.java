package org.playuniverse.minecraft.vcompat.reflection.reflect;

import org.playuniverse.minecraft.vcompat.reflection.reflect.handle.ClassLookup;

public class FakeLookup extends ClassLookup {

    public static final FakeLookup FAKE = new FakeLookup();

    private FakeLookup() {
        super((Class<?>) null);
    }

}