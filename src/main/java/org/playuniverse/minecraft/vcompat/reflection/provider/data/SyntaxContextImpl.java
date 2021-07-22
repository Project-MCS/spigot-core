package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContext;

public final class SyntaxContextImpl extends WrappedContext<PersistentDataAdapterContext> implements PersistentDataAdapterContext {

    private final PersistentDataAdapterContext context;

    public SyntaxContextImpl(PersistentDataAdapterContext context) {
        this.context = context;
    }

    @Override
    public PersistentDataAdapterContext getHandle() {
        return context;
    }

    @Override
    public PersistentDataContainer newPersistentDataContainer() {
        return context.newPersistentDataContainer();
    }

    @Override
    public IDataContainer newContainer() {
        return newWrapContainer();
    }

    @Override
    public SyntaxContainerImpl newWrapContainer() {
        return new SyntaxContainerImpl(context.newPersistentDataContainer());
    }

}