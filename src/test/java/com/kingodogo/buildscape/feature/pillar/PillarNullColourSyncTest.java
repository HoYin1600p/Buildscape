package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PillarNullColourSyncTest extends BootstrappedGameTest {
    @Test
    public void equalSizedListsCompareNullsAndStillApplyLaterColourChanges() throws Exception {
        Field value = RegistryObject.class.getDeclaredField("value");
        value.setAccessible(true);
        Object previous = value.get(ModBlockEntities.PILLAR_BLOCK_ENTITY);
        try {
            // A vanilla type lets the colour-sync unit run without mod registry events or a level.
            value.set(ModBlockEntities.PILLAR_BLOCK_ENTITY, BlockEntityType.BEACON);
            PillarBlockEntity pillar = new PillarBlockEntity(BlockPos.ZERO, Blocks.BEACON.defaultBlockState());
            pillar.pillarId = "S-P1";
            pillar.particleColors = new ArrayList<>(Arrays.asList(null, "#FFFFFF"));
            pillar.particleColorCounter = 7;
            pillar.lastParticleTick = 123L;
            PillarIdManager.PillarData data = new PillarIdManager.PillarData("S-P1", "minecraft:overworld",
                    BlockPos.ZERO);
            data.dyeColors = new ArrayList<>(Arrays.asList(null, "#000000"));

            PillarEntitySync.syncFromData(pillar, data);

            assertEquals(Arrays.asList(null, "#000000"), pillar.particleColors);
            assertNotSame(data.dyeColors, pillar.particleColors);
            assertTrue(pillar.colorsInitialized);
            assertEquals(1, pillar.colorsVersion);
            assertEquals(0, pillar.particleColorCounter);
            assertEquals(0L, pillar.lastParticleTick);
            List<String> synced = pillar.particleColors;
            PillarEntitySync.syncFromData(pillar, data);
            assertEquals(1, pillar.colorsVersion);
            assertSame(synced, pillar.particleColors);

            data.dyeColors.set(0, "#123456");
            PillarEntitySync.syncFromData(pillar, data);
            assertEquals(List.of("#123456", "#000000"), pillar.particleColors);
            assertEquals(2, pillar.colorsVersion);
            data.dyeColors.set(0, null);
            PillarEntitySync.syncFromData(pillar, data);
            assertEquals(Arrays.asList(null, "#000000"), pillar.particleColors);
            assertEquals(3, pillar.colorsVersion);
        } finally {
            value.set(ModBlockEntities.PILLAR_BLOCK_ENTITY, previous);
        }
    }
}
