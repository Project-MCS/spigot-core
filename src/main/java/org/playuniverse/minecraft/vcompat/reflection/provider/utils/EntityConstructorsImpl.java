package org.playuniverse.minecraft.vcompat.reflection.provider.utils;

import java.util.function.Function;

import org.playuniverse.minecraft.vcompat.reflection.provider.entity.ArmorStandImpl;

import net.minecraft.world.level.Level;

public abstract class EntityConstructorsImpl {

    public static final Function<Level, ArmorStandImpl> ARMOR_STAND = (world -> new ArmorStandImpl(world));

}