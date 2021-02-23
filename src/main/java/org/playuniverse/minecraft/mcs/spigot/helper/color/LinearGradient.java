package org.playuniverse.minecraft.mcs.spigot.helper.color;

import org.playuniverse.minecraft.mcs.spigot.helper.ColorHelper;

public class LinearGradient extends ColorIterator {

    private final float[] color0;
    private final float[] color1;

    private final float steps;
    private int step;

    public LinearGradient(float[] color0, float[] color1, int steps) {
        this.color0 = color0;
        this.color1 = color1;
        this.steps = steps;
    }

    @Override
    public boolean hasNext() {
        return step != steps;
    }

    @Override
    public float[] nextColor() {
        if (!hasNext()) {
            return null;
        }
        float ratio = (step += 1) / steps;
        float[] output = new float[3];
        output[0] = ColorHelper.interpolate(ratio, color0[0], color1[0]);
        output[1] = ColorHelper.interpolate(ratio, color0[1], color1[1]);
        output[2] = ColorHelper.interpolate(ratio, color0[2], color1[2]);
        return output;
    }

    @Override
    public void reset() {
        step = 0;
    }

}
