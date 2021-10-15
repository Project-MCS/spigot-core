package org.playuniverse.minecraft.vcompat.utils.java.math;

public final class Direction {

    private final float yaw;
    private final float pitch;

    public Direction(final float yaw, final float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

}
