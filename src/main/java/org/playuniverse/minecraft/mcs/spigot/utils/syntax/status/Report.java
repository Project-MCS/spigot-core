package org.playuniverse.minecraft.mcs.spigot.utils.syntax.status;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiConsumer;

import org.playuniverse.minecraft.mcs.spigot.utils.java.JavaHelper;
import org.playuniverse.minecraft.mcs.spigot.utils.java.TriConsumer;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.general.Status;

public class Report {

    private final Status status;
    private final Message message;

    private TriConsumer<Message, Status, Duration> finalizer;
    private TriConsumer<Message, Status, Duration> updater;

    private BiConsumer<Status, Duration> task;

    private String action;
    private String format = "%s %s [%s]";

    private boolean condition = true;

    public Report(ILogger logger, Status status) {
        this.status = status;
        this.message = new Message(logger);
    }

    /*
     * Condition
     */

    public boolean condition() {
        return condition;
    }

    public Report condition(boolean condition) {
        this.condition = condition;
        return this;
    }

    /*
     * Setter
     */

    public Report setAction(String action) {
        if (!condition)
            return this;
        this.action = action;
        return this;
    }

    public Report setFormat(String format) {
        if (!condition)
            return this;
        this.format = format;
        return this;
    }

    public Report setTask(BiConsumer<Status, Duration> task) {
        if (!condition)
            return this;
        this.task = task;
        return this;
    }

    public Report setFinalizer(TriConsumer<Message, Status, Duration> finalizer) {
        if (!condition)
            return this;
        this.finalizer = finalizer;
        return this;
    }

    public Report setUpdater(TriConsumer<Message, Status, Duration> updater) {
        if (!condition)
            return this;
        this.updater = updater;
        return this;
    }

    /*
     * Getter
     */

    public Status getStatus() {
        return status;
    }

    public Message getMessage() {
        return message;
    }

    public String getAction() {
        return action;
    }

    public String getFormat() {
        return format;
    }

    /*
     * Functions
     */

    public void execute(final Instant start, final int interval) {
        int ticks = interval;
        while (!status.isDone()) {
            Duration duration = Duration.between(start, Instant.now());
            if (ticks == 0) {
                if (updater == null)
                    message.prefix()
                        .message(format.replace("%action%", action)
                            .replace("%status%", "(" + status.getMarked() + " / " + status.getTotal() + ")")
                            .replace("%time%", JavaHelper.formatDuration(duration) + "s"))
                        .send();
                else
                    updater.accept(message, status, duration);
                ticks = interval;
            }
            if (task != null)
                task.accept(status, duration);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                message.logger().log(e);
            }
            ticks--;
        }
        Duration duration = Duration.between(start, Instant.now());
        if (finalizer == null)
            message.message(String.format(format, action, "(" + status.getMarked() + " / " + status.getTotal() + ")",
                JavaHelper.formatDuration(duration))).send();
        else
            finalizer.accept(message, status, duration);
    }

}
