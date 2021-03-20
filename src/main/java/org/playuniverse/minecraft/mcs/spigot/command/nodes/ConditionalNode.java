package org.playuniverse.minecraft.mcs.spigot.command.nodes;

import java.util.function.Predicate;

import org.playuniverse.minecraft.mcs.spigot.command.CommandContext;

public class ConditionalNode<S> extends RootNode<S> {

    private final Predicate<CommandContext<S>> predicate;
    private final Node<S> node;

    public ConditionalNode(Node<S> node, Predicate<CommandContext<S>> predicate) {
        super(node.getName());
        this.node = node;
        this.predicate = predicate;
    }

    @Override
    public int execute(CommandContext<S> context) {
        if (!predicate.test(context)) {
            return -1;
        }
        return node.execute(context);
    }

}