package org.playuniverse.minecraft.mcs.spigot.command.nodes;

import java.util.List;

import org.playuniverse.minecraft.mcs.spigot.command.CommandContext;
import org.playuniverse.minecraft.mcs.spigot.command.IModule;

public class PluginNode<S> extends RootNode<S> {

    private final RootNode<S> node;
    private final IModule plugin;

    public PluginNode(IModule plugin, RootNode<S> node) {
        super(plugin.getId() + ':' + node.getName());
        if (node instanceof PluginNode) {
            throw new IllegalArgumentException("A plugin node cannot contain another plugin node!");
        }
        this.plugin = plugin;
        this.node = node;
    }

    public IModule getPlugin() {
        return plugin;
    }

    public RootNode<S> getRoot() {
        return node;
    }

    @Override
    public int execute(CommandContext<S> context) {
        return node.execute(context);
    }
    
    @Override
    public List<String> complete(CommandContext<S> context) {
        return node.complete(context);
    }

}
