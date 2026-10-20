package com.kingodogo.buildscape.feature.pipe;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Set;

import static org.junit.Assert.*;

public class PipeOutletPlacementTest extends BootstrappedGameTest {

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
                assertFalse(trySpread(target, falling ? Direction.DOWN : Direction.EAST, 0));
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
        assertFalse(trySpread(target, Direction.EAST, 0));
        target.state = Blocks.AIR.defaultBlockState();
        assertTrue(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertEquals(Fluids.FLOWING_WATER.getFlowing(7, false).createLegacyBlock(), target.state);
        assertEquals(1, target.writes);
        assertEquals(1, target.fluidTicks);
        assertFalse(trySpread(target, Direction.EAST, 0));
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
        assertFalse(trySpread(target, Direction.EAST, 4));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 8, true));
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 8, true));
        assertFalse(trySpread(target, Direction.DOWN, 0));
        assertEquals(2, target.fluidTicks);
    }

    @Test
    public void exhaustedRangeUnloadedChunksAndFailedWritesDoNotReschedule() {
        Target target = new Target(Blocks.AIR.defaultBlockState());
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 0, false));
        assertFalse(trySpread(target, Direction.EAST, 7));
        target.loaded = false;
        assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertFalse(trySpread(target, Direction.EAST, 0));
        target.loaded = true;
        target.acceptWrites = false;
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertFalse(trySpread(target, Direction.EAST, 0));
        assertEquals(0, target.fluidTicks);
        assertEquals(Blocks.AIR.defaultBlockState(), target.state);
    }

    @Test
    public void outerSpreadReturnsTrueOnlyForAnActiveOutletThatChangesTheWorld() {
        Target target = new Target(Blocks.AIR.defaultBlockState());
        assertFalse(HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO,
                Fluids.WATER, 0, Set.of(Direction.NORTH), dir -> dir.getAxis() == Direction.Axis.X));
        assertFalse(HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO,
                Fluids.WATER, 0, Set.of(), dir -> dir.getAxis() == Direction.Axis.X));
        assertFalse(HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO,
                Fluids.WATER, 0, Set.of(Direction.UP), dir -> dir.getAxis() == Direction.Axis.Y));
        assertTrue(trySpread(target, Direction.EAST, 0));
        assertFalse(trySpread(target, Direction.EAST, 0));
        assertEquals(1, target.writes);
        assertEquals(1, target.fluidTicks);
    }

    private static boolean trySpread(Target target, Direction direction, int distance) {
        return HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO,
                Fluids.WATER, distance, Set.of(direction), dir -> dir.getAxis() == direction.getAxis());
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
                                if (!acceptWrites) {
                                    yield false;
                                }
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
