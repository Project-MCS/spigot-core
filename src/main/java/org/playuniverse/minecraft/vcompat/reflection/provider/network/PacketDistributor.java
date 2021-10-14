package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import com.syntaxphoenix.syntaxapi.thread.SynThreadFactory;

public final class PacketDistributor {

    private static final UUID GLOBAL = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final Function<UUID, ArrayList<PacketListener>> FUNCTION = (ignore) -> new ArrayList<>();

    private final HashMap<UUID, ArrayList<PacketListener>> listeners = new HashMap<>();
    private final ExecutorService packetServer = Executors.newCachedThreadPool(new SynThreadFactory("Packet"));

    public ExecutorService getPacketServer() {
        return packetServer;
    }
    
    public void register(PacketListener listener) {
        register(GLOBAL, listener);
    }

    public void register(UUID uniqueId, PacketListener listener) {
        ArrayList<PacketListener> listeners = this.listeners.computeIfAbsent(uniqueId, FUNCTION);
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        unregister(GLOBAL, listener);
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
        if (this.listeners.isEmpty()) {
            return listeners;
        }
        if (this.listeners.containsKey(GLOBAL)) {
            listeners.addAll(this.listeners.get(GLOBAL));
        }
        if (this.listeners.containsKey(target)) {
            listeners.addAll(this.listeners.get(target));
        }
        return listeners;
    }

    public PacketListener[] getListenersFor(UUID target, Class<?> packetType) {
        return getListeners(target).stream().filter(listener -> listener.packets.contains(packetType)).toArray(PacketListener[]::new);
    }

}
