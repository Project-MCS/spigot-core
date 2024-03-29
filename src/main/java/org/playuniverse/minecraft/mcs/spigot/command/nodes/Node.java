package org.playuniverse.minecraft.mcs.spigot.command.nodes;

import java.util.List;

import org.playuniverse.minecraft.mcs.spigot.command.CommandContext;

public abstract class Node<S> {

    protected final String name;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public abstract int execute(CommandContext<S> context);
    
    public List<String> complete(CommandContext<S> context) {
        return null;
    }

}
