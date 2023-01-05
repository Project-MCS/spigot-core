package org.playuniverse.minecraft.vcompat.reflection.provider;

import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;
import org.playuniverse.minecraft.vcompat.reflection.BukkitConversion;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContext;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsEntityType;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.BukkitContextImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.BukkitTypeImpl;

import com.syntaxphoenix.syntaxapi.nbt.NbtByte;
import com.syntaxphoenix.syntaxapi.nbt.NbtByteArray;
import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtDouble;
import com.syntaxphoenix.syntaxapi.nbt.NbtEnd;
import com.syntaxphoenix.syntaxapi.nbt.NbtFloat;
import com.syntaxphoenix.syntaxapi.nbt.NbtInt;
import com.syntaxphoenix.syntaxapi.nbt.NbtIntArray;
import com.syntaxphoenix.syntaxapi.nbt.NbtList;
import com.syntaxphoenix.syntaxapi.nbt.NbtLong;
import com.syntaxphoenix.syntaxapi.nbt.NbtLongArray;
import com.syntaxphoenix.syntaxapi.nbt.NbtShort;
import com.syntaxphoenix.syntaxapi.nbt.NbtString;
import com.syntaxphoenix.syntaxapi.nbt.NbtTag;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class BukkitConversionImpl extends BukkitConversion<VersionControlImpl> {

    protected BukkitConversionImpl(VersionControlImpl versionControl) {
        super(versionControl);
    }

    @Override
    public EntityType toEntityType(NmsEntityType type) {
        try {
            return EntityType.valueOf(type.name());
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }

    @Override
    public NmsEntityType fromEntityType(EntityType type) {
        try {
            return NmsEntityType.valueOf(type.name());
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }

    @Override
    public Tag toMinecraftTag(NbtTag tag) {
        switch (tag.getType()) {
        case END:
            return EndTag.INSTANCE;
        case BYTE:
            return ByteTag.valueOf(((NbtByte) tag).getValue());
        case BYTE_ARRAY:
            return new ByteArrayTag(((NbtByteArray) tag).getValue());
        case DOUBLE:
            return DoubleTag.valueOf(((NbtDouble) tag).getValue());
        case FLOAT:
            return FloatTag.valueOf(((NbtFloat) tag).getValue());
        case INT:
            return IntTag.valueOf(((NbtInt) tag).getValue());
        case INT_ARRAY:
            return new IntArrayTag(((NbtIntArray) tag).getValue());
        case LONG:
            return LongTag.valueOf(((NbtLong) tag).getValue());
        case LONG_ARRAY:
            return new LongArrayTag(((NbtLongArray) tag).getValue());
        case SHORT:
            return ShortTag.valueOf(((NbtShort) tag).getValue());
        case STRING:
            return StringTag.valueOf(((NbtString) tag).getValue());
        case LIST:
            return toMinecraftList((NbtList<?>) tag);
        case COMPOUND:
            return toMinecraftCompound((NbtCompound) tag);
        default:
            return null;
        }
    }

    @Override
    public NbtTag fromMinecraftTag(Object tag) {
        if (tag != null && tag instanceof Tag) {
            return fromMinecraftTag0((Tag) tag);
        }
        return null;
    }

    public NbtTag fromMinecraftTag0(Tag tag) {
        switch (NbtType.getById(tag.getId())) {
        case END:
            return NbtEnd.INSTANCE;
        case BYTE:
            return new NbtByte(((ByteTag) tag).getAsByte());
        case BYTE_ARRAY:
            return new NbtByteArray(((ByteArrayTag) tag).getAsByteArray());
        case DOUBLE:
            return new NbtDouble(((DoubleTag) tag).getAsDouble());
        case FLOAT:
            return new NbtFloat(((FloatTag) tag).getAsFloat());
        case INT:
            return new NbtInt(((IntTag) tag).getAsInt());
        case INT_ARRAY:
            return new NbtIntArray(((IntArrayTag) tag).getAsIntArray());
        case LONG:
            return new NbtLong(((LongTag) tag).getAsLong());
        case LONG_ARRAY:
            return new NbtLongArray(((LongArrayTag) tag).getAsLongArray());
        case SHORT:
            return new NbtShort(((ShortTag) tag).getAsShort());
        case STRING:
            return new NbtString(((StringTag) tag).getAsString());
        case LIST:
            return fromMinecraftList(tag);
        case COMPOUND:
            return fromMinecraftCompound(tag);
        default:
            return null;
        }
    }

    @Override
    public ListTag toMinecraftList(NbtList<?> list) {
        ListTag output = new ListTag();
        for (NbtTag tag : list) {
            output.add(toMinecraftTag(tag));
        }
        return output;
    }

    @Override
    public NbtList<NbtTag> fromMinecraftList(Object raw) {
        if (!(raw instanceof ListTag)) {
            return null;
        }
        ListTag list = (ListTag) raw;
        NbtList<NbtTag> output = new NbtList<>(NbtType.getById(list.getElementType()));
        for (Tag base : list) {
            output.add(fromMinecraftTag(base));
        }
        return output;
    }

    @Override
    public CompoundTag toMinecraftCompound(NbtCompound compound) {
        NbtCompound compoundTag = compound;
        CompoundTag targetCompound = new CompoundTag();
        for (String key : compoundTag.getKeys()) {
            targetCompound.put(key, toMinecraftTag(compoundTag.get(key)));
        }
        return targetCompound;
    }

    @Override
    public NbtCompound fromMinecraftCompound(Object raw) {
        if (!(raw instanceof CompoundTag)) {
            return null;
        }
        CompoundTag compoundTag = (CompoundTag) raw;
        NbtCompound targetCompound = new NbtCompound();
        for (String key : compoundTag.getAllKeys()) {
            targetCompound.set(key, fromMinecraftTag(compoundTag.get(key)));
        }
        return targetCompound;
    }

    @Override
    public org.bukkit.inventory.ItemStack itemFromCompound(NbtCompound compound) {
        return CraftItemStack.asBukkitCopy(ItemStack.of(toMinecraftCompound(compound)));
    }

    @Override
    public NbtCompound itemToCompound(org.bukkit.inventory.ItemStack itemStack) {
        return fromMinecraftCompound(CraftItemStack.asNMSCopy(itemStack).save(new CompoundTag()));
    }

    @Override
    public WrappedContext<IDataAdapterContext> createContext(IDataAdapterContext context) {
        return new BukkitContextImpl(context);
    }

    @Override
    public <P, C> WrapType<P, C> wrap(IDataType<P, C> dataType) {
        return new BukkitTypeImpl<>(dataType);
    }

}