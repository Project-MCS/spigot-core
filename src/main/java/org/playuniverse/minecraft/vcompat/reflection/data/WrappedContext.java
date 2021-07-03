package org.playuniverse.minecraft.vcompat.reflection.data;

import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;

public abstract class WrappedContext<H> implements IDataAdapterContext {

    public abstract H getHandle();

    public abstract WrappedContainer newWrapContainer();

}