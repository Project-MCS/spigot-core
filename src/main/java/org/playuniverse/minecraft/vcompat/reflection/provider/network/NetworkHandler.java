package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import org.playuniverse.minecraft.vcompat.reflection.provider.entity.PlayerImpl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.protocol.Packet;

public final class NetworkHandler {

    private final PacketClientHandler client = new PacketClientHandler(this);
    private final PacketServerHandler server = new PacketServerHandler(this);

    private final PacketDistributor distributor;

    private final PlayerImpl player;

    public NetworkHandler(PacketDistributor distributor, PlayerImpl player) {
        this.distributor = distributor;
        this.player = player;
        add();
    }

    public void remove(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get("vclient") != null) {
            pipeline.remove("vclient");
        }
        if (pipeline.get("vserver") != null) {
            pipeline.remove("vserver");
        }
    }

    public void add() {
        Channel channel = player.getChannel();
        remove(channel);
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get("vclient") == null) {
            pipeline.addAfter("decoder", "vclient", client);
        }
        if (pipeline.get("vserver") == null) {
            pipeline.addBefore("encoder", "vserver", client);
        }
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
            if (listener.onPacket(distributor.getPacketServer(), player, packet)) {
                return true;
            }
        }
        return false;
    }

}
