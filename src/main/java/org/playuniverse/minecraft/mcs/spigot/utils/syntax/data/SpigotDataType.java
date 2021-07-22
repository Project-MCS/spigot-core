package org.playuniverse.minecraft.mcs.spigot.utils.syntax.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;

public final class SpigotDataType {

    private SpigotDataType() {}

    public static final IDataType<IDataContainer, Location> LOCATION = IDataType.of(IDataContainer.class, Location.class,
        (container, context) -> {
            double x = container.get("x", IDataType.DOUBLE);
            double y = container.get("y", IDataType.DOUBLE);
            double z = container.get("z", IDataType.DOUBLE);
            float yaw = container.get("yaw", IDataType.FLOAT);
            float pitch = container.get("pitch", IDataType.FLOAT);
            if (container.has("world", IDataType.STRING)) {
                return new Location(Bukkit.getWorld(container.get("world", IDataType.STRING)), x, y, z, yaw, pitch);
            }
            return new Location(null, x, y, z, yaw, pitch);
        }, (location, context) -> {
            IDataContainer container = context.newContainer();
            container.set("x", location.getX(), IDataType.DOUBLE);
            container.set("y", location.getY(), IDataType.DOUBLE);
            container.set("z", location.getZ(), IDataType.DOUBLE);
            container.set("yaw", location.getPitch(), IDataType.FLOAT);
            container.set("pitch", location.getYaw(), IDataType.FLOAT);
            if (location.getWorld() != null) {
                container.set("world", location.getWorld().getName(), IDataType.STRING);
            }
            return container;
        });

}