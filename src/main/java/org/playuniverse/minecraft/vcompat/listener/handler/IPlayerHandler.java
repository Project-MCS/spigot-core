package org.playuniverse.minecraft.vcompat.listener.handler;

import org.playuniverse.minecraft.vcompat.reflection.entity.NmsPlayer;

public interface IPlayerHandler {

    default void onJoin(NmsPlayer player) {}

    default void onLeave(NmsPlayer player) {}
    
}
