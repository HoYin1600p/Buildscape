package com.kingodogo.buildscape.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;

public class ShapedDurabilityRecipe extends ShapedRecipe {

    public static final Serializer SERIALIZER = new Serializer();
    private final int damageAmount;

    public ShapedDurabilityRecipe(
            ResourceLocation id,
            String group,
            int width,
            int height,
            NonNullList<Ingredient> ingredients,
            ItemStack result,
            int damageAmount
    ) {
        super(id, group, width, height, ingredients, result);
        this.damageAmount = damageAmount;
    }

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
                if (stack.isDamageableItem() && !(stack.getItem() instanceof ArmorItem)) {
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
            implements RecipeSerializer<ShapedDurabilityRecipe> {

        private static Map<Character, Ingredient> deserializeKey(JsonObject json) {
            Map<Character, Ingredient> key = new HashMap<>();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().length() != 1) {
                    throw new JsonParseException("Invalid key entry: '" + entry.getKey() + "' is not a single character");
                }
                char symbol = entry.getKey().charAt(0);
                if (symbol == ' ') {
                    throw new JsonParseException("Invalid key entry: ' ' (space) is reserved and cannot be defined");
                }
                key.put(symbol, Ingredient.fromJson(entry.getValue()));
            }

            key.put(' ', Ingredient.EMPTY);
            return key;
        }

        private static String[] deserializePattern(JsonArray json) {
            String[] pattern = new String[json.size()];
            if (pattern.length == 0) {
                throw new JsonParseException("Invalid pattern: empty pattern not allowed");
            }

            for (int i = 0; i < json.size(); i++) {
                String line = GsonHelper.convertToString(json.get(i), "pattern[" + i + "]");
                pattern[i] = line;
            }
            return pattern;
        }

        private static String[] shrinkPattern(String[] pattern) {
            int minCol = Integer.MAX_VALUE;
            int maxCol = -1;
            int firstRow = 0;
            int lastRow = pattern.length - 1;

            // Find first and last non-empty rows
            while (firstRow < pattern.length && isRowEmpty(pattern[firstRow])) {
                firstRow++;
            }
            while (lastRow >= 0 && isRowEmpty(pattern[lastRow])) {
                lastRow--;
            }

            if (lastRow < firstRow) {
                return new String[]{""};
            }

            // Find bounds for columns
            for (int row = firstRow; row <= lastRow; row++) {
                String line = pattern[row];
                for (int col = 0; col < line.length(); col++) {
                    if (line.charAt(col) != ' ') {
                        minCol = Math.min(minCol, col);
                        maxCol = Math.max(maxCol, col);
                    }
                }
            }

            if (minCol > maxCol) {
                return new String[]{""};
            }

            int newHeight = lastRow - firstRow + 1;
            String[] result = new String[newHeight];
            for (int row = 0; row < newHeight; row++) {
                result[row] = pattern[firstRow + row].substring(minCol, maxCol + 1);
            }

            return result;
        }

        private static boolean isRowEmpty(String row) {
            for (int i = 0; i < row.length(); i++) {
                if (row.charAt(i) != ' ') {
                    return false;
                }
            }
            return true;
        }

        private static NonNullList<Ingredient> dissolvePattern(
                String[] pattern,
                Map<Character, Ingredient> key,
                int width,
                int height
        ) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int row = 0; row < pattern.length; row++) {
                String line = pattern[row];
                for (int col = 0; col < line.length(); col++) {
                    char symbol = line.charAt(col);
                    Ingredient ingredient = key.getOrDefault(symbol, Ingredient.EMPTY);
                    ingredients.set(col + row * width, ingredient);
                }
            }

            return ingredients;
        }

        @Override
        public ShapedDurabilityRecipe fromJson(
                ResourceLocation recipeId,
                JsonObject json
        ) {
            String group = GsonHelper.getAsString(json, "group", "");
            int damageAmount = GsonHelper.getAsInt(json, "damageAmount", 1);

            Map<Character, Ingredient> key = deserializeKey(GsonHelper.getAsJsonObject(json, "key"));
            String[] pattern = shrinkPattern(deserializePattern(GsonHelper.getAsJsonArray(json, "pattern")));

            int width = pattern[0].length();
            int height = pattern.length;

            NonNullList<Ingredient> ingredients = dissolvePattern(pattern, key, width, height);

            ItemStack result = CraftingHelper.getItemStack(
                    GsonHelper.getAsJsonObject(json, "result"),
                    true
            );

            return new ShapedDurabilityRecipe(
                    recipeId,
                    group,
                    width,
                    height,
                    ingredients,
                    result,
                    damageAmount
            );
        }

        @Override
        public ShapedDurabilityRecipe fromNetwork(
                ResourceLocation recipeId,
                FriendlyByteBuf buffer
        ) {
            String group = buffer.readUtf();
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();

            NonNullList<Ingredient> ingredients = NonNullList.withSize(
                    width * height,
                    Ingredient.EMPTY
            );

            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack result = buffer.readItem();
            int damageAmount = buffer.readVarInt();

            return new ShapedDurabilityRecipe(
                    recipeId,
                    group,
                    width,
                    height,
                    ingredients,
                    result,
                    damageAmount
            );
        }

        @Override
        public void toNetwork(
                FriendlyByteBuf buffer,
                ShapedDurabilityRecipe recipe
        ) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getWidth());
            buffer.writeVarInt(recipe.getHeight());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
            buffer.writeVarInt(recipe.damageAmount);
        }
    }
}

