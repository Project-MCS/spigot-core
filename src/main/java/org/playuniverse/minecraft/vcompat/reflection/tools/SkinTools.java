package org.playuniverse.minecraft.vcompat.reflection.tools;

import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.utils.minecraft.Skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public abstract class SkinTools {

    public abstract Skin skinFromPlayer(Player player);

    public Skin skinFromGameProfile(GameProfile profile) {
        PropertyMap properties = profile.getProperties();
        if (!properties.containsKey("textures")) {
            return null;
        }

        Property property = properties.get("textures").iterator().next();
        return new Skin(profile.getName(), property.getValue(), property.getSignature());
    }

}