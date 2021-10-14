package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import java.util.UUID;

import io.netty.channel.Channel;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkHandler {
    
    private final PacketClientHandler client = new PacketClientHandler(this);
    private final PacketServerHandler server = new PacketServerHandler(this);
    
    private final Channel channel;
    
    public NetworkHandler(UUID uniqueId, ServerPlayer player) {
        channel = player.connection.connection.channel;
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
        return false;
    }
    
    public boolean send(Packet<?> packet) {
        return false;
    }

}
