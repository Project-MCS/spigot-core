package org.playuniverse.minecraft.vcompat.reflection.provider;

import org.bukkit.entity.Player;
import org.playuniverse.minecraft.vcompat.reflection.PlayerProvider;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsPlayer;
import org.playuniverse.minecraft.vcompat.reflection.provider.entity.PlayerImpl;

public class PlayerProviderImpl extends PlayerProvider<VersionControlImpl> {

    protected PlayerProviderImpl(VersionControlImpl versionControl) {
        super(versionControl);
    }

    @Override
    protected NmsPlayer createPlayer(Player player) {
        return new PlayerImpl(player);
    }

}