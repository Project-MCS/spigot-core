package org.playuniverse.minecraft.vcompat.reflection.provider.entity;

import org.playuniverse.minecraft.vcompat.reflection.entity.NmsArmorStand;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

public class ArmorStandImpl extends EntityLivingImpl<ArmorStand> implements NmsArmorStand {

    public ArmorStandImpl(Level world) {
        super(new ArmorStand(EntityType.ARMOR_STAND, world));
    }

    @Override
    public void setSmall(boolean small) {
        handle.setSmall(small);
    }

    @Override
    public boolean isSmall() {
        return handle.isSmall();
    }

}