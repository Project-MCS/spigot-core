package org.playuniverse.minecraft.vcompat.base.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapter;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterRegistry;

import com.google.common.base.Objects;

public abstract class AbstractDataAdapterRegistry<B> implements IDataAdapterRegistry<B> {

    protected final List<IDataAdapter<?, ? extends B, B>> adapters = Collections.synchronizedList(new ArrayList<>());

    protected abstract <P, C extends B> IDataAdapter<P, C, B> build(Class<?> clazz);

    public abstract <P, C extends B> IDataAdapter<P, C, B> create(Class<P> primitiveType, Class<C> complexType, Function<P, C> builder,
        Function<C, P> extractor);

    @Override
    public B wrap(Object value) {
        if (value == null) {
            return null;
        }
        IDataAdapter<?, ? extends B, B> adapter = find(value.getClass(), IDataAdapter::getPrimitiveType);
        if (adapter == null) {
            return null;
        }
        return adapter.getPrimitiveType().isInstance(value) ? adapter.build(value) : null;
    }

    @Override
    public Object extract(B base) {
        if (base == null) {
            return null;
        }
        IDataAdapter<?, ? extends B, B> adapter = find(base.getClass(), IDataAdapter::getComplexType);
        return adapter == null ? null : adapter.extract(base);
    }

    @Override
    public boolean has(Class<?> clazz) {
        return adapters.stream()
            .anyMatch(adapter -> Objects.equal(clazz, adapter.getPrimitiveType()) || Objects.equal(clazz, adapter.getComplexType()));
    }

    private IDataAdapter<?, ? extends B, B> find(Class<?> clazz, Function<IDataAdapter<?, ? extends B, B>, Class<?>> mapper) {
        return adapters.stream().filter(adapter -> clazz.isAssignableFrom(mapper.apply(adapter))).findAny().orElseGet(() -> build(clazz));
    }

}
