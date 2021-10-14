package org.playuniverse.minecraft.vcompat.reflection.provider.entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.type.SkinDataType;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsNpc;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;
import org.playuniverse.minecraft.vcompat.utils.bukkit.Players;
import org.playuniverse.minecraft.vcompat.utils.minecraft.Skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NPCImpl extends EntityLivingImpl<ServerPlayer> implements NmsNpc {

    private final WrappedContainer container;

    public NPCImpl(WrappedContainer container, ServerPlayer handle) {
        super(handle);
        this.container = container;
        SynchedEntityData data = handle.getEntityData();
        data.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 0x7F);
    }

    @SuppressWarnings("unchecked")
    public NPCImpl loadPosition() {
        MinecraftServer server = handle.getServer();
        ResourceKey<?>[] keys = server.levelKeys().toArray(ResourceKey[]::new);
        String level = getLevel();
        ServerLevel world = server.overworld();
        for (ResourceKey<?> key : keys) {
            if (!key.location().toString().equals(level)) {
                continue;
            }
            world = server.getLevel((ResourceKey<Level>) key);
            break;
        }
        setLocation(new Location(world.getWorld(), getX(), getY(), getZ()));
        setRotation(getYaw(), getPitch());
        return this;
    }

    public double getX() {
        return container.getOrDefault("x", WrapType.DOUBLE, 0d);
    }

    public double getY() {
        return container.getOrDefault("y", WrapType.DOUBLE, 0d);
    }

    public double getZ() {
        return container.getOrDefault("z", WrapType.DOUBLE, 0d);
    }

    private void setXYZ(double x, double y, double z) {
        container.set("x", x, WrapType.DOUBLE);
        container.set("y", y, WrapType.DOUBLE);
        container.set("z", z, WrapType.DOUBLE);
    }

    public String getLevel() {
        return container.get("level", WrapType.STRING);
    }

    private void setLevel(String level) {
        container.set("level", level, WrapType.STRING);
    }

    public float getYaw() {
        return container.getOrDefault("yaw", WrapType.FLOAT, 0f);
    }

    public float getPitch() {
        return container.getOrDefault("pitch", WrapType.FLOAT, 0f);
    }

    private void setYawAndPitch(float yaw, float pitch) {
        container.set("yaw", yaw, WrapType.FLOAT);
        container.set("pitch", pitch, WrapType.FLOAT);
    }

    @Override
    public WrappedContainer getDataAdapter() {
        return container;
    }

    @Override
    public boolean isNpc() {
        return true;
    }

    @Override
    public void setSkin(Skin skin) {
        if (skin == null) {
            container.remove("skin");
            return;
        }
        container.set("skin", skin, SkinDataType.WRAPPED_INSTANCE);
    }

    @Override
    public Skin getSkin() {
        return container.get("skin", SkinDataType.WRAPPED_INSTANCE);
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            container.remove("name");
            return;
        }
        container.set("name", name, WrapType.STRING);
    }

    @Override
    public String getName() {
        return container.get("name", WrapType.STRING);
    }

    @Override
    public void setLocation(Location location) {
        handle.setPosRaw(location.getX(), location.getY(), location.getZ());
        handle.setOldPosAndRot();
        Vec3 position = handle.position();
        handle.setPos(position.x, position.y, position.z);
        if (location.getWorld() == null || handle.getCommandSenderWorld().getWorld() == location.getWorld()) {
            updatePosition();
            return;
        }
        handle.level = ((CraftWorld) location.getWorld()).getHandle();
        updatePosition();
    }

    @Override
    public NPCImpl setRotation(float yaw, float pitch) {
        yaw = Location.normalizeYaw(yaw);
        pitch = Location.normalizePitch(pitch);
        handle.setYRot(yaw);
        handle.setXRot(pitch);
        handle.yRotO = yaw;
        handle.xRotO = pitch;
        handle.setYHeadRot(yaw);
        return this;
    }

    @Override
    public NPCImpl updateRotation() {
        setYawAndPitch(handle.yRotO, handle.xRotO);
        ClientboundRotateHeadPacket rotationPacket = new ClientboundRotateHeadPacket(handle,
            (byte) Mth.floor(handle.getYHeadRot() * 256F / 360F));
        ClientboundMoveEntityPacket.Rot moreRotationPacket = new ClientboundMoveEntityPacket.Rot(handle.getId(),
            (byte) (handle.getYHeadRot() * 256 / 360), (byte) (handle.getXRot() * 256 / 360), true);
        sendPackets(rotationPacket, moreRotationPacket);
        return this;
    }

    @Override
    public NPCImpl updatePosition() {
        Vec3 pos = handle.position();
        double difX = pos.x - getX();
        double difY = pos.y - getY();
        double difZ = pos.z - getZ();
        setXYZ(pos.x, pos.y, pos.z);
        String level = handle.level.dimension().location().toString();
        if (Math.abs(difX) <= 8 && Math.abs(difY) <= 8 && Math.abs(difZ) <= 8 && level.equals(getLevel())) {
            ClientboundMoveEntityPacket.Pos positionPacket = new ClientboundMoveEntityPacket.Pos(handle.getId(), (short) (difX * 4096),
                (short) (difY * 4096), (short) (difZ * 4096), true);
            sendPackets(positionPacket);
            return this;
        }
        setLevel(level);
        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(handle);
        sendPackets(teleportPacket);
        return this;
    }

    @Override
    public void show(Player... players) {
        if (players.length == 0) {
            return;
        }
        ClientboundPlayerInfoPacket addInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, handle);
        ClientboundAddPlayerPacket spawnPacket = new ClientboundAddPlayerPacket(handle);
        ClientboundRotateHeadPacket rotationPacket = new ClientboundRotateHeadPacket(handle,
            (byte) Mth.floor(handle.getYHeadRot() * 256F / 360F));
        ClientboundMoveEntityPacket.Rot moreRotationPacket = new ClientboundMoveEntityPacket.Rot(handle.getId(),
            (byte) (handle.getYHeadRot() * 256 / 360), (byte) (handle.getXRot() * 256 / 360), true);
        ArrayList<Pair<EquipmentSlot, ItemStack>> list = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            list.add(Pair.of(slot, handle.getItemBySlot(slot)));
        }
        ClientboundSetEquipmentPacket equipmentPacket = new ClientboundSetEquipmentPacket(handle.getId(), list);
        for (Player player : players) {
            if (isShown(player)) {
                continue;
            }
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            connection.send(addInfoPacket);
            connection.send(spawnPacket);
            connection.send(rotationPacket);
            connection.send(moreRotationPacket);
            connection.send(equipmentPacket);
            synchronized (visible) {
                visible.add(player.getUniqueId());
            }
        }
    }

    @Override
    public void hide(Player... players) {
        if (players.length == 0) {
            return;
        }
        ClientboundPlayerInfoPacket remInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,
            handle);
        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(handle.getId());
        for (Player player : players) {
            if (!isShown(player)) {
                continue;
            }
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            connection.send(remInfoPacket);
            connection.send(destroyPacket);
            synchronized (visible) {
                visible.remove(player.getUniqueId());
            }
        }
    }

    @Override
    public void respawn() {
        ClientboundPlayerInfoPacket remInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,
            handle);
        ClientboundPlayerInfoPacket addInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, handle);

        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(handle.getId());
        ClientboundAddPlayerPacket spawnPacket = new ClientboundAddPlayerPacket(handle);
        ClientboundRotateHeadPacket rotationPacket = new ClientboundRotateHeadPacket(handle,
            (byte) Mth.floor(handle.getYHeadRot() * 256F / 360F));
        ClientboundMoveEntityPacket.Rot moreRotationPacket = new ClientboundMoveEntityPacket.Rot(handle.getId(),
            (byte) (handle.getYHeadRot() * 256 / 360), (byte) (handle.getXRot() * 256 / 360), true);

        ArrayList<Pair<EquipmentSlot, ItemStack>> list = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            list.add(Pair.of(slot, handle.getItemBySlot(slot)));
        }
        ClientboundSetEquipmentPacket equipmentPacket = new ClientboundSetEquipmentPacket(handle.getId(), list);
        sendPackets(remInfoPacket, addInfoPacket, destroyPacket, spawnPacket, rotationPacket, moreRotationPacket, equipmentPacket);
    }

    private void sendPackets(Packet<?>... packets) {
        Player[] players = Players.getOnline();
        for (Player player : players) {
            if (!isShown(player)) {
                continue;
            }
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            for (Packet<?> packet : packets) {
                connection.send(packet);
            }
        }
    }

    @Override
    public void update() {
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
        
        if(name != null || skin != null) {
            respawn();
            updatePosition();
            updateRotation();
        }
    }

    /*
     * Duplicated methods
     */

    @Override
    public void fakeRespawn() {
        respawn();
    }

    @Override
    public Skin getRealSkin() {
        return getSkin();
    }

    @Override
    public String getRealName() {
        return getName();
    }

    /*
     * Unused methods
     */

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void setPlayerListHeader(String text) {}

    @Override
    public String getPlayerListHeader() {
        return "";
    }

    @Override
    public void setPlayerListFooter(String text) {}

    @Override
    public String getPlayerListFooter() {
        return "";
    }

    @Override
    public Player getBukkitPlayer() {
        return null;
    }

    @Override
    public void setTitleTimes(int fadeIn, int stay, int fadeOut) {}

    @Override
    public void setPlayerListHeaderAndFooter(String header, String footer) {}

    @Override
    public void sendSubtitle(String text) {}

    @Override
    public void sendTitle(String text) {}

    @Override
    public void sendActionBar(String text) {}

}
