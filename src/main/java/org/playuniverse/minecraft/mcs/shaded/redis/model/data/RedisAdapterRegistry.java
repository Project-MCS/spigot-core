package org.playuniverse.minecraft.mcs.shaded.redis.model.data;

import java.util.function.Function;

import org.playuniverse.minecraft.mcs.shaded.redis.model.RModel;
import org.playuniverse.minecraft.vcompat.base.data.AbstractDataAdapterRegistry;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapter;

public final class RedisAdapterRegistry extends AbstractDataAdapterRegistry<RModel> {

    public static final RedisAdapterRegistry GLOBAL = new RedisAdapterRegistry();

    private RedisAdapterRegistry() {}

    @Override
    public Class<RModel> getBase() {
        return RModel.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <P, C extends RModel> IDataAdapter<P, C, RModel> build(Class<?> clazz) {
        return (IDataAdapter<P, C, RModel>) RedisAdapter.createAdapter(clazz);
    }

    @Override
    public <P, C extends RModel> IDataAdapter<P, C, RModel> create(Class<P> primitiveType, Class<C> complexType, Function<P, C> builder,
        Function<C, P> extractor) {
        return new RedisAdapter<>(primitiveType, complexType, builder, extractor);
    }

}
