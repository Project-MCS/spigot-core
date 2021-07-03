package org.playuniverse.minecraft.vcompat.reflection.data.wrap;

import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContext;

public class SimpleSyntaxContext extends WrappedContext<IDataAdapterContext> {

    private final IDataAdapterContext context;

    public SimpleSyntaxContext(IDataAdapterContext context) {
        this.context = context;
    }

    @Override
    public IDataAdapterContext getHandle() {
        return context;
    }

    @Override
    public IDataContainer newContainer() {
        return context.newContainer();
    }

    @Override
    public WrappedContainer newWrapContainer() {
        return new SimpleSyntaxContainer<>(context.newContainer());
    }

}