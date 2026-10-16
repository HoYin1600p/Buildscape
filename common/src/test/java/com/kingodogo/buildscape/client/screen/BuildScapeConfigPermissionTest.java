package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildScapeConfigPermissionTest {
    @Test
    void serverConfigurationRequiresOperatorAccess() {
        assertFalse(BuildScapeConfigScreen.canModifyConfig(false, false));
        assertTrue(BuildScapeConfigScreen.canModifyConfig(true, false));
    }

    @Test
    void clientOnlyPreferencesRemainEditableWithoutOperatorAccess() {
        assertTrue(BuildScapeConfigScreen.canModifyConfig(false, true));
        assertTrue(BuildScapeConfigScreen.canModifyConfig(true, true));
    }
}
