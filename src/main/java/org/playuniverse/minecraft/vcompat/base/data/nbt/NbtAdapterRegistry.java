package org.playuniverse.minecraft.vcompat.base.data.nbt;

import java.util.function.Function;

import org.playuniverse.minecraft.vcompat.base.data.AbstractDataAdapterRegistry;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapter;

import com.syntaxphoenix.syntaxapi.nbt.NbtTag;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

public class NbtAdapterRegistry extends AbstractDataAdapterRegistry<NbtTag> {

    @Override
    public Object extract(NbtTag base) {
        if (base.getType() == NbtType.END) {
            return null;
        }
        return super.extract(base);
    }

    @Override
    public Class<NbtTag> getBase() {
        return NbtTag.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <P, C extends NbtTag> IDataAdapter<P, C, NbtTag> build(Class<?> clazz) {
        return (IDataAdapter<P, C, NbtTag>) NbtAdapter.createAdapter(this, clazz);
    }

    @Override
    public <P, C extends NbtTag> IDataAdapter<P, C, NbtTag> create(Class<P> primitiveType, Class<C> complexType, Function<P, C> builder,
        Function<C, P> extractor) {
        return new NbtAdapter<>(primitiveType, complexType, builder, extractor);
    }

}
