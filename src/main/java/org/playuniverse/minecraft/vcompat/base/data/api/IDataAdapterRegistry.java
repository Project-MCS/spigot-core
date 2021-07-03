package org.playuniverse.minecraft.vcompat.base.data.api;

public interface IDataAdapterRegistry<B> {
    
    Class<B> getBase();
    
    boolean has(Class<?> clazz); // == isRegistered
    
    Object extract(B base);
    
    B wrap(Object value);

}
