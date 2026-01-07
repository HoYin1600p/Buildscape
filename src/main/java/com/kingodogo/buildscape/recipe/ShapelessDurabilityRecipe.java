package com.kingodogo.buildscape.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShapelessDurabilityRecipe extends ShapelessRecipe {

    private final int damageAmount;

    public ShapelessDurabilityRecipe(
            ResourceLocation id,
            String group,
            ItemStack result,
            NonNullList<Ingredient> ingredients,
            int damageAmount
    ) {
        super(id, group, result, ingredients);
        this.damageAmount = damageAmount;
    }

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(
                container.getContainerSize(),
                ItemStack.EMPTY
        );

        for (int i = 0; i < remaining.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (
                        stack.isDamageableItem() && !(stack.getItem() instanceof ArmorItem)
                ) {
                    ItemStack damagedStack = stack.copy();
                    int currentDamage = damagedStack.getDamageValue();
                    int newDamage = currentDamage + damageAmount;
                    damagedStack.setDamageValue(newDamage);

                    if (newDamage < damagedStack.getMaxDamage()) {
                        remaining.set(i, damagedStack);
                    }
                } else if (stack.hasContainerItem()) {
                    remaining.set(i, stack.getContainerItem());
                }
            }
        }

        return remaining;
    }

    public static class Serializer
            extends ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<ShapelessDurabilityRecipe> {

        @Override
        public ShapelessDurabilityRecipe fromJson(
                ResourceLocation recipeId,
                JsonObject json
        ) {
            String group = GsonHelper.getAsString(json, "group", "");
            int damageAmount = GsonHelper.getAsInt(json, "damageAmount", 1);

            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(
                    json,
                    "ingredients"
            );
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for (int i = 0; i < ingredientsJson.size(); i++) {
                Ingredient ingredient = Ingredient.fromJson(ingredientsJson.get(i));
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            }

            if (ingredients.isEmpty()) {
                throw new JsonParseException(
                        "No ingredients for shapeless durability recipe"
                );
            }

            ItemStack result = CraftingHelper.getItemStack(
                    GsonHelper.getAsJsonObject(json, "result"),
                    true
            );

            return new ShapelessDurabilityRecipe(
                    recipeId,
                    group,
                    result,
                    ingredients,
                    damageAmount
            );
        }

        @Override
        public ShapelessDurabilityRecipe fromNetwork(
                ResourceLocation recipeId,
                FriendlyByteBuf buffer
        ) {
            String group = buffer.readUtf();
            int damageAmount = buffer.readInt();
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(
                    ingredientCount,
                    Ingredient.EMPTY
            );

            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack result = buffer.readItem();

            return new ShapelessDurabilityRecipe(
                    recipeId,
                    group,
                    result,
                    ingredients,
                    damageAmount
            );
        }

        @Override
        public void toNetwork(
                FriendlyByteBuf buffer,
                ShapelessDurabilityRecipe recipe
        ) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeInt(recipe.damageAmount);
            buffer.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
