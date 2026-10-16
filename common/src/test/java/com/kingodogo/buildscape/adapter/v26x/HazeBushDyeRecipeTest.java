package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.TestBootstrap;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HazeBushDyeRecipeTest {
    @BeforeAll
    static void bootstrap() { TestBootstrap.initialize(); }

    private RecipeFactory.HazeBushDyeRecipe recipe() throws Exception {
        // Construct the native recipe without initializing loader-owned registration services.
        var constructor = RecipeFactory.HazeBushDyeRecipe.class.getDeclaredConstructor(
                Recipe.CommonInfo.class, CraftingRecipe.CraftingBookInfo.class, ItemStackTemplate.class, List.class);
        constructor.setAccessible(true);
        return constructor.newInstance(new Recipe.CommonInfo(true),
                new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
                new ItemStackTemplate(Items.ORANGE_TULIP),
                List.of(Ingredient.of(Items.WHITE_TULIP), Ingredient.of(Items.ORANGE_DYE)));
    }

    private CraftingInput grid(ItemStack bush) {
        return CraftingInput.of(2, 1, List.of(new ItemStack(Items.ORANGE_DYE), bush));
    }

    @Test
    void blockStateAndLegacyHazeSurviveWithoutCopyingUnrelatedData() throws Exception {
        var recipe = recipe();
        var bush = new ItemStack(Items.WHITE_TULIP);
        bush.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("has_haze", "false")));
        var legacy = new CompoundTag();
        legacy.putString("has_haze", "false");
        var data = new CompoundTag();
        data.put("BlockStateTag", legacy);
        data.putBoolean("HasHaze", false);
        data.putString("Unrelated", "leave behind");
        bush.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        var result = recipe.assemble(grid(bush));
        assertTrue(result.is(Items.ORANGE_TULIP));
        assertEquals("false", result.get(DataComponents.BLOCK_STATE).properties().get("has_haze"));
        var copied = result.get(DataComponents.CUSTOM_DATA).copyTag();
        assertEquals("false", copied.getCompound("BlockStateTag").orElseThrow().getString("has_haze").orElseThrow());
        assertFalse(copied.getBoolean("HasHaze").orElseThrow());
        assertFalse(copied.contains("Unrelated"));
        copied.putBoolean("HasHaze", true);
        assertFalse(bush.get(DataComponents.CUSTOM_DATA).copyTag().getBoolean("HasHaze").orElseThrow());
        var full = recipe.assemble(grid(new ItemStack(Items.WHITE_TULIP)));
        assertNull(full.get(DataComponents.BLOCK_STATE));
        assertNull(full.get(DataComponents.CUSTOM_DATA));
        bush.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("has_haze", "true")));
        assertEquals("true", recipe.assemble(grid(bush)).get(DataComponents.BLOCK_STATE).properties().get("has_haze"));
    }

    @Test
    void eachLegacyFallbackSurvivesIndependently() throws Exception {
        for (boolean blockStateTag : List.of(false, true)) {
            var bush = new ItemStack(Items.WHITE_TULIP);
            var data = new CompoundTag();
            if (blockStateTag) {
                var state = new CompoundTag();
                state.putString("has_haze", "false");
                data.put("BlockStateTag", state);
            } else data.putBoolean("HasHaze", false);
            bush.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
            assertEquals(data, recipe().assemble(grid(bush)).get(DataComponents.CUSTOM_DATA).copyTag());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void networkRoundTripRetainsStatePreservingAssembly() throws Exception {
        var field = RecipeFactory.HazeBushDyeRecipe.class.getDeclaredField("STREAM_CODEC");
        field.setAccessible(true);
        var codec = (StreamCodec<RegistryFriendlyByteBuf, RecipeFactory.HazeBushDyeRecipe>) field.get(null);
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(),
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        try {
            codec.encode(buffer, recipe());
            var decoded = codec.decode(buffer);
            var bush = new ItemStack(Items.WHITE_TULIP);
            bush.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("has_haze", "false")));
            var result = decoded.assemble(grid(bush));
            assertTrue(result.is(Items.ORANGE_TULIP));
            assertEquals("false", result.get(DataComponents.BLOCK_STATE).properties().get("has_haze"));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }
}