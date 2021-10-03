package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.util.function.BiConsumer;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.logging.LoggerState;
import com.syntaxphoenix.syntaxapi.logging.color.LogType;
import com.syntaxphoenix.syntaxapi.logging.color.LogTypeMap;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

class PluginLogger implements ILogger {

    private final ILogger logger;
    private final SpigotModule<?> plugin;

    public PluginLogger(ILogger logger, SpigotModule<?> plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    /*
     * 
     */

    @Override
    public ILogger close() {
        logger.close();
        return this;
    }

    /*
     * 
     */

    @Override
    public ILogger setThreadName(String name) {
        logger.setThreadName(name);
        return this;
    }

    @Override
    public String getThreadName() {
        return logger.getThreadName();
    }

    @Override
    public ILogger setState(LoggerState state) {
        logger.setState(state);
        return this;
    }

    @Override
    public LoggerState getState() {
        return logger.getState();
    }

    @Override
    public ILogger setCustom(BiConsumer<Boolean, String> custom) {
        logger.setCustom(custom);
        return this;
    }

    @Override
    public BiConsumer<Boolean, String> getCustom() {
        return logger.getCustom();
    }

    @Override
    public ILogger setType(LogType type) {
        logger.setType(type);
        return this;
    }

    @Override
    public LogType getType(String typeId) {
        return logger.getType(typeId);
    }

    @Override
    public ILogger setColored(boolean color) {
        logger.setColored(color);
        return this;
    }

    @Override
    public boolean isColored() {
        return logger.isColored();
    }

    @Override
    public LogTypeMap getTypeMap() {
        return logger.getTypeMap();
    }

    /*
     * 
     */

    @Override
    public ILogger log(String message) {
        return log(LogTypeId.INFO, message);
    }

    @Override
    public ILogger log(LogTypeId type, String message) {
        return log(type.id(), message);
    }

    @Override
    public ILogger log(String typeId, String message) {
        logger.log(typeId, plugin.getPrefix() + ' ' + message);
        return this;
    }

    /*
     * 
     */

    @Override
    public ILogger log(String... messages) {
        return log(LogTypeId.INFO, messages);
    }

    @Override
    public ILogger log(LogTypeId type, String... messages) {
        return log(type.id(), messages);
    }

    @Override
    public ILogger log(String typeId, String... messages) {
        if (messages == null || messages.length == 0) {
            return this;
        }
        String thread = getThreadName();
        for (String message : messages) {
            setThreadName(thread);
            log(typeId, message);
        }
        return this;
    }

    /*
     * 
     */

    @Override
    public ILogger log(Throwable throwable) {
        return log(LogTypeId.ERROR, throwable);
    }

    @Override
    public ILogger log(LogTypeId type, Throwable throwable) {
        return log(type.id(), throwable);
    }

    @Override
    public ILogger log(String typeId, Throwable throwable) {
        return log(typeId, Exceptions.stackTraceToStringArray(throwable));
    }

}
