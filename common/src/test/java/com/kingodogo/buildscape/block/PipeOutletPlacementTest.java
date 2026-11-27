package com.kingodogo.buildscape.block;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pipe outlets spill flowing fluid only where it changes something, and report whether it did. */
class PipeOutletPlacementTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void sourcesWaterloggableBlocksAndSolidsAreBlockedWithoutRescheduling() {
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
    void clearingABlockedEndResumesPlacementThenStopsOnceNothingChanges() {
        Target target = new Target(Blocks.STONE.defaultBlockState());
        assertFalse(trySpread(target, Direction.EAST, 0));
        target.state = Blocks.AIR.defaultBlockState();
        assertTrue(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertEquals(Fluids.FLOWING_WATER.getFlowing(7, false).createLegacyBlock(), target.state);
        assertFalse(trySpread(target, Direction.EAST, 0));
        assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertEquals(1, target.writes);
        assertEquals(1, target.fluidTicks);
    }

    @Test
    void exactAmountAndFallingStateDecideWhetherPlacementChangesTheTarget() {
        Target target = new Target(Fluids.FLOWING_WATER.getFlowing(3, false).createLegacyBlock());
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 3, false));
        assertFalse(trySpread(target, Direction.EAST, 4));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertTrue(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 8, true));
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 8, true));
        assertFalse(trySpread(target, Direction.DOWN, 0));
        assertEquals(2, target.fluidTicks);
    }

    @Test
    void exhaustedRangeUnloadedChunksAndRefusedWritesDoNotReschedule() {
        Target target = new Target(Blocks.AIR.defaultBlockState());
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 0, false));
        assertFalse(trySpread(target, Direction.EAST, 7));
        target.loaded = false;
        assertFalse(HollowPipeBlock.canSpillInto(target.level, BlockPos.ZERO, Fluids.WATER));
        assertFalse(trySpread(target, Direction.EAST, 0));
        target.loaded = true;
        target.acceptWrites = false;
        assertFalse(HollowPipeBlock.spreadToWorldBlock(target.level, BlockPos.ZERO, Fluids.WATER, 7, false));
        assertFalse(trySpread(target, Direction.EAST, 0));
        assertEquals(0, target.fluidTicks);
        assertEquals(Blocks.AIR.defaultBlockState(), target.state);
    }

    @Test
    void onlyAnActiveOpenOutletThatChangesTheWorldCounts() {
        Target target = new Target(Blocks.AIR.defaultBlockState());
        assertFalse(HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO, Fluids.WATER, 0,
                Set.of(Direction.NORTH), dir -> dir.getAxis() == Direction.Axis.X));
        assertFalse(HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO, Fluids.WATER, 0,
                Set.of(), dir -> dir.getAxis() == Direction.Axis.X));
        assertFalse(HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO, Fluids.WATER, 0,
                Set.of(Direction.UP), dir -> dir.getAxis() == Direction.Axis.Y));
        assertFalse(HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO, Fluids.EMPTY, 0,
                Set.of(Direction.EAST), dir -> true));
        assertTrue(trySpread(target, Direction.EAST, 0));
        assertFalse(trySpread(target, Direction.EAST, 0));
        assertEquals(1, target.writes);
        assertEquals(1, target.fluidTicks);
    }

    private static boolean trySpread(Target target, Direction direction, int distance) {
        return HollowPipeBlock.trySpreadToWorld(target.level, BlockPos.ZERO, Fluids.WATER, distance,
                Set.of(direction), dir -> dir.getAxis() == direction.getAxis());
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
                    new Class<?>[]{LevelAccessor.class}, (proxy, method, args) -> switch (method.getName()) {
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
                    });
        }
    }
}
