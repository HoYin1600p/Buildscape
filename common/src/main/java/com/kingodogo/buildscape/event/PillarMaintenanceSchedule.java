package com.kingodogo.buildscape.event;

/** Tick cadence starts after pillar data loads; recovery waits for players. */
final class PillarMaintenanceSchedule {
    private int saveTicks;
    private int backupTicks;
    private int recoveryTicks;
    private boolean recoveryAttempted;

    void tick(Runnable save, Runnable backup, Runnable flush, boolean playersOnline,
              Runnable scheduledRecovery, Runnable initialRecovery) {
        saveTicks++;
        backupTicks++;
        if (backupTicks >= 6000) {
            backupTicks = 0;
            saveTicks = 0;
            backup.run();
        } else if (saveTicks >= 600) {
            saveTicks = 0;
            save.run();
        }
        if (saveTicks % 20 == 0) flush.run();
        if (!playersOnline) return;
        scheduledRecovery.run();
        if (!recoveryAttempted && ++recoveryTicks >= 600) {
            recoveryAttempted = true;
            initialRecovery.run();
        }
    }

    void reset() {
        saveTicks = 0;
        backupTicks = 0;
        recoveryTicks = 0;
        recoveryAttempted = false;
    }
}
