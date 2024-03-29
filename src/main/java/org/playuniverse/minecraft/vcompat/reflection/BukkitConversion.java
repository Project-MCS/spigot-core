package org.playuniverse.minecraft.vcompat.reflection;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContext;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsEntityType;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtList;
import com.syntaxphoenix.syntaxapi.nbt.NbtTag;

public abstract class BukkitConversion<V extends VersionControl> extends VersionHandler<V> {

    protected BukkitConversion(V versionControl) {
        super(versionControl);
    }

    public abstract EntityType toEntityType(NmsEntityType type);

    public abstract NmsEntityType fromEntityType(EntityType type);

    public abstract Object toMinecraftTag(NbtTag tag);

    public abstract NbtTag fromMinecraftTag(Object tag);

    public abstract Object toMinecraftList(NbtList<?> list);

    public abstract NbtList<?> fromMinecraftList(Object list);

    public abstract Object toMinecraftCompound(NbtCompound compound);

    public abstract NbtCompound fromMinecraftCompound(Object compound);

    public abstract ItemStack itemFromCompound(NbtCompound compound);

    public abstract NbtCompound itemToCompound(ItemStack itemStack);

    public abstract WrappedContext<IDataAdapterContext> createContext(IDataAdapterContext context);
    
    public abstract <P, C> WrapType<P, C> wrap(IDataType<P, C> dataType);

}