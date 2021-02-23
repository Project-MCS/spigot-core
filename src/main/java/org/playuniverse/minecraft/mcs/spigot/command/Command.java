package org.playuniverse.minecraft.mcs.spigot.command;

@FunctionalInterface
public interface Command<S> {
    
    int execute(CommandContext<S> context);

}
