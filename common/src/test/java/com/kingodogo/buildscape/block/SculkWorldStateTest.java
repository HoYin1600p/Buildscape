package com.kingodogo.buildscape.block;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Sculk growth, vein placement and cursor saves, with vanilla stand-ins for the restored blocks. */
class SculkWorldStateTest {
    // Distinct vanilla blocks stand in for buildscape:sculk, sculk_catalyst, sculk_vein and sculk_shrieker.
    private static final Block SCULK = Blocks.BLACK_WOOL;
    private static final Block CATALYST = Blocks.OBSIDIAN;
    private static final Block VEIN = Blocks.GLOW_LICHEN;
    private static final Block SHRIEKER = Blocks.BEDROCK;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void oneOfElevenGrowthsIsAShriekerAndUnderwaterGrowthIsAWaterloggedSensor() {
        for (int choice = 0; choice < 11; choice++) {
            assertEquals(choice == 0 ? SHRIEKER : Blocks.SCULK_SENSOR,
                    SculkSpreader.growthState(choice, false, SHRIEKER).getBlock());
            BlockState underwater = SculkSpreader.growthState(choice, true, SHRIEKER);
            assertEquals(Blocks.SCULK_SENSOR, underwater.getBlock());
            assertTrue(underwater.getValue(BlockStateProperties.WATERLOGGED));
        }
        assertFalse(SculkSpreader.growthState(1, false, SHRIEKER).getValue(BlockStateProperties.WATERLOGGED));
    }

