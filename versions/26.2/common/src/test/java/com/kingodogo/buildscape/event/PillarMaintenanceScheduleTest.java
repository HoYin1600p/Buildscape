package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PillarMaintenanceScheduleTest {
    @Test
    void savesEveryThirtySecondsAndReplacesTheTenthSaveWithABackup() {
        PillarMaintenanceSchedule schedule = new PillarMaintenanceSchedule();
        List<Integer> saves = new ArrayList<>();
        List<Integer> backups = new ArrayList<>();
        List<Integer> flushes = new ArrayList<>();
        List<Integer> recoveryChecks = new ArrayList<>();
        List<Integer> initialChecks = new ArrayList<>();
        for (int tick = 1; tick <= 12000; tick++) {
            int now = tick;
            schedule.tick(() -> saves.add(now), () -> backups.add(now), () -> flushes.add(now), true,
                    () -> recoveryChecks.add(now), () -> initialChecks.add(now));
        }
        List<Integer> expectedSaves = new ArrayList<>();
        List<Integer> expectedFlushes = new ArrayList<>();
        for (int tick = 20; tick <= 12000; tick += 20) {
            expectedFlushes.add(tick);
            if (tick % 600 == 0 && tick % 6000 != 0) expectedSaves.add(tick);
        }
        assertEquals(expectedSaves, saves);
        assertEquals(List.of(6000, 12000), backups);
        assertEquals(expectedFlushes, flushes);
        assertEquals(12000, recoveryChecks.size());
        assertEquals(List.of(600), initialChecks);
    }

    @Test
    void emptyServersStillSaveButRecoveryCountsOnlyTicksWithPlayers() {
        PillarMaintenanceSchedule schedule = new PillarMaintenanceSchedule();
        int[] calls = new int[5];
        Runnable save = () -> calls[0]++;
        Runnable backup = () -> calls[1]++;
        Runnable flush = () -> calls[2]++;
        Runnable scheduled = () -> calls[3]++;
        Runnable initial = () -> calls[4]++;
        for (int tick = 0; tick < 6000; tick++) schedule.tick(save, backup, flush, false, scheduled, initial);
        assertEquals(9, calls[0]);
        assertEquals(1, calls[1]);
        assertEquals(300, calls[2]);
        assertEquals(0, calls[3]);
        assertEquals(0, calls[4]);
        for (int tick = 0; tick < 599; tick++) schedule.tick(save, backup, flush, true, scheduled, initial);
        assertEquals(0, calls[4]);
        for (int tick = 0; tick < 600; tick++) schedule.tick(save, backup, flush, false, scheduled, initial);
        schedule.tick(save, backup, flush, true, scheduled, initial);
        assertEquals(600, calls[3]);
        assertEquals(1, calls[4]);
        schedule.reset();
        for (int tick = 0; tick < 599; tick++) schedule.tick(save, backup, flush, true, scheduled, initial);
        assertEquals(1, calls[4]);
        schedule.tick(save, backup, flush, true, scheduled, initial);
        assertEquals(2, calls[4]);
    }
}
