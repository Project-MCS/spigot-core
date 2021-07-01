package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContext;

import com.syntaxphoenix.syntaxapi.data.DataAdapterContext;
import com.syntaxphoenix.syntaxapi.data.IDataContainer;

public final class BukkitContextImpl extends WrappedContext<DataAdapterContext> implements PersistentDataAdapterContext {

    private final DataAdapterContext context;

    public BukkitContextImpl(DataAdapterContext context) {
        this.context = context;
    }

    @Override
    public DataAdapterContext getHandle() {
        return context;
    }

    @Override
    public PersistentDataContainer newPersistentDataContainer() {
        return newContainer();
    }

    @Override
    public IDataContainer newDataContainer() {
        return context.newDataContainer();
    }

    @Override
    public BukkitContainerImpl newContainer() {
        return new BukkitContainerImpl(context.newDataContainer());
    }

}