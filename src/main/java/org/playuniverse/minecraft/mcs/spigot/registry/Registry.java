package org.playuniverse.minecraft.mcs.spigot.registry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Registry<K, V> implements IRegistry<K, V> {

    protected final ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V getOrElse(K key, V value) {
        return map.getOrDefault(key, value);
    }

    @Override
    public boolean register(K key, V value) {
        if (key == null || map.containsKey(key)) {
            return false;
        }
        map.put(key, value);
        return true;
    }

    @Override
    public boolean unregister(K key) {
        return map.remove(key) != null;
    }

    @Override
    public boolean isRegistered(K key) {
        return map.contains(key);
    }
    
    @Override
    public Collection<V> values() {
        return map.values();
    }
    
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void dispose() {
        map.clear();
    }

}
