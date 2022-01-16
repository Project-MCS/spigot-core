package org.playuniverse.minecraft.mcs.spigot.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.playuniverse.minecraft.mcs.spigot.language.MessageWrapper;
import org.playuniverse.minecraft.mcs.spigot.module.ModuleIndicator;
import org.playuniverse.minecraft.mcs.spigot.registry.IUnique;

import com.syntaxphoenix.avinity.command.CommandContext;
import com.syntaxphoenix.avinity.command.IPermission;
import com.syntaxphoenix.avinity.command.connection.AbstractConnection;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

public class BukkitCommand implements TabExecutor, IUnique {

    protected final String name;
    protected final AbstractConnection<BukkitSource> connection;

    protected final String prefix;
    protected final String[] aliases;
    
    protected final ModuleIndicator owner;

    public BukkitCommand(final ModuleIndicator owner, final AbstractConnection<BukkitSource> connection, final String name, final String prefix, final String... aliases) {
        if (Objects.requireNonNull(name).isBlank()) {
            throw new IllegalArgumentException("Name can't be blank!");
        }
        if (Objects.requireNonNull(prefix).isBlank()) {
            throw new IllegalArgumentException("Prefix can't be blank!");
        }
        this.owner = Objects.requireNonNull(owner);
        this.connection = Objects.requireNonNull(connection);
        this.name = name.toLowerCase();
        this.prefix = prefix.toLowerCase();
        HashSet<String> set = new HashSet<>();
        for(String alias : aliases) {
            if(alias.isBlank() || alias.equalsIgnoreCase(name)) {
                continue;
            }
            set.add(alias.toLowerCase());
        }
        this.aliases = set.toArray(String[]::new);
    }

    @Override
    public String getId() {
        return getName();
    }

    public String getName() {
        return name;
    }
    
    public String[] getAliases() {
        return aliases;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public AbstractConnection<BukkitSource> getConnection() {
        return connection;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        CommandContext<BukkitSource> context = connection.parse(new BukkitSource(sender, owner), args);
        if (context.isPermitted()) {
            connection.suggest(list, context);
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandContext<BukkitSource> context = connection.parse(new BukkitSource(sender, owner), args);
        MessageWrapper<?> wrapper = context.getSource().getWrapper();
        if (context.hasException() && !context.hasCommand()) {
            wrapper.send("$prefix " + context.getException().getMessage());
            return false;
        }
        if (!context.hasCommand()) {
            wrapper.send("$prefix Command not found");
            return false;
        }
        IPermission permission;
        if ((permission = context.hasPermission()) != null) {
            wrapper.send("$prefix You are lacking the permission '" + permission.id() + "'!");
            return false;
        }
        try {
            context.getCommand().execute(context);
        } catch (Exception exp) {
            wrapper.send("$prefix Failed!");
            wrapper.send("&7" + Exceptions.getError(exp));
        }
        return false;
    }

}
