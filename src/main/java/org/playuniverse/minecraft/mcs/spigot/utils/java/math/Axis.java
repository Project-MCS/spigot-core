package org.playuniverse.minecraft.mcs.spigot.utils.java.math;

public enum Axis {

    X(1d, 0d, 0d),
    Y(0d, 1d, 0d),
    Z(0d, 0d, 1d);

    private final double x, y, z;

    private Axis(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Quaternion asQuaternion(double angle) {
        double sin = Math.sin(angle);
        return Quaternion.of(Math.cos(angle), sin * x, sin * y, sin * z).normalize();
    }

}
