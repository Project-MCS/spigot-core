package org.playuniverse.minecraft.vcompat.reflection.provider.entity;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.type.SkinDataType;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsPlayer;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.SyntaxContainerImpl;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;
import org.playuniverse.minecraft.vcompat.utils.bukkit.Players;
import org.playuniverse.minecraft.vcompat.utils.minecraft.MojangProfileServer;
import org.playuniverse.minecraft.vcompat.utils.minecraft.Skin;
import org.playuniverse.minecraft.vcompat.utils.thread.PostAsync;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.BiomeManager;

public class PlayerImpl extends EntityLivingImpl<ServerPlayer> implements NmsPlayer {

    private String realName;
    private Skin realSkin;

    private final WrappedContainer dataAdapter;

    public PlayerImpl(Player player) {
        super(((CraftPlayer) player).getHandle());
        dataAdapter = new SyntaxContainerImpl(getBukkitPlayer().getPersistentDataContainer());
        update(false);
    }

    @Override
    public CraftPlayer getBukkitPlayer() {
        return handle.getBukkitEntity();
    }

    @Override
    public WrappedContainer getDataAdapter() {
        return dataAdapter;
    }

    @Override
    public void setSkin(Skin skin) {
        if (skin == null) {
            return;
        }
        dataAdapter.set("skin", skin, SkinDataType.WRAPPED_INSTANCE);
    }

    @Override
    public Skin getSkin() {
        return dataAdapter.getOrDefault("skin", SkinDataType.WRAPPED_INSTANCE, realSkin);
    }

    @Override
    public Skin getRealSkin() {
        return realSkin;
    }

    @Override
    public void setName(String name) {
        if (getName().equals(name)) {
            return;
        }
        if (name == null) {
            dataAdapter.remove("name");
            return;
        }
        dataAdapter.set("name", name, WrapType.STRING);
    }

    @Override
    public String getName() {
        return dataAdapter.getOrDefault("name", WrapType.STRING, realName);
    }

    @Override
    public String getRealName() {
        return realName;
    }

    @Override
    public void setPlayerListHeader(String text) {
        setPlayerListHeaderAndFooter(text, getPlayerListFooter());
    }

    @Override
    public String getPlayerListHeader() {
        return dataAdapter.getOrDefault("header", WrapType.STRING, "");
    }

    @Override
    public void setPlayerListFooter(String text) {
        setPlayerListHeaderAndFooter(getPlayerListHeader(), text);
    }

    @Override
    public String getPlayerListFooter() {
        return dataAdapter.getOrDefault("footer", WrapType.STRING, "");
    }

    @Override
    public int getPing() {
        return handle.latency;
    }

    @Override
    public void setPlayerListHeaderAndFooter(String header, String footer) {
        dataAdapter.set("header", header, WrapType.STRING);
        dataAdapter.set("footer", footer, WrapType.STRING);
        sendPlayerListInfo(header, footer);
    }

    private final void sendPlayerListInfo(String header, String footer) {
        if (handle.connection.isDisconnected()) {
            return;
        }

        Component headerComponent = header.isEmpty() ? null : CraftChatMessage.fromStringOrNull(header, true);
        Component footerComponent = footer.isEmpty() ? null : CraftChatMessage.fromStringOrNull(footer, true);

        handle.connection.send(new ClientboundTabListPacket(headerComponent, footerComponent));
    }

    @Override
    public void setTitleTimes(int fadeIn, int stay, int fadeOut) {
        if (handle.connection.isDisconnected()) {
            return;
        }
        handle.connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
    }

