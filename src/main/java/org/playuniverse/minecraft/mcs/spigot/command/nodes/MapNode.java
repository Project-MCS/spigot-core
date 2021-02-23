package org.playuniverse.minecraft.mcs.spigot.command.nodes;

import java.util.function.Function;

import org.playuniverse.minecraft.mcs.spigot.command.CommandContext;

public class MapNode<OS, NS> extends RootNode<OS> {

    private final Function<OS, NS> function;
    private final Node<NS> node;

    public MapNode(Function<OS, NS> function, Node<NS> node) {
        super(node.getName());
        this.function = function;
        this.node = node;
    }

    @Override
    public int execute(CommandContext<OS> context) {
        return node.execute(new CommandContext<>(function.apply(context.getSource()), context.getReader()));
    }

}
