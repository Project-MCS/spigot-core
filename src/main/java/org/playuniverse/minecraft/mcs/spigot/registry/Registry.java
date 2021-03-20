package org.playuniverse.minecraft.mcs.spigot.registry;

import java.util.concurrent.ConcurrentHashMap;

public class Registry<K, V> implements IRegistry<K, V> {

    protected final ConcurrentHashMap<K, V> registry = new ConcurrentHashMap<>();

    @Override
    public V get(K key) {
        return registry.get(key);
    }

    @Override
    public V getOrElse(K key, V value) {
        return registry.getOrDefault(key, value);
    }

    @Override
    public boolean register(K key, V value) {
        if (key == null || registry.containsKey(key)) {
            return false;
        }
        registry.put(key, value);
        return true;
    }

    @Override
    public boolean unregister(K key) {
        return registry.remove(key) != null;
    }

    @Override
    public boolean isRegistered(K key) {
        return registry.contains(key);
    }

}
