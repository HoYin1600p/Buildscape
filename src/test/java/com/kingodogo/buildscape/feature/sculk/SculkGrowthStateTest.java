package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SculkGrowthStateTest extends BootstrappedGameTest {
    private static final Set<String> GROWTH_BLOCKS = Set.of("sculk_sensor", "sculk_shrieker");
    private static Map<String, Block> blocks;

    @BeforeClass
    public static void bindBlocks() {
        blocks = RegistryTestSupport.bind(BlocksRegister.blocks(), GROWTH_BLOCKS::contains);
    }

    @AfterClass
    public static void unbindBlocks() {
        RegistryTestSupport.unbind(BlocksRegister.blocks(), GROWTH_BLOCKS::contains);
    }

    @Test
    public void oneOfElevenChoicesIsAShriekerAndTheRestAreFunctionalSensors() {
        for (int choice = 0; choice < 11; choice++) {
            assertEquals(choice == 0 ? blocks.get("sculk_shrieker") : Blocks.SCULK_SENSOR,
                    SculkSpreader.growthState(choice, false).getBlock());
        }
    }

    @Test
    public void waterlogsOnlyGrowthWithAWaterloggedProperty() {
        BlockState drySensor = SculkSpreader.growthState(1, false);
        BlockState wetSensor = SculkSpreader.growthState(1, true);
        assertFalse(drySensor.getValue(BlockStateProperties.WATERLOGGED));
        assertTrue(wetSensor.getValue(BlockStateProperties.WATERLOGGED));
        BlockState shrieker = SculkSpreader.growthState(0, true);
        assertFalse(shrieker.hasProperty(BlockStateProperties.WATERLOGGED));
        assertEquals(SculkSpreader.growthState(0, false), shrieker);
    }

    @Test
    public void growthSpaceAcceptsOnlyAirOrASourceWaterBlock() {
        assertTrue(SculkSpreader.isGrowthSpace(Blocks.AIR.defaultBlockState()));
        assertTrue(SculkSpreader.isGrowthSpace(Blocks.WATER.defaultBlockState()));
        assertFalse(SculkSpreader.isGrowthSpace(Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 1)));
        assertFalse(SculkSpreader.isGrowthSpace(Blocks.LAVA.defaultBlockState()));
        assertFalse(SculkSpreader.isGrowthSpace(Blocks.STONE.defaultBlockState()));
        assertFalse(SculkSpreader.isGrowthSpace(SculkSpreader.growthState(1, true)));
    }

    @Test
    public void growthLimitCountsBothSensorsAndTheBackportedShrieker() {
        assertTrue(SculkSpreader.isGrowth(Blocks.SCULK_SENSOR.defaultBlockState()));
        assertTrue(SculkSpreader.isGrowth(blocks.get("sculk_sensor").defaultBlockState()));
        assertTrue(SculkSpreader.isGrowth(blocks.get("sculk_shrieker").defaultBlockState()));
        assertFalse(SculkSpreader.isGrowth(Blocks.STONE.defaultBlockState()));
    }

    @Test
    public void legacyCursorNbtKeepsPositionChargeAndDelays() {
        CompoundTag cursor = legacyCursor();
        SculkSpreader spreader = loadCursor(cursor);
        CompoundTag saved = saveCursor(spreader);
        assertEquals(cursor, saved);
        assertFalse(saved.contains("facings"));
    }

    @Test
    public void cursorFacingsRoundTripWithoutRequiringThemInOldSaves() {
        CompoundTag cursor = legacyCursor();
        ListTag facings = new ListTag();
        facings.add(StringTag.valueOf("down"));
        facings.add(StringTag.valueOf("north"));
        cursor.put("facings", facings);
        assertEquals(cursor, saveCursor(loadCursor(cursor)));
    }

    @Test
    public void missingDelaysUseVanillaDefaultsAndLongUpdateDelaysSurvive() {
        CompoundTag cursor = legacyCursor();
        cursor.remove("decay_delay");
        cursor.remove("update_delay");
        CompoundTag saved = saveCursor(loadCursor(cursor));
        assertEquals(1, saved.getInt("decay_delay"));
        assertEquals(0, saved.getInt("update_delay"));
        cursor.putInt("update_delay", 12);
        assertEquals(12, saveCursor(loadCursor(cursor)).getInt("update_delay"));
    }

    private static CompoundTag legacyCursor() {
        CompoundTag cursor = new CompoundTag();
        cursor.put("pos", NbtUtils.writeBlockPos(new BlockPos(13, 64, -9)));
        cursor.putInt("charge", 147);
        cursor.putInt("update_delay", 1);
        cursor.putInt("decay_delay", 0);
        return cursor;
    }

    private static SculkSpreader loadCursor(CompoundTag cursor) {
        CompoundTag tag = new CompoundTag();
        ListTag cursors = new ListTag();
        cursors.add(cursor);
        tag.put("cursors", cursors);
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
