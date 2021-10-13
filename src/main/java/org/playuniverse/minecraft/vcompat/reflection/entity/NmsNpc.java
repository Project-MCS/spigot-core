package org.playuniverse.minecraft.vcompat.reflection.entity;

public interface NmsNpc extends NmsPlayer {
    
    NmsNpc loadPosition();
    
    double getX();
    
    double getY();
    
    double getZ();
    
    String getLevel();
    
    float getYaw();
    
    float getPitch();
    
    NmsNpc setRotation(float yaw, float pitch);
    
    NmsNpc updateRotation();
    
    NmsNpc updatePosition();
    
    NmsNpc updateMetadata();
    
}
