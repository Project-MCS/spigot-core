package org.playuniverse.minecraft.mcs.spigot.utils.java;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class JavaHelper {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.000");

    private JavaHelper() {}

    public static java.lang.String formatDuration(java.time.Duration duration) {
        return FORMAT
            .format((TimeUnit.SECONDS.toMillis(duration.getSeconds()) + TimeUnit.NANOSECONDS.toMillis(duration.getNano())) / 1000.0f);
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> fromArray(T... array) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }

}
