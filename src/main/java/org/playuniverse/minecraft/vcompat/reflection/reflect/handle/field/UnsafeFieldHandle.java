package org.playuniverse.minecraft.vcompat.reflection.reflect.handle.field;

import sun.misc.Unsafe;

public abstract class UnsafeFieldHandle<O> implements IFieldHandle<O> {
    
    protected static final Unsafe UNSAFE = Unsafe.getUnsafe();
    
    protected final IFieldHandle<O> setMemoryValue(Object base, long offset, Object value) {
        UNSAFE.putObject(base, offset, value);
        return this;
    }
    
    protected final Object getMemoryValue(Object base, long offset) {
        return UNSAFE.getObject(base, offset);
    }
    
    @Override
    public final boolean isUnsafe() {
        return true;
    }

}
