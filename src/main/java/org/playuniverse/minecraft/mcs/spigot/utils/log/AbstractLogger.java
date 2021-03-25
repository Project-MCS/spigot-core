package org.playuniverse.minecraft.mcs.spigot.utils.log;

import java.awt.Color;
import java.util.function.BiConsumer;

import org.playuniverse.minecraft.mcs.spigot.helper.ColorHelper;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.logging.LoggerState;
import com.syntaxphoenix.syntaxapi.logging.color.LogType;
import com.syntaxphoenix.syntaxapi.logging.color.LogTypeMap;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.Times;

public abstract class AbstractLogger<T extends AbstractLogger<T>> implements ILogger {

    public static final String DEFAULT_FORMAT = "[%date% / %prefix% => %type%] %message%";

    protected final LogTypeMap typeMap = new LogTypeMap();
    protected boolean colored = false;
    protected String format = DEFAULT_FORMAT;

    protected LoggerState state = LoggerState.STREAM_CUSTOM;
    protected String threadOverride;

    @Override
    public T setThreadName(String name) {
        this.threadOverride = name;
        return instance();
    }

    @Override
    public String getThreadName() {
        if (this.threadOverride == null) {
            return Thread.currentThread().getName();
        } else {
            String name = this.threadOverride;
            this.threadOverride = null;
            return name;
        }
    }

    @Override
    public T setState(LoggerState state) {
        this.state = state;
        return instance();
    }

    @Override
    public LoggerState getState() {
        return this.state;
    }

    @Override
    public T setColored(boolean colored) {
        this.colored = colored;
        return instance();
    }

    @Override
    public boolean isColored() {
        return this.colored;
    }

    public T setFormat(String format) {
        this.format = format;
        return instance();
    }

    public String getFormat() {
        return this.format;
    }

    /*
     * Types
     */

    public T setDefaultTypes() {
        setType("warning", "#E89102");
        setType("error", "#FF0000");
        setType("debug", "#2FE4E7");
        setType("info", "#949494");
        return instance();
    }

    public T setType(String name, String hex) {
        return setType(new BukkitLogType(name, hex));
    }

    public T setType(String name, Color color) {
        return setType(name, ColorHelper.toHexColor(color));
    }

    @Override
    public T setType(LogType type) {
        this.typeMap.override(type);
        return instance();
    }

    @Override
    public LogType getType(String typeId) {
        return typeMap.tryGetById(typeId).orElse(BukkitLogType.DEFAULT);
    }

    @Override
    public LogTypeMap getTypeMap() {
        return this.typeMap;
    }

    /*
     * Logging
     */

    //
    // Single
    //

    @Override
    public T log(String message) {
        return log(LogTypeId.INFO, message);
    }

    @Override
    public T log(LogTypeId type, String message) {
        return log(type.id(), message);
    }

    @Override
    public T log(String typeId, String message) {
        return log(getType(typeId), message);
    }

    public T log(LogType type, String message) {
        return println(type, format(type.getName(), getThreadName(), message));
    }

    //
    // Multiple
    //

    @Override
    public T log(String... messages) {
        return log(LogTypeId.INFO, messages);
    }

    @Override
    public T log(LogTypeId type, String... messages) {
        return log(type.id(), messages);
    }

    @Override
    public T log(String typeId, String... messages) {
        return log(getType(typeId), messages);
    }

    public T log(LogType type, String... messages) {
        if (messages == null || messages.length == 0) {
            return instance();
        }
        String thread = getThreadName();
        for (String message : messages) {
            setThreadName(thread);
            log(type, message);
        }
        return instance();
    }

    //
    // Throwables
    //

    @Override
    public T log(Throwable throwable) {
        return log(LogTypeId.ERROR, throwable);
    }

    @Override
    public T log(LogTypeId type, Throwable throwable) {
        return log(type.id(), throwable);
    }

    @Override
    public T log(String typeId, Throwable throwable) {
        return log(getType(typeId), throwable);
    }

    public T log(LogType type, Throwable throwable) {
        return log(type, asStringArray(throwable));
    }

    /*
     * Printing
     */

    public T println(LogType type, String message) {
        if (!colored) {
            return println(message);
        }
        if (state.useCustom() && hasCustom()) {
            println(true, type.asColorString(false) + message);
        }
        if (state.useStream() && hasStream()) {
            println(false, type.asColorString(true) + message);
        }
        return instance();
    }

    public T println(String message) {
        if (state.useCustom() && hasCustom()) {
            println(true, message);
        }
        if (state.useStream() && hasStream()) {
            println(false, message);
        }
        return instance();
    }

    public T print(LogType type, String message) {
        if (!colored) {
            return print(message);
        }
        if (state.useCustom() && hasCustom()) {
            print(true, type.asColorString(true) + message);
        }
        if (state.useStream() && hasStream()) {
            print(false, type.asColorString(false) + message);
        }
        return instance();
    }

    public T print(String message) {
        if (state.useCustom() && hasCustom()) {
            print(true, message);
        }
        if (state.useStream() && hasStream()) {
            print(false, message);
        }
        return instance();
    }

    /*
     * Implementation
     */

    @Override
    public T setCustom(BiConsumer<Boolean, String> custom) {
        return instance();
    }

    @Override
    public BiConsumer<Boolean, String> getCustom() {
        return (state, value) -> {
        };
    }

    /*
     * Abstraction
     */

    @Override
    public abstract T close();

    public abstract boolean hasCustom();

    public abstract boolean hasStream();

    protected abstract T instance();

    protected String[] asStringArray(Throwable throwable) {
        return Exceptions.stackTraceToStringArray(throwable);
    }

    protected String format(String type, String thread, String message) {
        return format.replace("%type%", type).replace("%thread%", thread).replace("%message%", message)
            .replace("%time%", Times.getTime(":")).replace("%date%", Times.getDate("."));
    }

    protected abstract void println(boolean custom, String message);

    protected abstract void print(boolean custom, String message);

}
