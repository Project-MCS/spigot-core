package org.playuniverse.minecraft.vcompat.reflection.provider;

import org.bukkit.persistence.PersistentDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.reflection.DataProvider;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.BukkitContainerImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.SyntaxContainerImpl;

public class DataProviderImpl extends DataProvider<VersionControlImpl> {

    protected DataProviderImpl(VersionControlImpl versionControl) {
        super(versionControl);
    }

    @Override
    public WrappedContainer wrap(Object container) {
        if (container instanceof PersistentDataContainer) {
            return new SyntaxContainerImpl((PersistentDataContainer) container);
        }
        if (container instanceof IDataContainer) {
            return new BukkitContainerImpl((IDataContainer) container);
        }
        return null;
    }

}
