package com.kingodogo.buildscape.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigEditPolicyTest {
    @Test
    void strictModeRequiresPermissionLevelFourForEveryEdit() {
        assertFalse(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.STRICT, 2,
                PillarParticleConfig.EditScope.LIMITED));
        assertTrue(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.STRICT, 4,
                PillarParticleConfig.EditScope.GENERAL));
    }

    @Test
    void standardModeOnlyUnlocksTheLimitedFeatureSetAtLevelTwo() {
        assertTrue(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.STANDARD, 2,
                PillarParticleConfig.EditScope.LIMITED));
        assertFalse(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.STANDARD, 2,
                PillarParticleConfig.EditScope.GENERAL));
        assertTrue(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.STANDARD, 4,
                PillarParticleConfig.EditScope.GENERAL));
    }

    @Test
    void openModeAllowsNormalEditsButNotPermissionPolicyChanges() {
        assertTrue(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.OPEN, 0,
                PillarParticleConfig.EditScope.GENERAL));
        assertFalse(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.OPEN, 2,
                PillarParticleConfig.EditScope.POLICY));
        assertTrue(PillarParticleConfig.canEdit(PillarParticleConfig.EditPermissionMode.OPEN, 4,
                PillarParticleConfig.EditScope.POLICY));
    }

    @Test
    void invalidPolicyNamesFallBackToStrict() {
        assertSame(PillarParticleConfig.EditPermissionMode.STRICT, PillarParticleConfig.parseEditPermissionMode(null));
        assertSame(PillarParticleConfig.EditPermissionMode.STRICT, PillarParticleConfig.parseEditPermissionMode("unknown"));
        assertSame(PillarParticleConfig.EditPermissionMode.STANDARD, PillarParticleConfig.parseEditPermissionMode("standard"));
    }
}
