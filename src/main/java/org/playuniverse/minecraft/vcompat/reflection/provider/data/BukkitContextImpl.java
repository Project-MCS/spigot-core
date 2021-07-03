package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContext;

public final class BukkitContextImpl extends WrappedContext<IDataAdapterContext> implements PersistentDataAdapterContext {

    private final IDataAdapterContext context;

    public BukkitContextImpl(IDataAdapterContext context) {
        this.context = context;
    }

    @Override
    public IDataAdapterContext getHandle() {
        return context;
    }

    @Override
    public PersistentDataContainer newPersistentDataContainer() {
        return newWrapContainer();
    }

    @Override
    public IDataContainer newContainer() {
        return context.newContainer();
    }

    @Override
    public BukkitContainerImpl newWrapContainer() {
        return new BukkitContainerImpl(context.newContainer());
    }

}