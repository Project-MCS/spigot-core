package org.playuniverse.minecraft.mcs.spigot.utils.java.math;

public final class FastMath {

    private static final double TWO_POWER_52 = 4503599627370496.0;

    private FastMath() {}

    public static float step(float edge, float value) {
        return value < edge ? 0f : 1f;
    }

    public static int Q_rsqrt(Integer value) {
        return Q_rsqrt(value, 3);
    }

    public static int Q_rsqrt(Integer value, int accuracy) {
        return (int) floor(Q_rsqrt(value.floatValue(), accuracy));
    }

    public static float Q_rsqrt(float value) {
        return Q_rsqrt(value, 3);
    }

    public static float Q_rsqrt(float value, int accuracy) {
        float x = value;
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 1597463007 - (i >> 1);
        x = Float.intBitsToFloat(i);
        for (int it = 0; it < accuracy; it++) {
            x = x * (1.5f - xhalf * x * x);
        }
        x *= value;
        return x;
    }

    public static long Q_rsqrt(Long value) {
        return Q_rsqrt(value, 3);
    }

    public static long Q_rsqrt(Long value, int accuracy) {
        return (long) floor(Q_rsqrt(value.doubleValue(), accuracy));
    }

    public static double Q_rsqrt(double value) {
        return Q_rsqrt(value, 3);
    }

    public static double Q_rsqrt(double value, int accuracy) {
        double x = value;
        double xhalf = 0.5 * x;
        long i = Double.doubleToLongBits(x);
        i = 6910469410427058090L - (i >> 1);
        x = Double.longBitsToDouble(i);
        for (int it = 0; it < accuracy; it++) {
            x = x * (1.5f - xhalf * x * x);
        }
        x *= value;
        return x;
    }

    public static float sin(float value) {
        return (float) Math.sin(value);
    }

    public static float floor(float value) {
        return (float) floor((double) value);
    }

    public static double floor(double x) {
        long y;
        if (Double.isNaN(x)) {
            return x;
        }
        if (x >= TWO_POWER_52 || x <= -TWO_POWER_52) {
            return x;
        }
        y = (long) x;
        if (x < 0 && y != x) {
            y--;
        }
        if (y == 0) {
            return x * y;
        }
        return y;
    }

}
