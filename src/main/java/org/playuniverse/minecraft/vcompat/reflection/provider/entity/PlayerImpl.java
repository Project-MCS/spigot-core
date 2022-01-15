package org.playuniverse.minecraft.vcompat.reflection.provider.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.type.SkinDataType;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsPlayer;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.SyntaxContainerImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.network.NetworkHandler;
import org.playuniverse.minecraft.vcompat.reflection.provider.network.PacketDistributor;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;
import org.playuniverse.minecraft.vcompat.utils.bukkit.Players;
import org.playuniverse.minecraft.vcompat.utils.minecraft.MojangProfileServer;
import org.playuniverse.minecraft.vcompat.utils.minecraft.Skin;
import org.playuniverse.minecraft.vcompat.utils.thread.PostAsync;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;

import io.netty.channel.Channel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.BiomeManager;

public final class PlayerImpl extends EntityLivingImpl<ServerPlayer> implements NmsPlayer {

    private String realName;
    private Skin realSkin;

    private final WrappedContainer dataAdapter;
    private final NetworkHandler networkHandler;

    private final UUID uniqueId;

    public PlayerImpl(PacketDistributor distributor, Player player) {
        super(((CraftPlayer) player).getHandle());
        this.uniqueId = player.getUniqueId();
        this.networkHandler = new NetworkHandler(distributor, this);
        this.dataAdapter = new SyntaxContainerImpl(getBukkitPlayer().getPersistentDataContainer());
        update(false);
    }

    @Override
    public ServerPlayer getHandle() {
        ServerPlayer player = super.getHandle();
        if (!player.hasDisconnected()) {
            return player;
        }
        ServerPlayer updated = player.getServer().getPlayerList().getPlayer(uniqueId);
        if(updated == player) {
            return player;
        }
        if (updated != null) {
            networkHandler.remove(player.connection.connection.channel);
            updateHandle(updated);
            networkHandler.add();
            update(false);
            return updated;
        }
        return player;
    }

    @Override
    public CraftPlayer getBukkitPlayer() {
        return getHandle().getBukkitEntity();
    }

    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    public Channel getChannel() {
        return getHandle().connection.connection.channel;
    }

    @Override
    public WrappedContainer getDataAdapter() {
        return dataAdapter;
    }

    @Override
    public boolean isNpc() {
        return false;
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
        return getHandle().latency;
    }

    @Override
    public void setPlayerListHeaderAndFooter(String header, String footer) {
        dataAdapter.set("header", header, WrapType.STRING);
        dataAdapter.set("footer", footer, WrapType.STRING);
        sendPlayerListInfo(header, footer);
    }

    private final void sendPlayerListInfo(String header, String footer) {
        if (getHandle().hasDisconnected()) {
            return;
        }

        Component headerComponent = header.isEmpty() ? null : CraftChatMessage.fromStringOrNull(header, true);
        Component footerComponent = footer.isEmpty() ? null : CraftChatMessage.fromStringOrNull(footer, true);

        getHandle().connection.send(new ClientboundTabListPacket(headerComponent, footerComponent));
    }

    @Override
    public void setTitleTimes(int fadeIn, int stay, int fadeOut) {
        if (getHandle().hasDisconnected()) {
            return;
        }
        getHandle().connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
    }

    @Override
    public void sendSubtitle(String text) {
        if (getHandle().hasDisconnected()) {
            return;
        }
        getHandle().connection.send(new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(text)));
    }

    @Override
    public void sendTitle(String text) {
        if (getHandle().hasDisconnected()) {
            return;
        }
        getHandle().connection.send(new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(text)));
    }

    @Override
    public void sendActionBar(String text) {
        if (getHandle().hasDisconnected()) {
            return;
        }
        getHandle().connection.send(new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull(text)));
    }

    @Override
    public void fakeRespawn() {
        if (getHandle().hasDisconnected()) {
            return;
        }
        ClientboundPlayerInfoPacket remInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,
            getHandle());
        ClientboundPlayerInfoPacket addInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER,
            getHandle());

        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(getHandle().getId());
        ClientboundAddPlayerPacket spawnPacket = new ClientboundAddPlayerPacket(getHandle());
        ClientboundRotateHeadPacket rotationPacket = new ClientboundRotateHeadPacket(getHandle(),
            (byte) Mth.floor(getHandle().getYHeadRot() * 256F / 360F));

        ArrayList<Pair<EquipmentSlot, ItemStack>> list = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            list.add(Pair.of(slot, getHandle().getItemBySlot(slot)));
        }
        ClientboundSetEquipmentPacket equipmentPacket = new ClientboundSetEquipmentPacket(getHandle().getId(), list);

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

        ServerLevel world = (ServerLevel) getHandle().level;

        ClientboundRespawnPacket respawnPacket = new ClientboundRespawnPacket(world.dimensionType(), world.dimension(),
            BiomeManager.obfuscateSeed(world.getSeed()), getHandle().gameMode.getGameModeForPlayer(),
            getHandle().gameMode.getPreviousGameModeForPlayer(), world.isDebug(), world.isFlat(), true);
        ClientboundPlayerPositionPacket positionPacket = new ClientboundPlayerPositionPacket(getHandle().getX(), getHandle().getY(),
            getHandle().getZ(), getHandle().xRotO, getHandle().yRotO, Collections.emptySet(), 0, false);
        ClientboundSetCarriedItemPacket itemPacket = new ClientboundSetCarriedItemPacket(getHandle().getInventory().selected);
        ClientboundEntityEventPacket statusPacket = new ClientboundEntityEventPacket(getHandle(), (byte) 28);
        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(getHandle().getId(), getHandle().getEntityData(),
            true);

        ServerGamePacketListenerImpl connection = getHandle().connection;
        connection.send(remInfoPacket);
        connection.send(addInfoPacket);
        connection.send(respawnPacket);
        connection.send(positionPacket);
        connection.send(itemPacket);
        connection.send(statusPacket);
        connection.send(metadataPacket);

        getHandle().onUpdateAbilities();
        getHandle().resetSentInfo();
        getHandle().inventoryMenu.broadcastChanges();
        getHandle().inventoryMenu.sendAllDataToRemote();
        if (getHandle().containerMenu != getHandle().inventoryMenu) {
            getHandle().containerMenu.broadcastChanges();
            getHandle().containerMenu.sendAllDataToRemote();
        }
        self.recalculatePermissions();
    }

    @Override
    public void respawn() {
        if (getHandle().hasDisconnected()) {
            return;
        }
        getHandle().connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
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
        if (getHandle().hasDisconnected()) {
            return;
        }
        if (flag) {
            GameProfile profile = getHandle().getGameProfile();

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