    @Override
    public void sendSubtitle(String text) {
        if (handle.connection.isDisconnected()) {
            return;
        }
        handle.connection.send(new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(text)));
    }

    @Override
    public void sendTitle(String text) {
        if (handle.connection.isDisconnected()) {
            return;
        }
        handle.connection.send(new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(text)));
    }

    @Override
    public void sendActionBar(String text) {
        if (handle.connection.isDisconnected()) {
            return;
        }
        handle.connection.send(new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull(text)));
    }

    @Override
    public void fakeRespawn() {
        if (handle.connection.isDisconnected()) {
            return;
        }
        ClientboundPlayerInfoPacket remInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,
            handle);
        ClientboundPlayerInfoPacket addInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, handle);

        ClientboundRemoveEntityPacket destroyPacket = new ClientboundRemoveEntityPacket(handle.getId());
        ClientboundAddPlayerPacket spawnPacket = new ClientboundAddPlayerPacket(handle);
        ClientboundRotateHeadPacket rotationPacket = new ClientboundRotateHeadPacket(handle,
            (byte) Mth.floor(handle.getYHeadRot() * 256F / 360F));

        ArrayList<Pair<EquipmentSlot, ItemStack>> list = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            list.add(Pair.of(slot, handle.getItemBySlot(slot)));
        }
        ClientboundSetEquipmentPacket equipmentPacket = new ClientboundSetEquipmentPacket(handle.getId(), list);

        Player self = getBukkitPlayer();
        Player[] players = Players.getOnlineWithout(getUniqueId());
        for (Player player : players) {
            if (!player.canSee(self)) {
                continue;
            }
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            connection.send(remInfoPacket);
            connection.send(addInfoPacket);
            connection.send(destroyPacket);
            connection.send(spawnPacket);
            connection.send(rotationPacket);
            connection.send(equipmentPacket);
        }

        ServerLevel world = (ServerLevel) handle.level;

        ClientboundRespawnPacket respawnPacket = new ClientboundRespawnPacket(world.dimensionType(), world.dimension(),
            BiomeManager.obfuscateSeed(world.getSeed()), handle.gameMode.getGameModeForPlayer(),
            handle.gameMode.getPreviousGameModeForPlayer(), world.isDebug(), world.isFlat(), true);
        ClientboundPlayerPositionPacket positionPacket = new ClientboundPlayerPositionPacket(handle.getX(), handle.getY(), handle.getZ(),
            handle.xRotO, handle.yRotO, Collections.emptySet(), 0, false);
        ServerboundPickItemPacket itemPacket = new ServerboundPickItemPacket(handle.getInventory().selected);
        ClientboundEntityEventPacket statusPacket = new ClientboundEntityEventPacket(handle, (byte) 28);
        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(handle.getId(), handle.getEntityData(), true);

        ServerGamePacketListenerImpl connection = handle.connection;
        connection.send(remInfoPacket);
        connection.send(addInfoPacket);
        connection.send(respawnPacket);
        connection.send(positionPacket);
        connection.send(itemPacket);
        connection.send(statusPacket);
        connection.send(metadataPacket);

        handle.onUpdateAbilities();
        handle.resetSentInfo();
        handle.inventoryMenu.broadcastChanges();
        handle.inventoryMenu.sendAllDataToRemote();
        if (handle.containerMenu != handle.inventoryMenu) {
            handle.containerMenu.broadcastChanges();
            handle.containerMenu.sendAllDataToRemote();
        }
        self.recalculatePermissions();
    }

    @Override
    public void respawn() {
        if (handle.connection.isDisconnected()) {
            return;
        }
        handle.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
    }

    @Override
    public void update() {
        update(true);
    }

    private final void update(boolean flag) {
        PostAsync.forcePost(() -> {
            realName = MojangProfileServer.getName(getUniqueId());
            realSkin = MojangProfileServer.getSkin(realName, getUniqueId());
        });
        if (flag) {
            GameProfile profile = handle.getGameProfile();

            Skin skin = getSkin();
            if (skin != null) {
                PropertyMap properties = profile.getProperties();
                properties.removeAll("textures");
                properties.put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
            }

            String name = getName();
            if (name != null) {
                ClassLookupProvider.DEFAULT.getLookup("mjGameProfile").setFieldValue(profile, "name", name);
            }

            if (!(name == null && skin == null)) {
                fakeRespawn();
            }
        }
    }

}