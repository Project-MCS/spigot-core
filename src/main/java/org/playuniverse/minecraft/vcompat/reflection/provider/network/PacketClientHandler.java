package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import io.netty.channel.ChannelHandler.Sharable;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.protocol.Packet;

@Sharable
public final class PacketClientHandler extends ChannelInboundHandlerAdapter {

    private final NetworkHandler handler;

    public PacketClientHandler(NetworkHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Packet)) {
            ctx.fireChannelRead(msg);
            return;
        }
        Packet<?> packet = (Packet<?>) msg;
        if (handler.receive(packet)) {
            return;
        }
        ctx.fireChannelRead(packet);
    }

}