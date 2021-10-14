package org.playuniverse.minecraft.vcompat.reflection.provider.utils;

import java.lang.reflect.Field;

import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookups;
import org.playuniverse.minecraft.vcompat.reflection.reflect.handle.ClassLookup;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;

public final class ReflectionSetup extends ClassLookups {

    public static ReflectionSetup INSTANCE = new ReflectionSetup();

    private ReflectionSetup() {}

    @Override
    public void setup(ClassLookupProvider provider) {

        //
        //
        // Needed classes to create Reflects
        //

        //
        //
        // Create Reflects
        //

        //
        // Packets

        ClassLookup lookup = provider.createLookup("interactionPacket", ServerboundInteractPacket.class);
        Field[] fields = lookup.getOwner().getDeclaredFields();
        for (Field field : fields) {
            if (int.class.equals(field.getType())) {
                lookup.putField("entityId", field, true);
                break;
            }
        }

    }

}
