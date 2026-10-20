package com.kingodogo.buildscape.feature.homemaker;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class HomemakerCooldownTrackerTest {

    @After
    public void clearCooldown() {
        HomemakerCooldownTracker.setCooldownEndTime(0L);
    }

    @Test
    public void storesTheLastValueSentByTheServer() {
        HomemakerCooldownTracker.setCooldownEndTime(1_800_000L);
        assertEquals(1_800_000L, HomemakerCooldownTracker.getCooldownEndTime());

        HomemakerCooldownTracker.setCooldownEndTime(0L);
        assertEquals(0L, HomemakerCooldownTracker.getCooldownEndTime());
    }
}
