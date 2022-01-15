package org.playuniverse.minecraft.vcompat.reflection.provider.tools;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftSkull;
import org.playuniverse.minecraft.vcompat.reflection.tools.BlockTools;
import org.playuniverse.minecraft.vcompat.utils.constants.MinecraftConstants;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.syntaxphoenix.syntaxapi.reflection.AbstractReflect;
import com.syntaxphoenix.syntaxapi.reflection.Reflect;

import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class BlockToolsImpl extends BlockTools {

    private final AbstractReflect craftEntityStateRef = new Reflect(CraftSkull.class).searchField("tileEntity", "tileEntity");

    @Override
    public void setHeadTexture(Block block, String texture) {
        if (!(block instanceof CraftSkull)) {
            return;
        }
        SkullBlockEntity entitySkull = (SkullBlockEntity) craftEntityStateRef.getFieldValue("tileEntity", block);
        PropertyMap map = entitySkull.owner.getProperties();
        map.removeAll("textures");
        map.put("textures", new Property("textures", MinecraftConstants.TEXTURE_SIGNATURE, texture));
    }

    @Override
    public String getHeadTexture(Block block) {
        if (!(block instanceof CraftSkull)) {
            return null;
        }
        SkullBlockEntity entitySkull = (SkullBlockEntity) craftEntityStateRef.getFieldValue("tileEntity", block);
        return entitySkull.owner.getProperties().get("textures").iterator().next().getValue();
    }

}