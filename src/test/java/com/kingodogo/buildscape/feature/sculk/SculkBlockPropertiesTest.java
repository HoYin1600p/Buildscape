package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class SculkBlockPropertiesTest extends BootstrappedGameTest {
    private static final Set<String> FAMILY = Set.of("sculk", "sculk_slab", "sculk_stairs", "sculk_wall",
            "sculk_vertical_slab");
    private static final Set<String> SCULK_BLOCKS = Set.of("sculk", "sculk_slab", "sculk_stairs", "sculk_wall",
            "sculk_vertical_slab", "sculk_vein", "sculk_catalyst", "sculk_shrieker", "sculk_sensor");
    private static Map<String, Block> blocks;

    @BeforeClass
    public static void bindBlocks() {
        blocks = RegistryTestSupport.bind(BlocksRegister.blocks(), SCULK_BLOCKS::contains);
    }

    @AfterClass
    public static void unbindBlocks() {
        RegistryTestSupport.unbind(BlocksRegister.blocks(), SCULK_BLOCKS::contains);
    }

    @Test
    public void sculkBuildingBlocksUseVanillaStrengthLightAndMapColor() {
        for (String path : FAMILY) {
            assertProperties(path, 0.2F, 0.2F, 0);
            for (BlockState state : blocks.get(path).getStateDefinition().getPossibleStates()) {
                assertEquals(path, MaterialColor.COLOR_BLACK,
                        state.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
            }
        }
    }

    @Test
    public void catalystShriekerVeinAndSensorUseVanillaStrengthAndLight() {
        assertProperties("sculk_catalyst", 3.0F, 3.0F, 6);
        assertProperties("sculk_shrieker", 3.0F, 3.0F, 0);
        assertProperties("sculk_vein", 0.2F, 0.2F, 0);
        assertProperties("sculk_sensor", 1.5F, 1.5F, 1);
    }

    private static void assertProperties(String path, float destroyTime, float resistance, int light) {
        Block block = blocks.get(path);
        assertEquals(path, destroyTime, block.defaultDestroyTime(), 0.0F);
        assertEquals(path, resistance, block.getExplosionResistance(), 0.0F);
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            assertEquals(path, destroyTime, state.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), 0.0F);
            assertEquals(path, light, state.getLightEmission());
        }
    }
}
