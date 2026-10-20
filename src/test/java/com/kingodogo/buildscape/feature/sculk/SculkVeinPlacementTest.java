package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SculkVeinPlacementTest extends BootstrappedGameTest {
    private static final Set<String> SCULK_BLOCKS = Set.of("sculk", "sculk_vein", "sculk_catalyst");
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
    public void acceptsAirSourceWaterAndExistingDryOrWaterloggedVeins() {
        assertReplaceable(Blocks.AIR.defaultBlockState());
        assertReplaceable(Blocks.WATER.defaultBlockState());
        BlockState vein = blocks.get("sculk_vein").defaultBlockState();
        assertReplaceable(vein);
        assertReplaceable(vein.setValue(BlockStateProperties.WATERLOGGED, true));
    }

    @Test
    public void acceptsReplaceablePlantsAndSnowButNotFlowersOrSolidBlocks() {
        assertReplaceable(Blocks.GRASS.defaultBlockState());
        assertReplaceable(Blocks.FERN.defaultBlockState());
        assertReplaceable(Blocks.DEAD_BUSH.defaultBlockState());
        assertReplaceable(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1));
        assertReplaceable(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 2));
        assertNotReplaceable(Blocks.POPPY.defaultBlockState());
        assertNotReplaceable(Blocks.STONE.defaultBlockState());
    }

    @Test
    public void refusesFlowingWaterLavaAndFire() {
        for (int level = 1; level <= 15; level++) {
            assertNotReplaceable(Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, level));
        }
        for (int level = 0; level <= 15; level++) {
            assertNotReplaceable(Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, level));
        }
        assertNotReplaceable(Blocks.FIRE.defaultBlockState());
        assertNotReplaceable(Blocks.SOUL_FIRE.defaultBlockState());
        assertNotReplaceable(Blocks.OAK_SLAB.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true));
    }

    @Test
    public void refusesTargetsAttachedToSculkCatalystsOrMovingPistons() {
        BlockState[] supports = {blocks.get("sculk").defaultBlockState(),
                blocks.get("sculk_catalyst").defaultBlockState(), Blocks.MOVING_PISTON.defaultBlockState()};
        BlockState[] targets = {Blocks.AIR.defaultBlockState(), Blocks.WATER.defaultBlockState(),
                Blocks.GRASS.defaultBlockState(), blocks.get("sculk_vein").defaultBlockState()};
        for (BlockState support : supports) {
            for (BlockState target : targets) {
                assertFalse(SculkVeinBlock.canReplace(target, support));
            }
        }
    }

    @Test
    public void wrapsAroundPartialCollisionUnlessTheFaceTowardTheTargetIsSturdy() {
        BlockState bottomSlab = Blocks.OAK_SLAB.defaultBlockState();
        BlockState topSlab = bottomSlab.setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
        assertFalse(bottomSlab.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty());
        assertTrue(canWrap(bottomSlab, Direction.UP));
        assertFalse(canWrap(bottomSlab, Direction.DOWN));
        assertFalse(canWrap(topSlab, Direction.UP));
        assertTrue(canWrap(topSlab, Direction.DOWN));
        assertTrue(canWrap(bottomSlab, Direction.EAST));
        assertTrue(canWrap(Blocks.OAK_FENCE.defaultBlockState(), Direction.EAST));
        for (Direction face : Direction.values()) {
            assertTrue(canWrap(Blocks.AIR.defaultBlockState(), face));
            assertFalse(canWrap(Blocks.STONE.defaultBlockState(), face));
        }
    }

    private static boolean canWrap(BlockState state, Direction face) {
        return SculkVeinBlock.canWrapAround(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, face);
    }

    private static void assertReplaceable(BlockState state) {
        assertTrue(SculkVeinBlock.canReplace(state, Blocks.STONE.defaultBlockState()));
    }

    private static void assertNotReplaceable(BlockState state) {
        assertFalse(SculkVeinBlock.canReplace(state, Blocks.STONE.defaultBlockState()));
    }
}
