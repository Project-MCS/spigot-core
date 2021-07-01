package org.playuniverse.minecraft.vcompat.reflection.data.wrap;

import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContext;

import com.syntaxphoenix.syntaxapi.data.DataAdapterContext;
import com.syntaxphoenix.syntaxapi.data.IDataContainer;

public class SimpleSyntaxContext extends WrappedContext<DataAdapterContext> {

    private final DataAdapterContext context;

    public SimpleSyntaxContext(DataAdapterContext context) {
        this.context = context;
    }

    @Override
    public DataAdapterContext getHandle() {
        return context;
    }

    @Override
    public IDataContainer newDataContainer() {
        return context.newDataContainer();
    }

    @Override
    public WrappedContainer newContainer() {
        return new SimpleSyntaxContainer<>(context.newDataContainer());
    }

}