package org.playuniverse.minecraft.mcs.spigot.registry;

public interface IRegistry<K, V> {

    V get(K key);

    V getOrElse(K key, V value);

    boolean register(K key, V value);

    boolean unregister(K key);
    
    boolean isRegistered(K key);

}
