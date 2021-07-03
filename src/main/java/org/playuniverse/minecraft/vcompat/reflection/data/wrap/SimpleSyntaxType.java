package org.playuniverse.minecraft.vcompat.reflection.data.wrap;

import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;
import org.playuniverse.minecraft.vcompat.reflection.VersionControl;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;

public class SimpleSyntaxType<P, C> implements IDataType<P, C> {

    private final WrapType<P, C> type;

    public SimpleSyntaxType(WrapType<P, C> type) {
        this.type = type;
    }

    @Override
    public Class<C> getComplex() {
        return type.getComplexWrapped();
    }

    @Override
    public Class<P> getPrimitive() {
        return type.getPrimitiveWrapped();
    }

    @Override
    public P toPrimitive(IDataAdapterContext context, C complex) {
        return type.wrapToPrimitive(complex, VersionControl.get().getBukkitConversion().createContext(context));
    }

    @Override
    public C fromPrimitive(IDataAdapterContext context, P primitive) {
        return type.wrapToComplex(primitive, VersionControl.get().getBukkitConversion().createContext(context));
    }

}