package org.playuniverse.minecraft.vcompat.reflection.reflect.provider;

import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookups;

import com.mojang.authlib.GameProfile;

public class GeneralReflections extends ClassLookups {

    public static GeneralReflections INSTANCE = new GeneralReflections();

    private GeneralReflections() {}

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
        // Mojang

        provider.createLookup("mjGameProfile", GameProfile.class).searchField("name", "name");

    }

}