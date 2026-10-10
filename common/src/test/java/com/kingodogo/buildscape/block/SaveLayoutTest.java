package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SaveLayoutTest {
    @BeforeAll
    static void bootstrap() { TestBootstrap.initialize(); }

    @Test
    void pillarPreservesReferenceKeysAndDoublePrecision() {
        var settings = new PillarBlockEntity.SavedSettings(123.5f, "spiral", 1.234567890123,
                2.345678901234, 3.456789012345, false, 7, "mangrove_42",
                List.of("#ABCDEF", "#123456"), 19, true);
        var output = output();
        settings.save(output);
        CompoundTag tag = output.buildResult();
        assertEquals(Set.of("FacingYaw", "ParticlePattern", "PatternSpeed", "PatternSpread", "PatternIntensity",
                "UsePattern", "MaxParticleColor", "PillarId", "ParticleColors", "ParticleColorCounter", "ColorsInitialized"), tag.keySet());
        assertInstanceOf(DoubleTag.class, tag.get("PatternSpeed"));
        assertInstanceOf(ListTag.class, tag.get("ParticleColors"));
        assertEquals(settings, PillarBlockEntity.SavedSettings.read(input(tag)));
    }

    @Test
    void pillarKeepsExplicitInitializationWithoutColorsAndReadsLegacyPattern() {
        CompoundTag old = new CompoundTag();
        old.putString("PATTERN", "ring");
        old.putBoolean("ColorsInitialized", true);
        old.putInt("ParticleColorCounter", 31);
        old.putFloat("FacingYaw", -90);
        var saved = PillarBlockEntity.SavedSettings.read(input(old));
        assertEquals("ring", saved.particlePattern());
        assertEquals(270, saved.facingYaw());
        assertTrue(saved.colorsInitialized());
        assertEquals(31, saved.particleColorCounter());
        assertNull(saved.particleColors());
    }

    @Test
    void workbenchPreservesReferenceSettingsArraysAndVersion() {
        int[] colors = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        int[] gradients = {8, 7, 6, 5, 4, 3, 2, 1, 0};
        var settings = new BuildersWorkbenchBlockEntity.SavedSettings(1, ColorGradientSolver.FILTER_DEFAULT, 27, colors, gradients);
        var output = output();
        settings.save(output);
        CompoundTag tag = output.buildResult();
        assertEquals(Set.of("ActiveTab", "FilterMask", "FilterMaskVersion", "CopyProgress",
                "ColorResultOffsets", "GradientResultOffsets"), tag.keySet());
        assertInstanceOf(IntArrayTag.class, tag.get("ColorResultOffsets"));
        assertInstanceOf(IntArrayTag.class, tag.get("GradientResultOffsets"));
        var loaded = BuildersWorkbenchBlockEntity.SavedSettings.read(input(tag));
        assertEquals(settings.activeTab(), loaded.activeTab());
        assertEquals(settings.filterMask(), loaded.filterMask());
        assertEquals(settings.copyProgress(), loaded.copyProgress());
        assertArrayEquals(colors, loaded.colorOffsets());
        assertArrayEquals(gradients, loaded.gradientOffsets());
    }

    @Test
    void workbenchMigratesOldOffsetsAndIgnoresObsoleteFilterMask() {
        CompoundTag old = new CompoundTag();
        old.putInt("ActiveTab", 1);
        old.putInt("FilterMask", -1);
        old.putIntArray("ResultOffsets", new int[]{4, -2, 6});
        var loaded = BuildersWorkbenchBlockEntity.SavedSettings.read(input(old));
        assertEquals(ColorGradientSolver.FILTER_DEFAULT, loaded.filterMask());
        assertArrayEquals(new int[]{4, 0, 6, 0, 0, 0, 0, 0, 0}, loaded.gradientOffsets());
        assertArrayEquals(new int[9], loaded.colorOffsets());
    }

    @Test
    void workbenchInventoryUsesFlatItemsListAndRetainsSparseSlots() {
        var items = NonNullList.withSize(30, ItemStack.EMPTY);
        items.set(0, new ItemStack(Items.STONE, 17));
        items.set(29, new ItemStack(Items.DIRT, 23));
        var output = output();
        BuildersWorkbenchBlockEntity.saveItems(output, items);
        CompoundTag tag = output.buildResult();
        assertEquals(Set.of("Items"), tag.keySet());
        ListTag list = tag.getList("Items").orElseThrow();
        assertEquals(2, list.size());
        CompoundTag first = list.getCompound(0).orElseThrow();
        assertInstanceOf(ByteTag.class, first.get("Slot"));
        assertEquals("minecraft:stone", first.getString("id").orElseThrow());
        assertFalse(first.contains("Item"));
        var loaded = NonNullList.withSize(30, ItemStack.EMPTY);
        for (int i = 0; i < loaded.size(); i++) loaded.set(i, new ItemStack(Items.DIAMOND));
        BuildersWorkbenchBlockEntity.loadItems(input(tag), loaded);
        assertEquals(17, loaded.get(0).getCount());
        assertEquals(Items.DIRT, loaded.get(29).getItem());
        assertEquals(23, loaded.get(29).getCount());
        assertTrue(loaded.get(10).isEmpty());
    }

    @Test
    void legacyItemCountsAndCustomDataSurviveBothEntityLayouts() {
        CompoundTag legacy = new CompoundTag();
        legacy.putString("id", "minecraft:stone");
        legacy.putByte("Count", (byte) 37);
        CompoundTag custom = new CompoundTag();
        custom.putString("buildscape_test", "retained");
        legacy.put("tag", custom);
        ItemStack pillarItem = PillarBlockEntity.SavedItem.read(input(legacy));
        assertEquals(37, pillarItem.getCount());
        assertEquals("retained", pillarItem.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA)
                .copyTag().getString("buildscape_test").orElseThrow());
        legacy.putByte("Slot", (byte) 29);
        ListTag list = new ListTag();
        list.add(legacy);
        CompoundTag workbench = new CompoundTag();
        workbench.put("Items", list);
        var items = NonNullList.withSize(30, ItemStack.EMPTY);
        BuildersWorkbenchBlockEntity.loadItems(input(workbench), items);
        assertEquals(37, items.get(29).getCount());
        assertEquals(Items.STONE, items.get(29).getItem());
    }

    private static ValueInput input(CompoundTag tag) {
        return TagValueInput.create(ProblemReporter.DISCARDING,
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), tag);
    }

    private static TagValueOutput output() {
        return TagValueOutput.createWithContext(ProblemReporter.DISCARDING,
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    }
}
