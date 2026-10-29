package com.kingodogo.buildscape.adapter.v121x;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HazeBushComponentsTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void convertedDropComponentsSurviveStackSerializationAndStateTakesPrecedence() {
        var stack = new ItemStack(Items.STICK);
        assertFalse(HazeBushItem.hasDrainedComponents(stack));
        stack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("has_haze", "false")));
        var data = new CompoundTag();
        data.putBoolean("HasHaze", false);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        var registries = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        var restored = ItemStack.parseOptional(registries, (CompoundTag) stack.save(registries));
        assertTrue(HazeBushItem.hasDrainedComponents(restored));
        restored.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("has_haze", "true")));
        assertFalse(HazeBushItem.hasDrainedComponents(restored));
        restored.remove(DataComponents.BLOCK_STATE);
        assertTrue(HazeBushItem.hasDrainedComponents(restored));
    }

    @Test
    void customDataFallbackAndCloneWriterPreserveUnrelatedProperties() {
        var stack = new ItemStack(Items.STICK);
        var data = new CompoundTag();
        data.putBoolean("HasHaze", true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        assertFalse(HazeBushItem.hasDrainedComponents(stack));
        var legacy = new CompoundTag();
        legacy.putString("has_haze", "false");
        legacy.putString("other", "retained");
        data.put("BlockStateTag", legacy);
        data.putInt("Unrelated", 7);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        assertTrue(HazeBushItem.hasDrainedComponents(stack));
        stack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("other", "retained")));
        HazeBushItem.writeDrainedComponents(stack);
        assertTrue(HazeBushItem.hasDrainedComponents(stack));
        assertEquals(Map.of("has_haze", "false", "other", "retained"), stack.get(DataComponents.BLOCK_STATE).properties());
        var written = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        assertFalse(written.getBoolean("HasHaze"));
        assertEquals(7, written.getInt("Unrelated"));
        assertEquals("retained", written.getCompound("BlockStateTag").getString("other"));
        assertFalse(written.getCompound("BlockStateTag").contains("has_haze"));
    }
}
