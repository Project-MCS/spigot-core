package org.playuniverse.minecraft.vcompat.utils.java.math;

import org.bukkit.Location;

public final class MathLocation {

    private static final double TWO_PI = Math.PI * 2;

    private MathLocation() {}

    public static Direction getDirection(Location from, Location to) {
        return getDirection(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    public static Direction getDirection(double x1, double y1, double z1, double x2, double y2, double z2) {
        double x = x2 - x1;
        double y = y2 - y1;
        double z = z2 - z1;
        double theta = Math.atan2(-x, z);
        float yaw = (float) Math.toDegrees((theta + TWO_PI) % TWO_PI);
        float pitch = (float) Math.toDegrees(Math.atan(-y / (Math.sqrt(x * x + z * z))));
        return new Direction(fixFloat(yaw, 0, 360), fixFloat(pitch, -90, 90));
    }

    private static float fixFloat(float value, float min, float max) {
        float output = value;
        if (Float.isInfinite(value)) {
            return max;
        }
        if (Float.isNaN(value)) {
            return min;
        }
        return Math.max(Math.min(output, max), min);
    }

}
