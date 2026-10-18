package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

public class PipeOutletPlacementTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void sourcesDryWaterloggableBlocksAndSolidsAreBlockedWithoutRescheduling() {
        for (BlockState state : new BlockState[]{Blocks.WATER.defaultBlockState(),
                Blocks.OAK_SLAB.defaultBlockState(), Blocks.OAK_FENCE.defaultBlockState(),
                Blocks.OAK_SLAB.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true),
                Blocks.STONE.defaultBlockState()}) {
            Target target = new Target(state);
            assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
            for (boolean falling : new boolean[]{false, true}) {
                assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER,
                        falling ? 8 : 7, falling));
                assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER,
                        falling ? 8 : 7, falling));
            }
            assertEquals(state, target.state);
            assertEquals(0, target.writes);
            assertEquals(0, target.fluidTicks);
        }
    }

    @Test
    public void removingABlockingTargetResumesPlacementThenStopsWhenNothingChanges() {
        Target target = new Target(Blocks.STONE.defaultBlockState());
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        target.state = Blocks.AIR.defaultBlockState();
        assertTrue(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertEquals(Fluids.FLOWING_WATER.getFlowing(7, false).createLegacyBlock(), target.state);
        assertEquals(1, target.writes);
        assertEquals(1, target.fluidTicks);
        assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertEquals(1, target.writes);
        assertEquals(1, target.fluidTicks);
    }

    @Test
    public void exactAmountAndFallingStateDetermineWhetherPlacementChangesTheTarget() {
        Target target = new Target(Fluids.FLOWING_WATER.getFlowing(3, false).createLegacyBlock());
        assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER, 3, false));
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 3, false));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 8, true));
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 8, true));
        assertEquals(2, target.fluidTicks);
    }

    @Test
    public void exhaustedRangeUnloadedChunksAndFailedWritesDoNotReschedule() {
        Target target = new Target(Blocks.AIR.defaultBlockState());
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 0, false));
        target.loaded = false;
        assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        target.loaded = true;
        target.acceptWrites = false;
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertEquals(0, target.fluidTicks);
        assertEquals(Blocks.AIR.defaultBlockState(), target.state);
    }

    private static final class Target {
        private BlockState state;
        private boolean loaded = true;
        private boolean acceptWrites = true;
        private int writes;
        private int fluidTicks;
        private final LevelAccessor level;

        private Target(BlockState state) {
            this.state = state;
            level = (LevelAccessor) Proxy.newProxyInstance(LevelAccessor.class.getClassLoader(),
                    new Class<?>[]{LevelAccessor.class}, (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "getBlockState" -> this.state;
                            case "hasChunkAt" -> loaded;
                            case "setBlock" -> {
                                if (!acceptWrites) yield false;
                                this.state = (BlockState) args[1];
                                writes++;
                                yield true;
                            }
                            case "scheduleTick" -> {
                                fluidTicks++;
                                yield null;
                            }
                            default -> throw new AssertionError("Unexpected level access: " + method.getName());
                        };
                    });
        }
    }
}
