package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public final class PacketDistributor {

    private static final Function<UUID, ArrayList<PacketListener>> FUNCTION = (ignore) -> new ArrayList<>();

    private final HashMap<UUID, ArrayList<PacketListener>> listeners = new HashMap<>();

    public void register(PacketListener listener) {
        register(null, listener);
    }

    public void register(UUID uniqueId, PacketListener listener) {
        ArrayList<PacketListener> listeners = this.listeners.computeIfAbsent(uniqueId, FUNCTION);
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        unregister(null, listener);
    }

    public void unregister(UUID uniqueId, PacketListener listener) {
        ArrayList<PacketListener> listeners = this.listeners.get(uniqueId);
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            this.listeners.remove(uniqueId);
        }
    }

    public ArrayList<PacketListener> getListeners(UUID target) {
        ArrayList<PacketListener> listeners = new ArrayList<>();
        listeners.addAll(this.listeners.get(null));
        listeners.addAll(this.listeners.get(target));
        return listeners;
    }

    public PacketListener[] getListenersFor(UUID target, Class<?> packetType) {
        return getListeners(target).stream().filter(listener -> listener.packets.contains(packetType)).toArray(PacketListener[]::new);
    }

}
