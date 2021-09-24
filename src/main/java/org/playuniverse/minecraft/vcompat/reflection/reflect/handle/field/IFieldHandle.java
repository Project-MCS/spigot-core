package org.playuniverse.minecraft.vcompat.reflection.reflect.handle.field;

public interface IFieldHandle<O> {
    
    Object getValue();
    
    Object getValue(Object source);
    
    IFieldHandle<O> setValue(Object value);
    
    IFieldHandle<O> setValue(Object source, Object value);
    
    O getHandle();
    
    boolean isUnsafe();

}
