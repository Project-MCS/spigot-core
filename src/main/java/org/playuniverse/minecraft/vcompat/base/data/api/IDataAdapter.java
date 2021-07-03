package org.playuniverse.minecraft.vcompat.base.data.api;

public interface IDataAdapter<P, C, B> {

    Class<B> getBaseType();
    
    Class<P> getPrimitiveType();
    
    Class<C> getComplexType();

    P extract(B input);

    C build(Object input);

    boolean isInstance(B base);

}
