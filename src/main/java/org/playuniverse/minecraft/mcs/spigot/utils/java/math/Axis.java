package org.playuniverse.minecraft.mcs.spigot.utils.java.math;

public enum Axis {

    X(1f, 0f, 0f),
    Y(0f, 1f, 0f),
    Z(0f, 0f, 1f);

    private final float x, y, z;

    private Axis(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Quaternion asQuaternion(float angle) {
        float sin = Maths.sin(angle);
        return Quaternion.of(Maths.cos(angle), sin * x, sin * y, sin * z);
    }

}
