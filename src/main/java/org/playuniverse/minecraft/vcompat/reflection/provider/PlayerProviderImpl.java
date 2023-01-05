package org.playuniverse.minecraft.vcompat.reflection.provider;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.reflection.PlayerProvider;
import org.playuniverse.minecraft.vcompat.reflection.data.persistence.DataDistributor;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsNpc;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsPlayer;
import org.playuniverse.minecraft.vcompat.reflection.provider.entity.PlayerImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.entity.npc.NPCImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.network.PacketDistributor;

import com.mojang.authlib.GameProfile;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.random.Keys;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class PlayerProviderImpl extends PlayerProvider<VersionControlImpl> {

    private final DataDistributor<UUID> npcData;
    private final DataProviderImpl dataProvider;

    private final PacketDistributor packetDistributor = new PacketDistributor();
    private final Container<EventManager> eventManager;

    protected PlayerProviderImpl(VersionControlImpl versionControl) {
        super(versionControl);
        this.dataProvider = versionControl.getDataProvider();
        this.eventManager = versionControl.getEventManager();
        this.npcData = dataProvider.createDistributor(new File(Bukkit.getServer().getWorldContainer(),
            versionControl.getServerProperties().getProperty("level-name", "world") + "/npcData"));
    }

    @Override
    protected NmsNpc createNpc(UUID uniqueId) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile profile = new GameProfile(uniqueId, Keys.generateKey(16));
        ServerLevel level = server.overworld();
        ServerPlayer player = new ServerPlayer(server, level, profile, null);
        return new NPCImpl(eventManager, packetDistributor, dataProvider.wrap(npcData.get(uniqueId)), player);
    }

    @Override
    protected NmsPlayer createPlayer(Player player) {
        return new PlayerImpl(packetDistributor, player);
    }

}