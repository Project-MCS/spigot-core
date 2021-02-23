package org.playuniverse.minecraft.mcs.spigot.utils.general;

public interface IEnum {

    default String id(String name) {
        return name.toLowerCase().replace('_', '.').replace("000", "_");
    }

}
