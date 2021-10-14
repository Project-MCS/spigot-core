package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import org.playuniverse.minecraft.vcompat.reflection.provider.entity.PlayerImpl;

import io.netty.channel.Channel;
import net.minecraft.network.protocol.Packet;

public final class NetworkHandler {

    private final PacketClientHandler client = new PacketClientHandler(this);
    private final PacketServerHandler server = new PacketServerHandler(this);

    private final PacketDistributor distributor;

    private final PlayerImpl player;

    public NetworkHandler(PacketDistributor distributor, PlayerImpl player) {
        this.distributor = distributor;
        this.player = player;
    }

    public void remove() {
        Channel channel = player.getChannel();
        channel.pipeline().remove(client);
        channel.pipeline().remove(server);
    }

    public void add() {
        Channel channel = player.getChannel();
        channel.pipeline().addAfter("decoder", "vClientHandler", client);
        channel.pipeline().addBefore("encoder", "vServerHandler", server);
    }

    public PacketClientHandler getClient() {
        return client;
    }

    public PacketServerHandler getServer() {
        return server;
    }

    public boolean receive(Packet<?> packet) {
        return onPacket(packet);
    }

    public boolean send(Packet<?> packet) {
        return onPacket(packet);
    }

    private boolean onPacket(Packet<?> packet) {
        PacketListener[] listeners = distributor.getListenersFor(player.getUniqueId(), packet.getClass());
        if (listeners.length == 0) {
            return false;
        }
        for (PacketListener listener : listeners) {
            if (listener.onPacket(player, packet)) {
                return true;
            }
        }
        return false;
    }

}
