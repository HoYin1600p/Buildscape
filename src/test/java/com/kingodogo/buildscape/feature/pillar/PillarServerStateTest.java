package com.kingodogo.buildscape.feature.pillar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.After;
import org.junit.Test;

public class PillarServerStateTest {

    @After
    public void dropTestState() {
        PillarIdManager.fullReset();
    }

    @Test
    public void currentStateHoldsTheServerRegistry() {
        PillarServerState state = PillarServerState.current();
        assertNotNull(state);
        assertSame(state.manager, PillarIdManager.get());
        assertSame(state.manager, PillarIdManager.get(null));
        assertTrue(state.manager.serverAuthoritative);
        assertNotSame(PillarIdManager.getClient(), PillarIdManager.get());
        assertFalse(PillarIdManager.getClient().serverAuthoritative);
    }

    @Test
    public void freshStateIsEmptyWithTheWorldLoadClockStarted() {
        long before = System.currentTimeMillis();
        PillarIdManager.resetWorldCache();
        PillarServerState state = PillarServerState.current();

        assertTrue(state.manager.getAllData().isEmpty());
        assertFalse(state.manager.hasLoaded());
        assertFalse(state.manager.isLoadInProgress());
        assertNull(state.cachedWorldSaveDir);
        assertFalse(state.recoveryScheduled);
        assertFalse(state.recoveryClearColors);
        assertFalse(state.recoveryInProgress);
        assertFalse(PillarRecovery.isInProgress());
        assertTrue(state.worldLoadStartTime >= before);
        assertTrue(state.worldLoadStartTime <= System.currentTimeMillis());
    }

    @Test
    public void nothingFromOneLifecycleReachesTheNext() {
        PillarIdManager.resetWorldCache();
        PillarServerState first = PillarServerState.current();
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = "S-Pfirst";
        data.dimension = "minecraft:overworld";
        first.manager.registry.pillarData.put(data.id, data);
        first.manager.registry.positionIndex.put("minecraft:overworld|0,64,0", data.id);
        first.cachedWorldSaveDir = new File("first-world");
        PillarRecovery.schedule(true);
        first.recoveryInProgress = true;
        assertTrue(PillarRecovery.isInProgress());

        PillarIdManager.fullReset();
        PillarServerState second = PillarServerState.current();

        assertNotSame(first, second);
        assertNotSame(first.manager, second.manager);
        assertSame(second.manager, PillarIdManager.get());
        assertTrue(PillarIdManager.get().getAllData().isEmpty());
        assertTrue(second.manager.registry.positionIndex.isEmpty());
        assertNull(second.cachedWorldSaveDir);
        assertFalse(second.recoveryScheduled);
        assertFalse(second.recoveryClearColors);
        assertFalse(PillarRecovery.isInProgress());

        PillarIdManager.resetWorldCache();
        assertNotSame(second, PillarServerState.current());
        assertTrue(PillarIdManager.get().getAllData().isEmpty());
    }
}
