package org.playuniverse.minecraft.mcs.spigot.utils.java.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;

import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

public final class DebugDump {
    
    private DebugDump() {}

    private final static AtomicLong ID = new AtomicLong(0);
    
    static {
        new File("dumps").delete();
    }

    public static PrintStream getDumpStream() {
        File file = new File("dumps/dump-" + ID.getAndIncrement() + ".log");
        Files.createFile(file);
        try {
            return new PrintStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void dump(Object object) {
        try (PrintStream stream = getDumpStream()) {
            if (stream == null) {
                return;
            }
            stream.println(translator(object));
        }
    }

    public static void dump(Object... objects) {
        try (PrintStream stream = getDumpStream()) {
            if (stream == null) {
                return;
            }
            for (Object object : objects) {
                stream.println(translator(object));
            }
        }
    }
    
    private static Object translator(Object object) {
        if(object instanceof Throwable) {
            return Exceptions.stackTraceToString((Throwable) object);
        }
        return object;
    }

}
