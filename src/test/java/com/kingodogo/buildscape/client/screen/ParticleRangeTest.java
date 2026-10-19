package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ParticleRangeTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void storedValuesOutsideTheScreenRangeAreShownExactlyAndNeverMoved() {
        assertTrue(ParticleRange.PARTICLE_SPEED.isOutOfRange(4.0D));
        assertEquals("4.00", ParticleRange.PARTICLE_SPEED.formatValue(4.0D));
        assertEquals("0.005", ParticleRange.PARTICLE_SPEED.formatValue(0.005D));
        assertEquals("16.00", ParticleRange.PATTERN_SPREAD.formatValue(16.0D));
        assertEquals("64.0x", ParticleRange.PATTERN_INTENSITY.formatValue(64.0D));
        assertEquals("12000 ticks", ParticleRange.PARTICLE_LIFETIME.formatValue(12000));
        assertEquals("128x", ParticleRange.PARTICLE_DENSITY.formatValue(128));
        assertEquals("0.13", ParticleRange.PATTERN_SPEED.formatValue(0.13D));
        assertFalse(ParticleRange.PATTERN_SPEED.isOutOfRange(0.5D));
        assertFalse(ParticleRange.PATTERN_SPEED.isOutOfRange(0.01D));
        assertTrue(ParticleRange.PARTICLE_DENSITY.isOutOfRange(0));
        assertTrue(ParticleRange.PARTICLE_LIFETIME.isOutOfRange(121));
    }

    @Test
    public void tooltipShowsTheRangeAndFlagsAnOutOfRangeStoredValue() {
        assertEquals("Range: 0.01 - 0.50", ParticleRange.PARTICLE_SPEED.tooltip(0.02D));
        assertEquals("Range: 0.1 - 5.0", ParticleRange.PATTERN_INTENSITY.tooltip(1.0D));
        assertEquals("Range: 5 - 120", ParticleRange.PARTICLE_LIFETIME.tooltip(20));
        assertEquals("Range: 1 - 16", ParticleRange.PARTICLE_DENSITY.tooltip(2));
        assertEquals("Range: 0.01 - 0.50 (stored value 4.00 is outside it)", ParticleRange.PARTICLE_SPREAD.tooltip(4.0D));
    }

    @Test
    public void firstClickWarnsWithTheValueItWillSetAndTheSecondCommits() {
        assertEquals("Particle Speed 4.00 is above the new limit; the next click sets it to 0.50",
                ParticleRange.PARTICLE_SPEED.warning(4.0D));
        assertEquals("Particle Density 0 is below the new minimum; the next click sets it to 1",
                ParticleRange.PARTICLE_DENSITY.warning(0));
        assertEquals(0.5D, ParticleRange.PARTICLE_SPEED.nearestInRange(4.0D), 0.0D);
        assertEquals(1.0D, ParticleRange.PARTICLE_DENSITY.nearestInRange(0), 0.0D);
        assertEquals(120.0D, ParticleRange.PARTICLE_LIFETIME.nearestInRange(12000), 0.0D);

        ParticleRange.Migration migration = new ParticleRange.Migration();
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PARTICLE_SPEED, "global", 4.0D, 1000L));
        assertEquals(ParticleRange.Migration.Step.COMMIT, migration.click(ParticleRange.PARTICLE_SPEED, "global", 4.0D, 2000L));
        // After the commit the value is in range and edits normally; a stale out-of-range value starts over.
        assertEquals(ParticleRange.Migration.Step.EDIT, migration.click(ParticleRange.PARTICLE_SPEED, "global", 0.5D, 3000L));
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PARTICLE_SPEED, "global", 4.0D, 4000L));
    }

    @Test
    public void migrationIsPerSettingPerScopeAndExpires() {
        ParticleRange.Migration migration = new ParticleRange.Migration();
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PARTICLE_SPEED, "global", 4.0D, 0L));
        // Another out-of-range setting is a different migration: its first click warns, and it re-arms only itself.
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PARTICLE_SPREAD, "global", 16.0D, 100L));
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PARTICLE_SPEED, "global", 4.0D, 200L));
        // The same setting on another pillar is separate too.
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PATTERN_SPEED, "pillar-a", 2.0D, 300L));
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PATTERN_SPEED, "pillar-b", 2.0D, 400L));
        assertEquals(ParticleRange.Migration.Step.COMMIT, migration.click(ParticleRange.PATTERN_SPEED, "pillar-b", 2.0D, 500L));
        // A second click after the window only warns again.
        assertEquals(ParticleRange.Migration.Step.WARN, migration.click(ParticleRange.PARTICLE_LIFETIME, "global", 500, 1000L));
        assertEquals(ParticleRange.Migration.Step.WARN,
                migration.click(ParticleRange.PARTICLE_LIFETIME, "global", 500, 1000L + ParticleRange.MIGRATION_WINDOW_MS + 1));
        // An in-range value never warns and never touches another setting's pending migration.
        assertEquals(ParticleRange.Migration.Step.EDIT, migration.click(ParticleRange.PARTICLE_DENSITY, "global", 2, 9000L));
    }

    @Test
    public void uploadWaitsThreeSecondsFromTheLastEditAndCarriesOnlyChangedGroups() {
        ConfigUploadBatch batch = new ConfigUploadBatch();
        assertFalse(batch.hasPending());
        assertFalse(batch.isDue(1_000_000L));
        batch.edited(ConfigSetting.PARTICLES, new PillarParticleConfig(), 1000L);
        assertFalse(batch.isDue(1000L + ConfigUploadBatch.DELAY_MS - 1));
        // Another edit restarts the wait.
        batch.edited(ConfigSetting.PARTICLES, new PillarParticleConfig(), 3500L);
        assertFalse(batch.isDue(1000L + ConfigUploadBatch.DELAY_MS));
        assertFalse(batch.isDue(3500L + ConfigUploadBatch.DELAY_MS - 1));
        batch.edited(ConfigSetting.ITEMS, new PillarParticleConfig(), 4000L);
        assertTrue(batch.isDue(4000L + ConfigUploadBatch.DELAY_MS));
        assertEquals(Set.of(ConfigSetting.PARTICLES, ConfigSetting.ITEMS), batch.take());
        assertFalse(batch.hasPending());
        assertFalse(batch.isDue(99_000L));
        assertTrue(batch.take().isEmpty());
        assertEquals(3000L, ConfigUploadBatch.DELAY_MS);
    }

    @Test
    public void rememberedPresetIsSelectedWhenItStillExistsOtherwiseDefault() {
        List<String> keys = List.of("default", "preset_1", "preset_2");
        assertEquals(2, BuildScapeConfigScreen.presetIndexFor(keys, "preset_2"));
        assertEquals(0, BuildScapeConfigScreen.presetIndexFor(keys, "default"));
        assertEquals(0, BuildScapeConfigScreen.presetIndexFor(keys, "deleted_preset"));
        assertEquals(0, BuildScapeConfigScreen.presetIndexFor(keys, null));
        assertEquals(0, BuildScapeConfigScreen.presetIndexFor(List.of(), "preset_1"));
    }
}
