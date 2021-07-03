package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import java.util.Arrays;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;

public abstract class WrappedTypeImpl<H, P0, P1, C0, C1> implements WrapType<P0, C0> {

    protected final Class<P0> primitiveType;
    protected final Class<C0> complexType;

    private final int primitiveWrap;
    private final int complexWrap;

    @SuppressWarnings("unchecked")
    protected WrappedTypeImpl(Class<P1> primitive, Class<C1> complex) {
        this.primitiveWrap = WrappedTypeImpl.internalState(primitive);
        this.complexWrap = WrappedTypeImpl.internalState(complex);
        this.primitiveType = (Class<P0>) WrappedTypeImpl.internalWrap(primitive, primitiveWrap);
        this.complexType = (Class<C0>) WrappedTypeImpl.internalWrap(complex, complexWrap);
    }

    public abstract H getHandle();

    public Class<P0> getPrimitiveWrapped() {
        return primitiveType;
    }

    public Class<C0> getComplexWrapped() {
        return complexType;
    }

    public abstract Class<P1> getPrimitiveOriginal();

    public abstract Class<C1> getComplexOriginal();

    @SuppressWarnings("unchecked")
    public P0 toPrimitiveWrapped(P1 primitive) {
        switch (primitiveWrap) {
        case 1:
            return (P0) new SyntaxContainerImpl((PersistentDataContainer) primitive);
        case 2:
            return (P0) Arrays.stream((PersistentDataContainer[]) primitive).map(SyntaxContainerImpl::new)
                .toArray(SyntaxContainerImpl[]::new);
        case 3:
            return (P0) new BukkitContainerImpl((IDataContainer) primitive);
        case 4:
            return (P0) Arrays.stream((IDataContainer[]) primitive).map(BukkitContainerImpl::new)
                .toArray(BukkitContainerImpl[]::new);
        default:
            return (P0) primitive;
        }
    }

    @SuppressWarnings("unchecked")
    public C0 toComplexWrapped(C1 complex) {
        switch (complexWrap) {
        case 1:
            return (C0) new SyntaxContainerImpl((PersistentDataContainer) complex);
        case 2:
            return (C0) Arrays.stream((PersistentDataContainer[]) complex).map(SyntaxContainerImpl::new).toArray(SyntaxContainerImpl[]::new);
        case 3:
            return (C0) new BukkitContainerImpl((IDataContainer) complex);
        case 4:
            return (C0) Arrays.stream((IDataContainer[]) complex).map(BukkitContainerImpl::new).toArray(BukkitContainerImpl[]::new);
        default:
            return (C0) complex;
        }
    }

    @SuppressWarnings("unchecked")
    public P1 toPrimitiveOriginal(P0 primitive) {
        switch (primitiveWrap) {
        case 1:
            return (P1) new BukkitContainerImpl((IDataContainer) primitive);
        case 2:
            return (P1) Arrays.stream((IDataContainer[]) primitive).map(BukkitContainerImpl::new)
                .toArray(BukkitContainerImpl[]::new);
        case 3:
            return (P1) new SyntaxContainerImpl((PersistentDataContainer) primitive);
        case 4:
            return (P1) Arrays.stream((PersistentDataContainer[]) primitive).map(SyntaxContainerImpl::new)
                .toArray(SyntaxContainerImpl[]::new);
        default:
            return (P1) primitive;
        }
    }

    @SuppressWarnings("unchecked")
    public C1 toComplexOriginal(C0 complex) {
        switch (complexWrap) {
        case 1:
            return (C1) new BukkitContainerImpl((IDataContainer) complex);
        case 2:
            return (C1) Arrays.stream((IDataContainer[]) complex).map(BukkitContainerImpl::new).toArray(BukkitContainerImpl[]::new);
        case 3:
            return (C1) new SyntaxContainerImpl((PersistentDataContainer) complex);
        case 4:
            return (C1) Arrays.stream((PersistentDataContainer[]) complex).map(SyntaxContainerImpl::new).toArray(SyntaxContainerImpl[]::new);
        default:
            return (C1) complex;
        }
    }

    protected static Class<?> internalWrap(Class<?> clazz, int state) {
        switch (state) {
        case 1:
            return SyntaxContainerImpl.class;
        case 2:
            return SyntaxContainerImpl[].class;
        case 3:
            return BukkitContainerImpl.class;
        case 4:
            return BukkitContainerImpl[].class;
        default:
            return clazz;
        }
    }

    protected static int internalState(Class<?> clazz) {
        if (clazz.isAssignableFrom(PersistentDataContainer.class)) {
            return 1;
        }
        if (clazz.isAssignableFrom(PersistentDataContainer[].class)) {
            return 2;
        }
        if (clazz.isAssignableFrom(IDataContainer.class)) {
            return 3;
        }
        if (clazz.isAssignableFrom(IDataContainer[].class)) {
            return 4;
        }
        return 0;
    }

    public static <A, B, C, D> BukkitTypeImpl<C, A, D, B> wrap(IDataType<A, B> type) {
        return new BukkitTypeImpl<>(type);
    }

    public static <A, B, C, D> SyntaxTypeImpl<C, A, D, B> wrap(PersistentDataType<A, B> type) {
        return new SyntaxTypeImpl<>(type);
    }

}