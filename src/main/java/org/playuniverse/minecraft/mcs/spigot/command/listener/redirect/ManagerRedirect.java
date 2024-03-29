package org.playuniverse.minecraft.mcs.spigot.command.listener.redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.playuniverse.minecraft.mcs.spigot.command.CommandManager;
import org.playuniverse.minecraft.mcs.spigot.command.IModule;
import org.playuniverse.minecraft.mcs.spigot.command.listener.AbstractRedirect;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftInfo;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.RootNode;

public class ManagerRedirect extends AbstractRedirect {

    private final CommandManager<MinecraftInfo> manager;

    private Function<String, Boolean> condition;

    public ManagerRedirect(CommandManager<MinecraftInfo> manager, IModule plugin) {
        super(plugin);
        this.manager = manager;
    }

    @Override
    public boolean isValid() {
        return manager != null;
    }

    public void setCondition(Function<String, Boolean> condition) {
        this.condition = condition;
    }

    @Override
    protected RootNode<MinecraftInfo> handleComplete(String command) {
        return handleCommand(command);
    }

    @Override
    protected List<String> handleNullComplete(MinecraftCommand root, CommandSender sender, String[] args) {
        return args.length <= 1 ? collectCommands() : null;
    }

    @Override
    protected RootNode<MinecraftInfo> handleCommand(String command) {
        if (command == null) {
            if (condition != null && !condition.apply(command)) {
                return null;
            }
            return manager.getGlobal();
        }
        RootNode<MinecraftInfo> node = manager.getCommand(command);
        if (node == null && hasGlobal()) {
            if (condition != null && !condition.apply(command)) {
                return null;
            }
            return manager.getGlobal();
        }
        return node;
    }

    @Override
    protected boolean hasGlobal() {
        return manager.hasGlobal();
    }

    @Override
    protected String getGlobal() {
        return manager.hasGlobal() ? manager.getGlobal().getName() : null;
    }

    private List<String> collectCommands() {
        if (manager.getCommands().isEmpty()) {
            return null;
        }
        ArrayList<String> commands = new ArrayList<>();
        manager.getCommands().values().forEach(array -> Collections.addAll(commands, array));
        return commands;
    }

    @Override
    protected int argBuildStart() {
        return 1;
    }

}
