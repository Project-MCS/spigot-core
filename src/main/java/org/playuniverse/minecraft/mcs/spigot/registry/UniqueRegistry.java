package org.playuniverse.minecraft.mcs.spigot.registry;

import java.util.concurrent.ConcurrentHashMap;

public class UniqueRegistry<V extends IUnique> implements IRegistry<String, V> {

    private final ConcurrentHashMap<String, V> map = new ConcurrentHashMap<>();

    @Override
    public V get(String key) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.values().stream().filter(unique -> unique.getName().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    @Override
    public V getOrElse(String key, V fallback) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.values().stream().filter(unique -> unique.getName().equalsIgnoreCase(key)).findFirst().orElse(fallback);
    }

    public boolean register(V value) {
        return register(value.getId(), value);
    }

    @Override
    public boolean register(String key, V value) {
        if (!key.equals(value.getId()) || map.containsKey(key)) {
            return false;
        }
        map.put(key, value);
        return true;
    }

    @Override
    public boolean unregister(String key) {
        V value = get(key);
        if (value == null) {
            return false;
        }
        map.remove(value.getId(), value);
        return true;
    }

    @Override
    public boolean isRegistered(String key) {
        return map.containsKey(key) ? true : map.values().stream().anyMatch(value -> value.getName().equalsIgnoreCase(key));
    }

}
