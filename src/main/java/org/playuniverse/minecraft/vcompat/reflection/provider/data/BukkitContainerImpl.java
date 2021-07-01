package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedKey;
import org.playuniverse.minecraft.vcompat.reflection.data.wrap.SyntaxKey;

import com.syntaxphoenix.syntaxapi.data.IDataContainer;

public final class BukkitContainerImpl extends WrappedContainer implements PersistentDataContainer {

    private final IDataContainer container;

    public BukkitContainerImpl(IDataContainer container) {
        this.container = container;
    }

    @Override
    public IDataContainer getHandle() {
        return container;
    }

    @Override
    public IDataContainer getAsSyntaxContainer() {
        return container;
    }

    /*
    * 
    */

    @Override
    public <T, Z> boolean has(NamespacedKey key, PersistentDataType<T, Z> type) {
        return has(new BukkitKeyImpl(key), WrappedTypeImpl.wrap(type));
    }

    @Override
    public <T, Z> Z get(NamespacedKey key, PersistentDataType<T, Z> type) {
        return get(new BukkitKeyImpl(key), WrappedTypeImpl.wrap(type));
    }

    @Override
    public <T, Z> Z getOrDefault(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        return Optional.ofNullable(get(key, type)).orElse(value);
    }

    @Override
    public <T, Z> void set(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        set(new BukkitKeyImpl(key), value, WrappedTypeImpl.wrap(type));
    }

    @Override
    public void remove(NamespacedKey key) {
        remove(new BukkitKeyImpl(key));
    }

    @Override
    public Set<NamespacedKey> getKeys() {
        return Arrays.stream(container.getKeys()).map(SyntaxKey::new).map(BukkitKeyImpl::asBukkit).collect(Collectors.toSet());
    }

    @Override
    public PersistentDataAdapterContext getAdapterContext() {
        return getContext();
    }

    /*
    * 
    */

    @Override
    public BukkitContextImpl getContext() {
        return new BukkitContextImpl(container.getAdapterContext());
    }

    @Override
    public boolean has(String key) {
        return has(wrappedKey(key));
    }

    @Override
    public boolean has(WrappedKey<?> key) {
        return container.has(key.getNamespacedKey());
    }

    @Override
    public <P, C> boolean has(String key, WrapType<P, C> type) {
        return has(wrappedKey(key), type);
    }

    @Override
    public <P, C> boolean has(WrappedKey<?> key, WrapType<P, C> type) {
        return container.has(key.getNamespacedKey(), type.syntaxType());
    }

    @Override
    public Object get(String key) {
        return get(wrappedKey(key));
    }

    @Override
    public Object get(WrappedKey<?> key) {
        return container.get(key.getNamespacedKey());
    }

    @Override
    public <P, C> C get(String key, WrapType<P, C> type) {
        return get(wrappedKey(key), type);
    }

    @Override
    public <P, C> C get(WrappedKey<?> key, WrapType<P, C> type) {
        return container.get(key.getNamespacedKey(), type.syntaxType());
    }

    @Override
    public <B> void set(String key, B value, WrapType<?, B> type) {
        set(wrappedKey(key), value, type);
    }

    @Override
    public <B> void set(WrappedKey<?> key, B value, WrapType<?, B> type) {
        container.set(key.getNamespacedKey(), value, type.syntaxType());
    }

    @Override
    public boolean remove(String key) {
        return false;
    }

    @Override
    public boolean remove(WrappedKey<?> key) {
        return container.remove(key.getNamespacedKey());
    }

    @Override
    public Set<String> keySet() {
        return container.getKeyspaces();
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @Override
    public int size() {
        return container.size();
    }

}