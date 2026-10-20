package com.kingodogo.buildscape.feature.pillar;

import java.io.File;

/**
 * The pillar state of one server lifecycle: the server registry, the cached world data folder and the recovery flags.
 * {@link #replace()} drops the current state and installs a fresh one; it runs when a server is starting and again when
 * it has stopped (through {@link PillarIdManager#resetWorldCache()} and {@link PillarIdManager#fullReset()}), so one
 * world's records never reach the next world opened in the same game session.
 */
final class PillarServerState {

    private static volatile PillarServerState current = new PillarServerState(0L);

    final PillarIdManager manager = PillarIdManager.newServerInstance();

    File cachedWorldSaveDir = null;

    boolean recoveryScheduled = false;
    long recoveryScheduledTime = 0L;
    boolean recoveryClearColors = false;
    boolean recoveryInProgress = false;
    long worldLoadStartTime;

    private PillarServerState(long worldLoadStartTime) {
        this.worldLoadStartTime = worldLoadStartTime;
    }

    /** The state of the current (or, between servers, the next) server lifecycle. */
    static PillarServerState current() {
        return current;
    }

    /** Drops the current state and starts a fresh one, with the world-load clock starting now. */
    static PillarServerState replace() {
        PillarServerState fresh = new PillarServerState(System.currentTimeMillis());
        current = fresh;
        return fresh;
    }
}
