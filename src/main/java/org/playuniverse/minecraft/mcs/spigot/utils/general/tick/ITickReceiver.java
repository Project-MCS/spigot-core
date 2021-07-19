package org.playuniverse.minecraft.mcs.spigot.utils.general.tick;

@FunctionalInterface
public interface ITickReceiver {
    
    void onTick(long deltaTime);

}
