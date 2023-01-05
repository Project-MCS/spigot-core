package org.playuniverse.minecraft.vcompat.reflection.provider.tools;

import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.reflection.tools.SkinTools;
import org.playuniverse.minecraft.vcompat.utils.minecraft.Skin;

public class SkinToolsImpl extends SkinTools {

    @Override
    public Skin skinFromPlayer(Player player) {
        return skinFromGameProfile(((CraftPlayer) player).getHandle().getGameProfile());
    }

}