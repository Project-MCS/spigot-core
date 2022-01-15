package org.playuniverse.minecraft.mcs.spigot.command.argument;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.syntaxphoenix.avinity.command.StringReader;
import com.syntaxphoenix.avinity.command.type.IArgumentType;

public final class PlayerArgument implements IArgumentType<Player> {
    
    public static final IArgumentType<Player> PLAYER = new PlayerArgument();
    
    private PlayerArgument() {}

    @Override
    public Player parse(StringReader reader) throws IllegalArgumentException {
        return Bukkit.getPlayer(reader.read());
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        String name = "";
        try {
            name = remaining.read();
        } catch (IllegalArgumentException ignore) {
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!player.getName().startsWith(name)) {
                continue;
            }
            suggestions.add(player.getName());
        }
    }

    @Override
    public String print(Player value) {
        return value.getName();
    }
}
