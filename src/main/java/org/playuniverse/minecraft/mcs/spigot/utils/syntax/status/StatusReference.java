package org.playuniverse.minecraft.mcs.spigot.utils.syntax.status;

import com.syntaxphoenix.syntaxapi.utils.general.Status;

public class StatusReference extends Status {

    private Status status;

    public StatusReference() {
        super(0);
    }

    public StatusReference(int total) {
        super(0);
        this.status = new Status(total);
    }

    public StatusReference(Status status) {
        super(0);
        this.status = status;
    }

    public Status getReference() {
        return status;
    }

    public void setReference(Status status) {
        this.status = status;
    }

    @Override
    public void done() {
        status.done();
    }

    @Override
    public boolean isDone() {
        return status.isDone();
    }

    @Override
    public boolean success() {
        return status.success();
    }

    @Override
    public boolean failed() {
        return status.failed();
    }

    @Override
    public boolean skip() {
        return status.skip();
    }

    @Override
    public boolean cancel() {
        return status.cancel();
    }

    @Override
    public void add() {
        status.add();
    }

    @Override
    public void add(int amount) {
        status.add(amount);
    }

    @Override
    public void add(Status status) {
        status.add(status);
    }

    @Override
    public int getTotal() {
        return status.getTotal();
    }

    @Override
    public int getMarked() {
        return status.getMarked();
    }

    @Override
    public int getFailed() {
        return status.getFailed();
    }

    @Override
    public int getSuccess() {
        return status.getSuccess();
    }

    @Override
    public int getSkipped() {
        return status.getSkipped();
    }

    @Override
    public int getCancelled() {
        return status.getCancelled();
    }
}
