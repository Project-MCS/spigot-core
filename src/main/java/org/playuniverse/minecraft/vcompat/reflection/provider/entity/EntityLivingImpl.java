package org.playuniverse.minecraft.vcompat.reflection.provider.entity;

import org.playuniverse.minecraft.vcompat.reflection.entity.NmsEntityLiving;

import net.minecraft.world.entity.LivingEntity;

public abstract class EntityLivingImpl<E extends LivingEntity> extends EntityImpl<E> implements NmsEntityLiving {

    public EntityLivingImpl(E handle) {
        super(handle);
    }

    @Override
    public void setCollidable(boolean collidable) {
        getHandle().collides = collidable;
    }

}