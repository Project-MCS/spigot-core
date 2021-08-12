package org.playuniverse.minecraft.mcs.spigot.utils.java.math;

import org.bukkit.util.Vector;

public final class Quaternion implements Cloneable {

    public static final float NORMALIZATION_TOLERANCE = 1.0E-6f;
    public static final float PARALLEL_TOLERANCE = 1.0E-6f;

    private float w, x, y, z;

    private Quaternion() {
        identity();
    }

    private Quaternion(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        computeW();
    }

    private Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion set(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Quaternion add(Quaternion value) {
        this.w += value.w;
        this.x += value.x;
        this.y += value.y;
        this.z += value.z;
        return this;
    }

    public Quaternion subtract(Quaternion value) {
        this.w -= value.w;
        this.x -= value.x;
        this.y -= value.y;
        this.z -= value.z;
        return this;
    }

    public Quaternion multiply(float value) {
        this.w *= value;
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }

    public Quaternion multiply(Quaternion value) {
        float ow = w * value.w - x * value.x - y * value.y + z * value.z;
        float ox = w * value.x + x * value.w + y * value.z + z * value.y;
        float oy = w * value.y - x * value.z + y * value.w + z * value.x;
        float oz = w * value.z + x * value.y - y * value.x + z * value.w;
        return set(ow, ox, oy, oz);
    }

    public Quaternion multiplyLeft(Quaternion value) {
        float ow = value.w * w - value.x * x - value.y * y + value.z * z;
        float ox = value.x * w + value.w * x + value.z * y + value.y * z;
        float oy = value.y * w - value.z * x + value.w * y + value.x * z;
        float oz = value.z * w + value.y * x - value.x * y + value.w * z;
        return set(ow, ox, oy, oz);
    }

    public Quaternion divide(float value) {
        this.w /= value;
        this.x /= value;
        this.y /= value;
        this.z /= value;
        return this;
    }

    public Quaternion inverse() {
        return conjugate().multiply(1 / lengthSquared());
    }

    public Quaternion conjugate() {
        this.x = -x;
        this.y = -y;
        this.z = -z;
        return this;
    }

    public Quaternion normalize() {
        float length = lengthSquared();
        if (length != 0 && (Math.abs(length - 1.0) > NORMALIZATION_TOLERANCE)) {
            return multiply(FastMath.Q_rsqrt(length, 2));
        }
        return this;
    }

    public Quaternion computeW() {
        float t = 1.0f - (x * x) - (y * y) - (z * z);
        this.w = t < 0.0 ? 0.0f : -Maths.sqrt(t);
        return this;
    }

    public Quaternion identity() {
        return set(1, 0, 0, 0);
    }

    public float lengthSquared() {
        return w * w + x * x + y * y + z * z;
    }

    public float length() {
        return Double.valueOf(Math.sqrt(lengthSquared())).floatValue();
    }

    public float dot(Quaternion value) {
        return w * value.w + x * value.x + y * value.y + z * value.z;
    }

    public Vector multiply(Vector vector) {
        Vector tmp1 = new Vector(x, y, z);
        Vector tmp2 = tmp1.clone();
        tmp1.crossProduct(vector);
        tmp2.crossProduct(tmp1);
        tmp1.multiply(2f * w);
        tmp2.multiply(2f);
        tmp1.add(tmp2);
        tmp1.add(vector);
        return tmp1;
    }

    public Quaternion rotate(Axis axis, float angle) {
        return multiply((axis == null ? Axis.Z : axis).asQuaternion(angle));
    }

    public Vector getAxis(Axis axis) {
        switch (axis) {
        case X:
            return getXAxis();
        case Y:
            return getYAxis();
        default:
            return getZAxis();
        }
    }

    public Vector getXAxis() {
        float fTy = 2f * y;
        float fTz = 2f * z;
        return new Vector(1 - (fTy * y + fTz * z), fTy * x + fTz * w, fTz * x - fTy * w);
    }

    public Vector getYAxis() {
        double fTx = 2f * x;
        double fTz = 2f * z;
        return new Vector(fTx * y - fTz * w, 1 - (fTx * x + fTz * z), fTz * y + fTx * w);
    }

    public Vector getZAxis() {
        double fTx = 2f * x;
        double fTy = 2f * y;
        return new Vector(fTx * z + fTy * w, fTy * z - fTx * w, 1 - (fTx * x + fTy * y));
    }

    public Quaternion clone() {
        return new Quaternion(w, x, y, z);
    }

    public static Quaternion of() {
        return new Quaternion();
    }

    public static Quaternion of(float x, float y, float z) {
        return new Quaternion(x, y, z);
    }

    public static Quaternion of(float w, float x, float y, float z) {
        return new Quaternion(w, x, y, z);
    }

}
