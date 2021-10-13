package org.playuniverse.minecraft.vcompat.reflection;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.reflection.data.persistence.PersistentContainer;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsNpc;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsPlayer;

public abstract class PlayerProvider<V extends VersionControl> extends VersionHandler<V> {

    protected final ConcurrentHashMap<UUID, NmsPlayer> players = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<UUID, NmsNpc> npcs = new ConcurrentHashMap<>();

    protected PlayerProvider(V versionControl) {
        super(versionControl);
    }

    public NmsPlayer getPlayer(UUID uniqueId) {
        Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) {
            return players.get(uniqueId);
        }
        return getPlayer(player);
    }

    public NmsPlayer getPlayer(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            NmsPlayer nmsPlayer = players.get(player.getUniqueId());
            if (nmsPlayer.getBukkitPlayer() != player) {
                Object container = nmsPlayer.getDataAdapter().getHandle();
                if (container instanceof PersistentContainer) {
                    ((PersistentContainer<?>) container).delete();
                }
                players.put(player.getUniqueId(), nmsPlayer = createPlayer(player));
            }
            return nmsPlayer;
        }
        NmsPlayer nmsPlayer = createPlayer(player);
        players.put(player.getUniqueId(), nmsPlayer);
        return nmsPlayer;
    }
    
    public NmsNpc newNpc() {
        return getNpc(UUID.randomUUID());
    }
    
    public NmsNpc getNpc(UUID uniqueId) {
        if(npcs.containsKey(uniqueId)) {
            return npcs.get(uniqueId);
        }
        NmsNpc npc = createNpc(uniqueId);
        if(npc == null) {
            return null;
        }
        npcs.put(uniqueId, npc);
        return npc;
    }

    protected NmsNpc createNpc(UUID uniqueId) {
        return null;
    }

    protected abstract NmsPlayer createPlayer(Player player);

}