    @Test
    void growthSpaceAcceptsOnlyAirOrASourceWaterBlock() {
        assertTrue(SculkSpreader.isGrowthSpace(Blocks.AIR.defaultBlockState()));
        assertTrue(SculkSpreader.isGrowthSpace(Blocks.WATER.defaultBlockState()));
        assertFalse(SculkSpreader.isGrowthSpace(Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 1)));
        assertFalse(SculkSpreader.isGrowthSpace(Blocks.LAVA.defaultBlockState()));
        assertFalse(SculkSpreader.isGrowthSpace(Blocks.STONE.defaultBlockState()));
        assertFalse(SculkSpreader.isGrowthSpace(SculkSpreader.growthState(1, true, SHRIEKER)));
    }

    @Test
    void veinsGrowIntoAirSourceWaterPlantsAndSnowButNotFlowersOrSolidBlocks() {
        assertReplaceable(Blocks.AIR.defaultBlockState());
        assertReplaceable(Blocks.WATER.defaultBlockState());
        assertReplaceable(VEIN.defaultBlockState());
        assertReplaceable(Blocks.GRASS.defaultBlockState());
        assertReplaceable(Blocks.FERN.defaultBlockState());
        assertReplaceable(Blocks.DEAD_BUSH.defaultBlockState());
        assertReplaceable(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 2));
        assertNotReplaceable(Blocks.POPPY.defaultBlockState());
        assertNotReplaceable(Blocks.STONE.defaultBlockState());
    }

    @Test
    void veinsRefuseFlowingWaterLavaFireAndWaterloggedBlocks() {
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
    void veinsNeverAttachToSculkCatalystsOrMovingPistons() {
        BlockState[] supports = {SCULK.defaultBlockState(), CATALYST.defaultBlockState(),
                Blocks.MOVING_PISTON.defaultBlockState()};
        BlockState[] targets = {Blocks.AIR.defaultBlockState(), Blocks.WATER.defaultBlockState(),
                Blocks.GRASS.defaultBlockState(), VEIN.defaultBlockState()};
        for (BlockState support : supports) {
            for (BlockState target : targets) {
                assertFalse(SculkVeinBlock.canReplace(target, support, SCULK, CATALYST, VEIN));
            }
        }
    }

    @Test
    void veinsWrapAroundPartialBlocksUnlessTheFaceIsSturdy() {
        BlockState bottomSlab = Blocks.OAK_SLAB.defaultBlockState();
        BlockState topSlab = bottomSlab.setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
        assertTrue(canWrap(bottomSlab, Direction.UP));
        assertFalse(canWrap(bottomSlab, Direction.DOWN));
        assertFalse(canWrap(topSlab, Direction.UP));
        assertTrue(canWrap(topSlab, Direction.DOWN));
        assertTrue(canWrap(Blocks.OAK_FENCE.defaultBlockState(), Direction.EAST));
        for (Direction face : Direction.values()) {
            assertTrue(canWrap(Blocks.AIR.defaultBlockState(), face));
            assertFalse(canWrap(Blocks.STONE.defaultBlockState(), face));
        }
    }

    @Test
    void cursorsSavedBefore4_1_0LoadWithTheirPositionChargeAndDelays() {
        CompoundTag cursor = legacyCursor();
        CompoundTag saved = saveCursor(loadCursor(cursor));
        assertEquals(cursor, saved);
        assertFalse(saved.contains("facings"));

        ListTag facings = new ListTag();
        facings.add(StringTag.valueOf("down"));
        facings.add(StringTag.valueOf("north"));
        cursor.put("facings", facings);
        assertEquals(cursor, saveCursor(loadCursor(cursor)));
    }

    @Test
    void missingDelaysUseVanillaDefaultsAndChargeIsClamped() {
        CompoundTag cursor = legacyCursor();
        cursor.remove("decay_delay");
        cursor.remove("update_delay");
        CompoundTag saved = saveCursor(loadCursor(cursor));
        assertEquals(1, saved.getInt("decay_delay"));
        assertEquals(0, saved.getInt("update_delay"));
        cursor.putInt("charge", 5000);
        assertEquals(1000, saveCursor(loadCursor(cursor)).getInt("charge"));
        cursor.putInt("charge", 0);
        CompoundTag empty = new CompoundTag();
        wrap(cursor, empty);
        SculkSpreader spreader = new SculkSpreader();
        spreader.load(empty);
        CompoundTag none = new CompoundTag();
        spreader.save(none);
        assertEquals(0, none.getList("cursors", Tag.TAG_COMPOUND).size());
    }

    private static boolean canWrap(BlockState state, Direction face) {
        return SculkVeinBlock.canWrapAround(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, face);
    }

    private static void assertReplaceable(BlockState state) {
        assertTrue(SculkVeinBlock.canReplace(state, Blocks.STONE.defaultBlockState(), SCULK, CATALYST, VEIN),
                state.toString());
    }

    private static void assertNotReplaceable(BlockState state) {
        assertFalse(SculkVeinBlock.canReplace(state, Blocks.STONE.defaultBlockState(), SCULK, CATALYST, VEIN),
                state.toString());
    }

    private static CompoundTag legacyCursor() {
        CompoundTag cursor = new CompoundTag();
        cursor.put("pos", NbtUtils.writeBlockPos(new BlockPos(13, 64, -9)));
        cursor.putInt("charge", 147);
        cursor.putInt("update_delay", 1);
        cursor.putInt("decay_delay", 0);
        return cursor;
    }

    private static void wrap(CompoundTag cursor, CompoundTag tag) {
        ListTag cursors = new ListTag();
        cursors.add(cursor);
        tag.put("cursors", cursors);
    }

    private static SculkSpreader loadCursor(CompoundTag cursor) {
        CompoundTag tag = new CompoundTag();
        wrap(cursor, tag);
        SculkSpreader spreader = new SculkSpreader();
        spreader.load(tag);
        return spreader;
    }

    private static CompoundTag saveCursor(SculkSpreader spreader) {
        CompoundTag tag = new CompoundTag();
        spreader.save(tag);
        ListTag cursors = tag.getList("cursors", Tag.TAG_COMPOUND);
        assertEquals(1, cursors.size());
        return cursors.getCompound(0);
    }
}
