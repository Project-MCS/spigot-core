package org.playuniverse.minecraft.vcompat.base.data;

import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterRegistry;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;

import com.syntaxphoenix.syntaxapi.utils.key.IKey;
import com.syntaxphoenix.syntaxapi.utils.key.NamespacedKey;

public abstract class AbstractDataContainer<B> implements IDataContainer {

    protected final IDataAdapterRegistry<B> registry;

    public AbstractDataContainer(IDataAdapterRegistry<B> registry) {
        this.registry = registry;
    }

    @Override
    public IDataAdapterRegistry<B> getRegistry() {
        return registry;
    }

    @Override
    public Object get(String key) {
        B raw = getRaw(key);
        if (raw == null) {
            return raw;
        }
        return registry.extract(raw);
    }

    @Override
    public <E> E get(String key, IDataType<?, E> type) {
        Object value = type.getPrimitive().isAssignableFrom(registry.getBase()) ? getRaw(key) : get(key);
        if (value == null || !type.isPrimitive(value)) {
            return null;
        }
        return type.fromPrimitiveObj(getContext(), value);
    }

    @Override
    public boolean has(String key, IDataType<?, ?> type) {
        if (!has(key)) {
            return false;
        }
        Object value = type.getPrimitive().isAssignableFrom(registry.getBase()) ? getRaw(key) : get(key);
        return (value == null || !type.isPrimitive(value));
    }

    @Override
    public <V, E> void set(String key, E value, IDataType<V, E> type) {
        set(key, registry.wrap(type.toPrimitive(getContext(), value)));
    }

    /*
     * Key conversion
     */

    @Override
    public Object get(IKey key) {
        return get(key.asString());
    }

    @Override
    public <E> E get(IKey key, IDataType<?, E> type) {
        return get(key.asString(), type);
    }

    @Override
    public <V, E> void set(IKey key, E value, IDataType<V, E> type) {
        set(key.asString(), value, type);
    }

    @Override
    public boolean has(IKey key) {
        return has(key.asString());
    }

    @Override
    public boolean has(IKey key, IDataType<?, ?> type) {
        return has(key.asString(), type);
    }

    @Override
    public boolean remove(IKey key) {
        return remove(key.asString());
    }

    @Override
    public IKey[] getKeys() {
        return getKeyspaces().stream().map(NamespacedKey::fromString).toArray(IKey[]::new);
    }
    
    /*
     * Abstract
     */
    
    public abstract B getRaw(String key);

    public B getRaw(IKey key) {
        return getRaw(key.asString());
    }
    
    public abstract void set(String key, B value);

    public void set(IKey key, B value) {
        set(key.asString(), value);
    }

}
