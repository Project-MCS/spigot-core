package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.persistence.PersistentDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterRegistry;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;
import org.playuniverse.minecraft.vcompat.reflection.VersionControl;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedKey;
import org.playuniverse.minecraft.vcompat.reflection.data.wrap.SyntaxKey;

import com.syntaxphoenix.syntaxapi.utils.key.IKey;

public final class SyntaxContainerImpl extends WrappedContainer implements IDataContainer {

    private final PersistentDataContainer container;

    public SyntaxContainerImpl(PersistentDataContainer container) {
        this.container = container;
    }

    @Override
    public PersistentDataContainer getHandle() {
        return container;
    }

    @Override
    public IDataContainer getAsSyntaxContainer() {
        return new SyntaxContainerImpl(container);
    }

    /*
    * 
    */
    
    @Override
    public IDataAdapterRegistry<?> getRegistry() {
        return VersionControl.get().getDataProvider().getRegistry();
    }

    @Override
    public boolean has(IKey key) {
        if(container instanceof WrappedContainer) {
            return ((WrappedContainer) container).has(new SyntaxKey(key));
        }
        throw new UnsupportedOperationException("Can't be used with PersistentDataContainer of Bukkit");
    }

    @Override
    public boolean has(String key, IDataType<?, ?> type) {
        return has(syntaxKey(key), type);
    }

    @Override
    public boolean has(IKey key, IDataType<?, ?> type) {
        return has(new SyntaxKey(key), WrappedTypeImpl.wrap(type));
    }

    @Override
    public <C> C get(String key, IDataType<?, C> type) {
        return get(syntaxKey(key), type);
    }

    @Override
    public <C> C get(IKey key, IDataType<?, C> type) {
        return get(new SyntaxKey(key), WrappedTypeImpl.wrap(type));
    }

    @Override
    public Object get(String key) {
        if(container instanceof WrappedContainer) {
            return ((WrappedContainer) container).get(key);
        }
        throw new UnsupportedOperationException("Can't be used with PersistentDataContainer of Bukkit");
    }

    @Override
    public Object get(IKey key) {
        if(container instanceof WrappedContainer) {
            return ((WrappedContainer) container).get(new SyntaxKey(key));
        }
        throw new UnsupportedOperationException("Can't be used with PersistentDataContainer of Bukkit");
    }

    @Override
    public <V, E> void set(String key, E value, IDataType<V, E> type) {
        set(wrappedKey(key), value, WrappedTypeImpl.wrap(type));
    }

    @Override
    public <V, E> void set(IKey key, E value, IDataType<V, E> type) {
        set(new SyntaxKey(key), value, WrappedTypeImpl.wrap(type));
    }

    @Override
    public boolean remove(String key) {
        return remove(wrappedKey(key));
    }

    @Override
    public boolean remove(IKey key) {
        return remove(new SyntaxKey(key));
    }

    @Override
    public IKey[] getKeys() {
        return container.getKeys().stream().map(BukkitKeyImpl::new).map(WrappedKey::getNamespacedKey).toArray(IKey[]::new);
    }

    @Override
    public Set<String> getKeyspaces() {
        return container.getKeys().stream().map(org.bukkit.NamespacedKey::toString).collect(Collectors.toSet());
    }

    @Override
    public IDataAdapterContext getContext() {
        return getWrapContext();
    }

    /*
    * 
    */

    @Override
    public SyntaxContextImpl getWrapContext() {
        return new SyntaxContextImpl(container.getAdapterContext());
    }

    @Override
    public boolean has(String key) {
        if(container instanceof WrappedContainer) {
            return ((WrappedContainer) container).has(key);
        }
        throw new UnsupportedOperationException("Can't be used with PersistentDataContainer of Bukkit");
    }

    @Override
    public boolean has(WrappedKey<?> key) {
        if(container instanceof WrappedContainer) {
            return ((WrappedContainer) container).has(key);
        }
        throw new UnsupportedOperationException("Can't be used with PersistentDataContainer of Bukkit");
    }

    @Override
    public <P, C> boolean has(String key, WrapType<P, C> type) {
        return has(wrappedKey(key), type);
    }

    @Override
    public <P, C> boolean has(WrappedKey<?> key, WrapType<P, C> type) {
        return container.has(BukkitKeyImpl.asBukkit(key), new SimpleBukkitTypeImpl<>(type));
    }

    @Override
    public Object get(WrappedKey<?> key) {
        if(container instanceof WrappedContainer) {
            return ((WrappedContainer) container).get(key);
        }
        throw new UnsupportedOperationException("Can't be used with PersistentDataContainer of Bukkit");
    }

    @Override
    public <P, C> C get(String key, WrapType<P, C> type) {
        return get(wrappedKey(key), type);
    }

    @Override
    public <P, C> C get(WrappedKey<?> key, WrapType<P, C> type) {
        return container.get(BukkitKeyImpl.asBukkit(key), new SimpleBukkitTypeImpl<>(type));
    }

    @Override
    public <B> void set(String key, B value, WrapType<?, B> type) {
        set(wrappedKey(key), value, type);
    }

    @Override
    public <B> void set(WrappedKey<?> key, B value, WrapType<?, B> type) {
        container.set(BukkitKeyImpl.asBukkit(key), new SimpleBukkitTypeImpl<>(type), value);
    }

    @Override
    public boolean remove(WrappedKey<?> key) {
        container.remove(BukkitKeyImpl.asBukkit(key));
        return true; // Will always return true as we don't know if it contained it
    }

    @Override
    public Set<String> keySet() {
        return getKeyspaces();
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @Override
    public int size() {
        return container.getKeys().size();
    }

}