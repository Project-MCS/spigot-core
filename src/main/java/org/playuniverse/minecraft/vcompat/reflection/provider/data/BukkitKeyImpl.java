package org.playuniverse.minecraft.vcompat.reflection.provider.data;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedKey;

public final class BukkitKeyImpl extends WrappedKey<NamespacedKey> {

    private final NamespacedKey key;

    public BukkitKeyImpl(Plugin plugin, String key) {
        this.key = new NamespacedKey(plugin, key);
    }

    @SuppressWarnings("deprecation")
    public BukkitKeyImpl(String name, String key) {
        this.key = new NamespacedKey(name, key);
    }

    public BukkitKeyImpl(NamespacedKey key) {
        this.key = key;
    }

    @Override
    public NamespacedKey getHandle() {
        return key;
    }

    @Override
    public String getName() {
        return key.getNamespace();
    }

    @Override
    public String getKey() {
        return key.getKey();
    }

    @Override
    public String toString() {
        return key.toString();
    }

    public static NamespacedKey asBukkit(WrappedKey<?> key) {
        if (key.getHandle() instanceof NamespacedKey) {
            return (NamespacedKey) key.getHandle();
        }
        return new BukkitKeyImpl(key.getName(), key.getKey()).getHandle();
    }

}