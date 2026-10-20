package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.ConfigSetting;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EditGuardTest {
    @Test
    public void everyRefusedOperationSaysWhyAndPolicyNamesItsLevel() {
        for (ConfigSetting setting : ConfigSetting.values()) {
            assertTrue(EditGuard.refusalText(setting).startsWith("BuildScape: "));
        }
        assertTrue(EditGuard.refusalText(ConfigSetting.POLICY).contains("level 4"));
        assertTrue(EditGuard.refusalText(ConfigSetting.WORLD_RULES).contains("edit policy"));
    }
}
