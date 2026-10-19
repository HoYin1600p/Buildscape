package com.kingodogo.buildscape.fluid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExperienceFluidMatchTest {
    private final Object still = new Object();
    private final Object flowing = new Object();

    @Test
    public void matchesStillAndFlowing() {
        assertTrue(ExperienceFluidMatch.matches(still, still, flowing));
        assertTrue(ExperienceFluidMatch.matches(flowing, still, flowing));
    }

    @Test
    public void otherFluidDoesNotMatch() {
        assertFalse(ExperienceFluidMatch.matches(new Object(), still, flowing));
    }

    @Test
    public void absentCandidatesNeverMatch() {
        assertFalse(ExperienceFluidMatch.matches(still, null, null));
        assertTrue(ExperienceFluidMatch.matches(flowing, null, flowing));
        assertTrue(ExperienceFluidMatch.matches(still, still, null));
    }

    @Test
    public void nullFluidNeverMatchesEvenWhenCandidatesAreAbsent() {
        assertFalse(ExperienceFluidMatch.matches(null, null, null));
        assertFalse(ExperienceFluidMatch.matches(null, still, flowing));
        assertFalse(ExperienceFluidMatch.matches(null, (Object) null));
    }

    @Test
    public void singleCandidateMatch() {
        assertTrue(ExperienceFluidMatch.matches(still, still));
        assertFalse(ExperienceFluidMatch.matches(flowing, still));
        assertFalse(ExperienceFluidMatch.matches(still, null));
    }
}
