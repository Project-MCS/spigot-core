package org.playuniverse.minecraft.mcs.spigot.registry;

import java.util.Collection;

public interface IRegistry<K, V> {

    V get(K key);

    V getOrElse(K key, V value);

    boolean register(K key, V value);

    boolean unregister(K key);
    
    boolean isRegistered(K key);
    
    Collection<V> values();
    
    boolean isEmpty();
    
    int size();
    
    void dispose();

}
