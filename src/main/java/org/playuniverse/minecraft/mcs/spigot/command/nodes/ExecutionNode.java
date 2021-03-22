package org.playuniverse.minecraft.mcs.spigot.command.nodes;

import java.util.List;

import org.playuniverse.minecraft.mcs.spigot.command.Command;
import org.playuniverse.minecraft.mcs.spigot.command.CommandContext;
import org.playuniverse.minecraft.mcs.spigot.command.Complete;
import org.playuniverse.minecraft.mcs.spigot.command.VoidCommand;

public class ExecutionNode<S> extends SubNode<S> {

    private final Command<S> command;
    private final Complete<S> complete;

    public ExecutionNode(String name, Command<S> command) {
        super(name);
        this.command = command;
        this.complete = Complete.nothing();
    }

    public ExecutionNode(String name, VoidCommand<S> command) {
        super(name);
        this.command = command;
        this.complete = Complete.nothing();
    }

    public ExecutionNode(String name, Command<S> command, Complete<S> complete) {
        super(name);
        this.command = command;
        this.complete = complete;
    }

    public ExecutionNode(String name, VoidCommand<S> command, Complete<S> complete) {
        super(name);
        this.command = command;
        this.complete = complete;
    }

    public Command<S> getCommand() {
        return command;
    }

    public Complete<S> getComplete() {
        return complete;
    }

    @Override
    public int execute(CommandContext<S> context) {
        return command.execute(context);
    }

    @Override
    public List<String> complete(CommandContext<S> context) {
        return complete.complete(context);
    }

}
