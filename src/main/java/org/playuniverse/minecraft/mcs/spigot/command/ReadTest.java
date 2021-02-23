package org.playuniverse.minecraft.mcs.spigot.command;

@FunctionalInterface
public interface ReadTest {
    
    void test(StringReader reader) throws IllegalArgumentException;

}
