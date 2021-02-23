package org.playuniverse.minecraft.mcs.spigot.command.nodes;

import org.playuniverse.minecraft.mcs.spigot.command.Command;
import org.playuniverse.minecraft.mcs.spigot.command.CommandContext;
import org.playuniverse.minecraft.mcs.spigot.command.VoidCommand;

public class CommandNode<S> extends RootNode<S> {

    private final Command<S> command;

    public CommandNode(String name, Command<S> command) {
        super(name);
        this.command = command;
    }

    public CommandNode(String name, VoidCommand<S> command) {
        super(name);
        this.command = command;
    }

    public Command<S> getCommand() {
        return command;
    }

    @Override
    public int execute(CommandContext<S> context) {
        return command.execute(context);
    }

}