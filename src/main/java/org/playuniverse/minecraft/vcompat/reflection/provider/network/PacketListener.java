package org.playuniverse.minecraft.vcompat.reflection.provider.network;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import org.playuniverse.minecraft.vcompat.reflection.provider.entity.PlayerImpl;

import net.minecraft.network.protocol.Packet;

public abstract class PacketListener {

    protected final ArrayList<Class<? extends Packet<?>>> packets = new ArrayList<>();

    protected abstract boolean onPacket(ExecutorService service, PlayerImpl player, Packet<?> packet);

}
