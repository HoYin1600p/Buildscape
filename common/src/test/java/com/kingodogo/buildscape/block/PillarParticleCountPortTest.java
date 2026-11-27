package com.kingodogo.buildscape.block;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PillarParticleCountPortTest {
    @Test
    void zeroDensityDisablesParticlesEvenWithPatternIntensity() {
        assertEquals(0, PillarBlockEntity.particleCount(0, false, 1.0));
        assertEquals(0, PillarBlockEntity.particleCount(0, true, 64.0));
    }

    @Test
    void patternIntensityCanReduceEmissionToZero() {
        assertEquals(0, PillarBlockEntity.particleCount(5, true, 0.0));
        assertEquals(0, PillarBlockEntity.particleCount(1, true, 0.1));
        assertEquals(8, PillarBlockEntity.particleCount(5, true, 1.5));
    }

    @Test
    void simpleParticlesIgnorePatternIntensity() {
        assertEquals(5, PillarBlockEntity.particleCount(5, false, 0.0));
        assertEquals(5, PillarBlockEntity.particleCount(5, false, 64.0));
    }

    @Test
    void emissionStaysBoundedForLargeOrMalformedConfigValues() {
        assertEquals(64, PillarBlockEntity.particleCount(128, true, 64.0));
        assertEquals(64, PillarBlockEntity.particleCount(Integer.MAX_VALUE, true, Double.MAX_VALUE));
        assertEquals(0, PillarBlockEntity.particleCount(-1, true, 1.0));
    }
}
