package org.playuniverse.minecraft.mcs.spigot.utils.syntax.status;

import java.time.Duration;
import java.time.Instant;

import org.playuniverse.minecraft.mcs.spigot.utils.java.JavaHelper;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.logging.color.LogType;
import com.syntaxphoenix.syntaxapi.utils.general.Status;
import com.syntaxphoenix.syntaxapi.utils.java.lang.StringBuilder;

public final class Message {

    private final StringBuilder builder = new StringBuilder();
    private final ILogger logger;

    private String prefix;
    private String head;

    private boolean condition = true;

    public Message() {
        this.logger = null;
    }

    public Message(ILogger logger) {
        this.logger = logger;
    }

    public ILogger logger() {
        return logger;
    }

    /*
     * Condition
     */

    public boolean condition() {
        return condition;
    }

    public Message condition(boolean condition) {
        this.condition = condition;
        return this;
    }

    public String getHead() {
        return head;
    }

    public Message setHead(String head) {
        this.head = head;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Message setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /*
     * Basic messages
     */

    public Message objects(Object... objects) {
        if (!condition)
            return this;
        for (Object object : objects)
            builder.append(object);
        return this;
    }

    public Message messages(String... messages) {
        if (!condition)
            return this;
        for (String message : messages)
            builder.append(message);
        return this;
    }

    public Message message(String message) {
        if (!condition)
            return this;
        builder.append(message);
        return this;
    }

    public Message line() {
        if (!condition)
            return this;
        builder.append(System.lineSeparator());
        return this;
    }

    public Message space() {
        if (!condition)
            return this;
        builder.append(' ');
        return this;
    }

    public Message character(char character) {
        if (!condition)
            return this;
        builder.append(character);
        return this;
    }

    /*
     * Presets
     */

    public Message prefix() {
        return message(prefix);
    }

    public Message header() {
        return header(head);
    }

    public Message header(String content) {
        return line().line().message("=========> [ " + content + " ] <=========").line();
    }

    public Message status(Status status) {
        return message("&8Total: &7" + status.getTotal()).line().line().message("&2Successful: &a" + status.getSuccess()).line()
            .message("&6Skipped: &e" + status.getSkipped()).line().message("&4Failed: &c" + status.getFailed());
    }

    public Message split(String name) {
        return messages("&8==> &d" + name);
    }

    public Message smallStatus(Status status) {
        return messages("(", status.getMarked() + " / " + status.getTotal(), ")");
    }

    public Message duration(Instant start, boolean small) {
        return duration(Duration.between(start, Instant.now()), small);
    }

    public Message duration(Duration duration, boolean small) {
        return small ? messages(JavaHelper.formatDuration(duration), "s")
            : message(JavaHelper.formatDuration(duration)).space().message("seconds");
    }

    public Message footer() {
        return footer(head);
    }

    public Message footer(String content) {
        return line().message("=========> [ " + content + " ] <=========").line();
    }

    /*
     * Sending
     */

    public String asString() {
        return builder.toStringClear();
    }

    public Message send() {
        if (logger == null)
            return this;
        return send(logger);
    }

    public Message send(LogTypeId type) {
        return send(type, logger);
    }

    public Message send(String type) {
        return send(type, logger);
    }

    public Message send(LogType type) {
        return send(type, logger);
    }

    public Message send(ILogger logger) {
        return send(LogTypeId.INFO, logger);
    }

    public Message send(LogTypeId type, ILogger logger) {
        logger.log(type, asString());
        return this;
    }

    public Message send(String type, ILogger logger) {
        logger.log(type, asString());
        return this;
    }

    public Message send(LogType type, ILogger logger) {
        logger.setType(type).log(type.getId(), asString());
        return this;
    }

}
