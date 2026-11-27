package com.kingodogo.buildscape.block;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** BuildScape copper weathers like vanilla, and the stage tables only name real blocks. */
class CopperWeatheringTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void lonelyCopperUsesTheVanillaChanceAndUnaffectedCopperIsSlower() {
        assertEquals(0.75F, CopperWeathering.chance(0, 0, 0), 0.0F);
        assertEquals(1.0F, CopperWeathering.chance(1, 0, 0), 0.0F);
        assertEquals(1.0F, CopperWeathering.chance(2, 0, 0), 0.0F);
    }

    @Test
    void sameAgeNeighboursSlowWeatheringAndOlderNeighboursSpeedItBackUp() {
        // (older + 1) / (older + same + 1), squared, as in ChangeOverTimeBlock.
        assertEquals(0.25F, CopperWeathering.chance(1, 1, 0), 1.0E-6F);
        assertEquals(1.0F / 16.0F, CopperWeathering.chance(1, 3, 0), 1.0E-6F);
        assertEquals(4.0F / 9.0F, CopperWeathering.chance(1, 1, 1), 1.0E-6F);
        assertEquals(0.75F * 0.25F, CopperWeathering.chance(0, 1, 0), 1.0E-6F);
    }

    @Test
    void vanillaCopperAgesComeFromItsOwnChainAndWaxedCopperDoesNotWeather() {
        assertEquals(0, CopperWeathering.age(Blocks.COPPER_BLOCK.defaultBlockState()));
        assertEquals(1, CopperWeathering.age(Blocks.EXPOSED_CUT_COPPER_STAIRS.defaultBlockState()));
        assertEquals(2, CopperWeathering.age(Blocks.WEATHERED_CUT_COPPER.defaultBlockState()));
        assertEquals(3, CopperWeathering.age(Blocks.OXIDIZED_CUT_COPPER_SLAB.defaultBlockState()));
        assertEquals(-1, CopperWeathering.age(Blocks.WAXED_COPPER_BLOCK.defaultBlockState()));
        assertEquals(-1, CopperWeathering.age(Blocks.STONE.defaultBlockState()));
    }

    @Test
    void stagePrefixesGiveTheAgeOfRestoredFamilies() {
        assertEquals(0, CopperWeathering.ageFromPath("copper_lantern"));
        assertEquals(1, CopperWeathering.ageFromPath("exposed_copper_lantern"));
        assertEquals(2, CopperWeathering.ageFromPath("weathered_copper_lantern"));
        assertEquals(3, CopperWeathering.ageFromPath("oxidized_copper_lantern"));
        assertEquals(-1, CopperWeathering.ageFromPath("waxed_exposed_copper_lantern"));
    }

    @Test
    void bitCopperStepsThroughItsOwnChainButNeverWeathersOnItsOwn() {
        CopperOxidationHandler.initTables(false);
        assertEquals("bit_exposed_cut_copper", CopperOxidationHandler.nextStage("bit_cut_copper"));
        assertEquals("bit_weathered_copper_grate", CopperOxidationHandler.nextStage("bit_exposed_copper_grate"));
        assertEquals("bit_oxidized_copper_bulb", CopperOxidationHandler.nextStage("bit_weathered_copper_bulb"));
        assertEquals("bit_exposed_copper_block_wall", CopperOxidationHandler.previousStage("bit_weathered_copper_block_wall"));
        assertNull(CopperOxidationHandler.nextStage("bit_oxidized_chiseled_copper"));
        assertEquals(-1, CopperOxidationHandler.naturalAge("bit_cut_copper"));
        assertEquals(1, CopperOxidationHandler.naturalAge("exposed_copper_mesh"));
    }

    @Test
    void namesOutsideTheTablesNoLongerInventStageIds() {
        CopperOxidationHandler.initTables(false);
        // 4.0.5 guessed "bit_exposed_<id>" for any unknown block, which named blocks that do not exist.
        assertNull(CopperOxidationHandler.nextStage("bit_exposed_bit_cut_copper"));
        assertNull(CopperOxidationHandler.nextStage("copper_pillar"));
        assertNull(CopperOxidationHandler.nextStage("waxed_copper_mesh"));
        assertNull(CopperOxidationHandler.nextStage("oxidized_copper_mesh"));
    }
}
