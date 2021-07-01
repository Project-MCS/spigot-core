package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;

public class SimpleBukkitTypeImpl<P, C> implements PersistentDataType<P, C> {

    private final WrapType<P, C> type;

    public SimpleBukkitTypeImpl(WrapType<P, C> type) {
        this.type = type;
    }

    @Override
    public Class<C> getComplexType() {
        return type.getComplexWrapped();
    }

    @Override
    public Class<P> getPrimitiveType() {
        return type.getPrimitiveWrapped();
    }

    @Override
    public P toPrimitive(C complex, PersistentDataAdapterContext context) {
        return type.wrapToPrimitive(complex, new SyntaxContextImpl(context));
    }

    @Override
    public C fromPrimitive(P primitive, PersistentDataAdapterContext context) {
        return type.wrapToComplex(primitive, new SyntaxContextImpl(context));
    }

}