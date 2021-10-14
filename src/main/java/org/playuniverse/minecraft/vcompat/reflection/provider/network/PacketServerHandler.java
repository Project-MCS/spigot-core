package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;

public final class PacketServerHandler extends ChannelOutboundHandlerAdapter {

    private final NetworkHandler handler;

    public PacketServerHandler(NetworkHandler handler) {
        this.handler = handler;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Packet)) {
            ctx.write(msg, promise);
            return;
        }
        Packet<?> packet = (Packet<?>) msg;
        if (handler.send(packet)) {
            promise.cancel(true);
            return;
        }
        ctx.write(packet, promise);
    }

}
