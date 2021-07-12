package org.playuniverse.minecraft.vcompat.reflection.provider;

import static org.playuniverse.minecraft.vcompat.utils.constants.MinecraftConstants.TEXTURE_SIGNATURE;

import java.io.IOException;
import java.util.Base64;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftSkull;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.playuniverse.minecraft.vcompat.reflection.TextureProvider;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;
import com.syntaxphoenix.syntaxapi.reflection.AbstractReflect;
import com.syntaxphoenix.syntaxapi.reflection.Reflect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class TextureProviderImpl extends TextureProvider<VersionControlImpl> {

    private final AbstractReflect craftEntityStateRef = new Reflect(CraftBlockEntityState.class).searchField("tileEntity", "tileEntity");
    private final AbstractReflect craftItemStackRef = new Reflect(CraftItemStack.class).searchField("handle", "handle");
    private final AbstractReflect craftMetaSkullRef = new Reflect("org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMetaSkull")
        .searchField("serialized", "serializedProfile").searchField("profile", "profile");

    private final JsonParser parser = new JsonParser();
    private final JsonWriter writer = new JsonWriter().setPretty(false).setSpaces(true);

    protected TextureProviderImpl(VersionControlImpl versionControl) {
        super(versionControl);
    }

    @Override
    public GameProfile profileFromBlock(Block block) {
        if (!(block instanceof CraftSkull)) {
            return null;
        }
        SkullBlockEntity entitySkull = (SkullBlockEntity) craftEntityStateRef.getFieldValue("tileEntity", block);
        return entitySkull.owner;
    }

    @Override
    public GameProfile profileFromItem(org.bukkit.inventory.ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta)) {
            return null;
        }
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        GameProfile profile = (GameProfile) craftMetaSkullRef.getFieldValue("profile", meta);
        if (profile == null) {
            CompoundTag compound = (CompoundTag) craftMetaSkullRef.getFieldValue("serialized", meta);
            if (compound == null) {
                ItemStack stack = null;
                if (itemStack instanceof CraftItemStack) {
                    stack = (ItemStack) craftItemStackRef.getFieldValue("handle", itemStack);
                }
                if (stack == null) {
                    stack = CraftItemStack.asNMSCopy(itemStack);
                }
                CompoundTag stackTag = stack.getOrCreateTag();
                if (stackTag.contains("SkullOwner", 10)) {
                    compound = stackTag.getCompound("SkullOwner");
                } else if (stackTag.contains("SkullProfile", 10)) {
                    compound = stackTag.getCompound("SkullProfile");
                }
            }
            if (compound == null) {
                return null;
            }
            profile = NbtUtils.readGameProfile(compound);
        }
        return profile;
    }

    @Override
    public org.bukkit.inventory.ItemStack getItem(GameProfile profile) {
        org.bukkit.inventory.ItemStack craftStack = CraftItemStack.asCraftCopy(new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD));
        applyItem(craftStack, profile);
        return craftStack;
    }

    @Override
    public boolean applyItem(org.bukkit.inventory.ItemStack itemStack, GameProfile profile) {
        ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof SkullMeta)) {
            return false;
        }
        SkullMeta skullMeta = (SkullMeta) meta;
        craftMetaSkullRef.setFieldValue(meta, "profile", profile);
        itemStack.setItemMeta(skullMeta);
        return true;
    }

    @Override
    public boolean applyBlock(Block block, GameProfile profile) {
        if (!(block instanceof CraftSkull)) {
            return false;
        }
        SkullBlockEntity entitySkull = (SkullBlockEntity) craftEntityStateRef.getFieldValue("tileEntity", block);
        entitySkull.setOwner(profile);
        return true;
    }

    @Override
    public GameProfile profileFromPlayer(OfflinePlayer player) {
        GameProfile profile = player.isOnline() ? ((CraftPlayer) player.getPlayer()).getProfile()
            : ((CraftOfflinePlayer) player).getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String string = new String(Base64.getDecoder().decode(property.getValue()));
        GameProfile outputProfile = new GameProfile(player.getUniqueId(), player.getName());
        PropertyMap map = outputProfile.getProperties();
        try {
            JsonObject object = (JsonObject) parser.fromString(string);
            JsonObject textures = (JsonObject) object.get("textures");
            JsonObject skin = (JsonObject) textures.get("SKIN");
            skin.remove("metadata");
            JsonObject output = new JsonObject();
            output.set("textures", textures);
            map.put("textures",
                new Property(property.getName(), Base64.getEncoder().encodeToString(writer.toBytes(output)), TEXTURE_SIGNATURE));
            return outputProfile;
        } catch (IOException e) {
        }
        return outputProfile;
    }

}