package com.kingodogo.buildscape.recipe;

import com.google.gson.JsonObject;
import com.kingodogo.buildscape.core.NbtKeys;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

/** A shapeless dye recipe whose first ingredient supplies the bush's haze state. */
public class HazeBushDyeRecipe extends ShapelessRecipe {
    public static final Serializer SERIALIZER = new Serializer();

    public HazeBushDyeRecipe(ResourceLocation id, String group, ItemStack result,
                             NonNullList<Ingredient> ingredients) {
        super(id, group, result, ingredients);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack result = super.assemble(container);
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack input = container.getItem(slot);
            if (getIngredients().get(0).test(input)) {
                CompoundTag tag = input.getTag();
                if (tag != null) {
                    if (tag.contains(NbtKeys.BLOCK_STATE_TAG, Tag.TAG_COMPOUND)) {
                        result.addTagElement("BlockStateTag", tag.getCompound(NbtKeys.BLOCK_STATE_TAG).copy());
                    }
                    if (tag.contains(NbtKeys.HAS_HAZE)) {
                        result.addTagElement("HasHaze", tag.get(NbtKeys.HAS_HAZE).copy());
                    }
                }
                break;
            }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<HazeBushDyeRecipe> {
        private final ShapelessRecipe.Serializer delegate = new ShapelessRecipe.Serializer();

        private HazeBushDyeRecipe wrap(ShapelessRecipe recipe) {
            return new HazeBushDyeRecipe(recipe.getId(), recipe.getGroup(),
                    recipe.getResultItem(), recipe.getIngredients());
        }

        @Override
        public HazeBushDyeRecipe fromJson(ResourceLocation id, JsonObject json) {
            return wrap(delegate.fromJson(id, json));
        }

        @Override
        public HazeBushDyeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return wrap(delegate.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HazeBushDyeRecipe recipe) {
            delegate.toNetwork(buffer, recipe);
        }
    }
}
