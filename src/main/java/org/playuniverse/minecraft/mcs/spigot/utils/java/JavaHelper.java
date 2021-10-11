package org.playuniverse.minecraft.mcs.spigot.utils.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.syntaxphoenix.syntaxapi.utils.java.Streams;

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

    public static Optional<String> getResourceStringFromJar(File file, String name) {
        return getResourceStreamFromJar(file, name).map(stream -> {
            try {
                return Streams.toString(stream);
            } catch (IOException exp) {
                return null;
            }
        });
    }

    public static Optional<InputStream> getResourceStreamFromJar(File file, String name) {
        try (JarFile jarFile = new JarFile(file)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (!name.equalsIgnoreCase(entry.getName())) {
                    continue;
                }
                return Optional.of(jarFile.getInputStream(entry));
            }
        } catch (IOException exp) {
        }
        return Optional.empty();
    }

}
