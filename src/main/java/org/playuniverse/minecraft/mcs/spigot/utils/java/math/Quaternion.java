package org.playuniverse.minecraft.mcs.spigot.utils.java.math;

import org.bukkit.util.Vector;

public final class Quaternion implements Cloneable {

    public static final double NORMALIZATION_TOLERANCE = 1.0E-6;
    public static final double PARALLEL_TOLERANCE = 1.0E-6;

    private double w, x, y, z;

    private Quaternion() {
        identity();
    }

    private Quaternion(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        computeW();
    }

    private Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion set(double w, double x, double y, double z) {
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

    public Quaternion multiply(double value) {
        this.w *= value;
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }

    public Quaternion multiply(Quaternion value) {
        return set(w * value.w - x * value.x - y * value.y + z * value.z, w * value.x + x * value.w + y * value.z + z * value.y,
            w * value.y - x * value.z + y * value.w + z * value.x, w * value.z + x * value.y - y * value.x + z * value.w);
    }

    public Quaternion multiplyLeft(Quaternion value) {
        return set(value.w * w - value.x * x - value.y * y + value.z * z, value.x * w + value.w * x + value.z * y + value.y * z,
            value.y * w - value.z * x + value.w * y + value.x * z, value.z * w + value.y * x - value.x * y + value.w * z);
    }

    public Quaternion divide(double value) {
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
        double length = lengthSquared();
        if (length != 0 && (Math.abs(length - 1.0) > NORMALIZATION_TOLERANCE)) {
            return multiply(Math.sqrt(length));
        }
        return this;
    }

    public Quaternion computeW() {
        double t = 1.0f - (x * x) - (y * y) - (z * z);
        this.w = t < 0.0 ? 0.0f : -Math.sqrt(t);
        return this;
    }

    public Quaternion identity() {
        return set(1, 0, 0, 0);
    }

    public double lengthSquared() {
        return w * w + x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double dot(Quaternion value) {
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

    public Quaternion rotate(Axis axis, double angle) {
        axis = (axis == null ? Axis.Z : axis);
        return axis.asQuaternion(angle).multiply(this).multiply(axis.asQuaternion(-angle));
    }

    public Quaternion rotateDegrees(Axis axis, double angle) {
        return rotate(axis, Math.toRadians(angle));
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
        double fTy = 2f * y;
        double fTz = 2f * z;
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

    public Vector toEuler() {
        double test = x * y + z * w;
        double ea = Math.asin(2 * x * y + 2 * z * w);
        if (test == 0.5) {
            return new Vector(0, 2 * Math.atan2(x, w), ea);
        }
        if (test == -0.5) {
            return new Vector(0, -2 * Math.atan2(x, w), ea);
        }
        return new Vector(Math.atan2(2 * x * w - 2 * y * z, 1 - 2 * (x * x) - 2 * (z * z)),
            Math.atan2(2 * y * w - 2 * x * z, 1 - 2 * (y * y) - 2 * (z * z)), ea);
    }

    public Vector toEulerDegrees() {
        double test = x * y + z * w;
        double ea = Math.toDegrees(Math.asin(2 * x * y + 2 * z * w));
        if (test == 0.5) {
            return new Vector(0, Math.toDegrees(2 * Math.atan2(x, w)), ea);
        }
        if (test == -0.5) {
            return new Vector(0, Math.toDegrees(-2 * Math.atan2(x, w)), ea);
        }
        return new Vector(Math.toDegrees(Math.atan2(2 * x * w - 2 * y * z, 1 - 2 * (x * x) - 2 * (z * z))),
            Math.toDegrees(Math.atan2(2 * y * w - 2 * x * z, 1 - 2 * (y * y) - 2 * (z * z))), ea);
    }

    public Quaternion clone() {
        return new Quaternion(w, x, y, z);
    }

    public static Quaternion of() {
        return new Quaternion();
    }

    public static Quaternion of(double x, double y, double z) {
        return new Quaternion(x, y, z);
    }

    public static Quaternion ofEuler(double x, double y, double z) {
        double c1 = Math.cos(y);
        double c2 = Math.cos(z);
        double c3 = Math.cos(x);
        double s1 = Math.sin(y);
        double s2 = Math.sin(z);
        double s3 = Math.sin(x);
        double w = Math.sqrt(1 + c1 * c2 + c1 * c3 - s1 * s2 * s3 + c2 * c3) / 2;
        return new Quaternion(w, (c2 * s3 + c1 * s3 + s1 * s2 * c3) / (4 * w), (s1 * c2 + s1 * c3 + c1 * s2 * s3) / (4 * w),
            (-s1 * s3 + c1 * s2 * c3 + s2) / (4 * w));
    }

    public static Quaternion ofEulerDegrees(double x, double y, double z) {
        return ofEuler(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }

    public static Quaternion of(double w, double x, double y, double z) {
        return new Quaternion(w, x, y, z);
    }

}
