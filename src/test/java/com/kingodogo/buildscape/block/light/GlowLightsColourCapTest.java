package com.kingodogo.buildscape.block.light;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GlowLightsColourCapTest extends BootstrappedGameTest {
    @Test
    public void loadKeepsFirstFiveOfEightAndAllThreeOnTheNextLoad() throws Exception {
        Field value = RegistryObject.class.getDeclaredField("value");
        value.setAccessible(true);
        Object previous = value.get(ModBlockEntities.GLOW_LIGHTS_BLOCK_ENTITY);
        try {
            // Only the NBT loader is under test; no mod block registrations are needed.
            value.set(ModBlockEntities.GLOW_LIGHTS_BLOCK_ENTITY, BlockEntityType.BEACON);
            GlowLightsBlockEntity lights = new GlowLightsBlockEntity(BlockPos.ZERO, Blocks.BEACON.defaultBlockState());
            List<String> eight = List.of("#FF0000", "#00FF00", "#0000FF", "#FFFFFF", "#000000",
                    "#FFFF00", "#FF00FF", "#00FFFF");
            lights.load(coloursTag(eight));
            assertEquals(eight.subList(0, 5), lights.getDyeColors());
            assertEquals(5, lights.getDyeColorCount());
            assertFalse(lights.canAddMoreColors());
            CompoundTag saved = new CompoundTag();
            lights.saveAdditional(saved);
            assertEquals(coloursTag(eight.subList(0, 5)).getList(NbtKeys.DYE_COLORS, Tag.TAG_STRING),
                    saved.getList(NbtKeys.DYE_COLORS, Tag.TAG_STRING));

            List<String> three = List.of("#FFFF00", "#FF00FF", "#00FFFF");
            lights.load(coloursTag(three));
            assertEquals(three, lights.getDyeColors());
            assertEquals(3, lights.getDyeColorCount());
            assertTrue(lights.canAddMoreColors());
        } finally {
            value.set(ModBlockEntities.GLOW_LIGHTS_BLOCK_ENTITY, previous);
        }
    }

    private static CompoundTag coloursTag(List<String> colours) {
        ListTag list = new ListTag();
        for (String colour : colours) {
            list.add(StringTag.valueOf(colour));
        }
        CompoundTag tag = new CompoundTag();
        tag.put(NbtKeys.DYE_COLORS, list);
        return tag;
    }
}
