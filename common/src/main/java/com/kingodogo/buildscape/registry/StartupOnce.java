package com.kingodogo.buildscape.registry;

import java.util.Objects;

/** Prevents duplicate initialization, including retries after a partially completed startup. */
public final class StartupOnce {
    private enum State { NEW, RUNNING, COMPLETE, FAILED }
    private State state = State.NEW;
    private Throwable failure;

    public synchronized void run(Runnable initializer) {
        Objects.requireNonNull(initializer, "initializer");
        if (state == State.COMPLETE) return;
        if (state == State.RUNNING) throw new IllegalStateException("Recursive startup");
        if (state == State.FAILED) throw new IllegalStateException("Startup previously failed", failure);
        state = State.RUNNING;
        try {
            initializer.run();
            state = State.COMPLETE;
        } catch (RuntimeException | Error exception) {
            failure = exception;
            state = State.FAILED;
            throw exception;
        }
    }
}
