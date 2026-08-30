package com.kingodogo.buildscape.recipe.framework.cache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingodogo.buildscape.BuildScape;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * High-performance binary recipe cache (.bscb).
 * Serializes and deserializes compiled recipe graphs into compact binary
 * streams with string dictionary pool interning.
 * Supports loading bundled binary cache directly from JAR resources for instant
 * 1st boot (< 20ms).
 */
public class BinaryRecipeCache {

    private static final int MAGIC_HEADER = 0x4B59524F;
    private static final int CACHE_VERSION = 4;

    private static Item getItemFromRegistry(ResourceLocation rl) {
        if (rl == null) return Items.AIR;
        try {
            if (ForgeRegistries.ITEMS != null) {
                Item item = ForgeRegistries.ITEMS.getValue(rl);
                if (item != null) return item;
            }
        } catch (Throwable ignored) {}
        return Items.AIR;
    }

    private static ResourceLocation getItemKeyFromRegistry(Item item) {
        if (item == null) return new ResourceLocation("minecraft", "air");
        try {
            if (ForgeRegistries.ITEMS != null) {
                ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
                if (key != null) return key;
            }
        } catch (Throwable ignored) {}
        return new ResourceLocation("minecraft", "air");
    }

    public static Path getCacheDir() {
        Path dir = FMLPaths.GAMEDIR.get().resolve("buildscape").resolve("cache");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            BuildScape.LOGGER.error("Failed to create cache directory: {}", dir, e);
        }
        return dir;
    }

    public static String computeHash(byte[] combinedContent) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combinedContent);
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return String.valueOf(Arrays.hashCode(combinedContent));
        }
    }

    public static boolean isCacheValid(String currentHash) {
        Path hashFile = getCacheDir().resolve("recipes.bscb.hash");
        Path cacheFile = getCacheDir().resolve("recipes.bscb");
        if (!Files.exists(hashFile) || !Files.exists(cacheFile)) {
            return false;
        }
        try {
            String cachedHash = Files.readString(hashFile, StandardCharsets.UTF_8).trim();
            return cachedHash.equals(currentHash);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isCacheable(Recipe<?> recipe) {
        if (recipe == null) return false;
        return recipe instanceof ShapedRecipe
                || recipe instanceof ShapelessRecipe
                || recipe instanceof StonecutterRecipe
                || recipe instanceof SmeltingRecipe
                || recipe instanceof BlastingRecipe
                || recipe instanceof SmokingRecipe
                || recipe instanceof CampfireCookingRecipe
                || recipe instanceof UpgradeRecipe
                || recipe instanceof com.kingodogo.buildscape.recipe.ShapedDurabilityRecipe
                || recipe instanceof com.kingodogo.buildscape.recipe.ShapelessDurabilityRecipe
                || recipe instanceof com.kingodogo.buildscape.recipe.ConfettiConfigureRecipe
                || recipe instanceof com.kingodogo.buildscape.recipe.ClearShulkerFiltersRecipe;
    }

    public static byte[] serializeCache(String hash, List<Recipe<?>> recipes) throws IOException {
        try (ByteArrayOutputStream recipeByteStream = new ByteArrayOutputStream();
                DataOutputStream recipeDataOut = new DataOutputStream(recipeByteStream)) {

            List<String> stringPool = new ArrayList<>();
            Map<String, Integer> stringIndexMap = new HashMap<>();

            List<Recipe<?>> cacheable = recipes != null ? recipes.stream().filter(BinaryRecipeCache::isCacheable).toList() : List.of();

            // Write Recipes and build string pool dynamically
            recipeDataOut.writeInt(cacheable.size());
            for (Recipe<?> recipe : cacheable) {
                writeRecipe(recipeDataOut, recipe, stringPool, stringIndexMap);
            }

            try (ByteArrayOutputStream finalOut = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(finalOut)) {
                out.writeInt(MAGIC_HEADER);
                out.writeInt(CACHE_VERSION);
                out.writeUTF(hash != null ? hash : "");

                out.writeInt(stringPool.size());
                for (String s : stringPool) {
                    out.writeUTF(s);
                }

                out.write(recipeByteStream.toByteArray());
                return finalOut.toByteArray();
            }
        }
    }

    public static void saveCacheToFile(Path targetFile, String hash, List<Recipe<?>> recipes) throws IOException {
        byte[] fullCacheData = serializeCache(hash, recipes);
        if (targetFile.getParent() != null) {
            Files.createDirectories(targetFile.getParent());
        }
        Files.write(targetFile, fullCacheData);
    }

    public static void saveCache(String hash, List<Recipe<?>> recipes) {
        try {
            byte[] fullCacheData = serializeCache(hash, recipes);

            try {
                Path cacheFile = getCacheDir().resolve("recipes.bscb");
                Path hashFile = getCacheDir().resolve("recipes.bscb.hash");
                Files.write(cacheFile, fullCacheData);
                Files.writeString(hashFile, hash, StandardCharsets.UTF_8);
            } catch (Throwable ignored) {}

            try {
                Path packDir = findWorkspaceRecipesPackDir();
                if (packDir != null) {
                    Path srcResourcePath = packDir.resolve("recipes.bscb");
                    Files.write(srcResourcePath, fullCacheData);
                    BuildScape.LOGGER.info("BDRE Bundled JAR Cache synced to {}", srcResourcePath.toAbsolutePath());
                }
            } catch (Exception e) {
                BuildScape.LOGGER.warn("BDRE Bundled JAR Cache sync warning: {}", e.getMessage());
            }

            BuildScape.LOGGER.info("BDRE Binary Cache saved successfully ({} recipes).", recipes.size());
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to write BDRE Binary Cache", e);
        }
    }

    public static List<Recipe<?>> loadCache(String expectedHash) {
        Path cacheFile = getCacheDir().resolve("recipes.bscb");
        if (!Files.exists(cacheFile)) {
            return new ArrayList<>();
        }
        try {
            return loadCacheFromStream(Files.newInputStream(cacheFile), expectedHash);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static List<Recipe<?>> loadCacheFromStream(InputStream rawStream, String expectedHash) {
        List<Recipe<?>> recipes = new ArrayList<>();
        if (rawStream == null) {
            return recipes;
        }

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(rawStream, 65536))) {
            int header = in.readInt();
            int version = in.readInt();
            if (header != MAGIC_HEADER || version != CACHE_VERSION) {
                BuildScape.LOGGER.warn("BDRE Binary Cache stream version mismatch.");
                return recipes;
            }

            String cachedHash = in.readUTF();
            if (expectedHash != null && !expectedHash.isBlank() && !expectedHash.equals(cachedHash)) {
                BuildScape.LOGGER.warn("BDRE Binary Cache content hash mismatch; recompiling from source recipes.");
                return recipes;
            }

            // Read String Pool
            int poolSize = in.readInt();
            String[] stringPool = new String[poolSize];
            for (int i = 0; i < poolSize; i++) {
                stringPool[i] = in.readUTF();
            }

            // High-Performance Caching Pools for O(1) constant-time interning
            Ingredient[] ingredientCache = new Ingredient[poolSize];
            Item[] itemCache = new Item[poolSize];
            ResourceLocation[] rlCache = new ResourceLocation[poolSize];

            // Multi-threaded Parallel Pre-Warm of String Pool Interning
            java.util.stream.IntStream.range(0, poolSize).parallel().forEach(i -> {
                String s = stringPool[i];
                if (s != null && !s.isEmpty()) {
                    if (s.startsWith("{") || s.startsWith("[")) {
                        try {
                            com.google.gson.JsonElement jsonElement = com.google.gson.JsonParser.parseString(s);
                            ingredientCache[i] = Ingredient.fromJson(jsonElement);
                        } catch (Exception e) {
                            ingredientCache[i] = Ingredient.EMPTY;
                        }
                    } else if (s.contains(":")) {
                        try {
                            ResourceLocation rl = new ResourceLocation(s);
                            rlCache[i] = rl;
                            Item item = getItemFromRegistry(rl);
                            if (item != null && item != Items.AIR) {
                                itemCache[i] = item;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            });

            // Read Recipes
            int recipeCount = in.readInt();
            recipes = new ArrayList<>(recipeCount);
            for (int i = 0; i < recipeCount; i++) {
                Recipe<?> r = readRecipe(in, stringPool, ingredientCache, itemCache, rlCache);
                if (r != null) {
                    recipes.add(r);
                }
            }

            BuildScape.LOGGER.info("BDRE Binary Cache stream loaded successfully ({} recipes).", recipes.size());
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to load BDRE Binary Cache from stream", e);
            recipes.clear();
        }

        return recipes;
    }

    private static ResourceLocation getResourceLocation(int idx, String[] stringPool, ResourceLocation[] rlCache) {
        ResourceLocation rl = rlCache[idx];
        if (rl == null) {
            rl = new ResourceLocation(stringPool[idx]);
            rlCache[idx] = rl;
        }
        return rl;
    }

    private static int getStringIndex(String str, List<String> stringPool, Map<String, Integer> stringIndexMap) {
        if (str == null)
            str = "";
        return stringIndexMap.computeIfAbsent(str, s -> {
            int idx = stringPool.size();
            stringPool.add(s);
            return idx;
        });
    }

    private static void writeRecipe(DataOutputStream out, Recipe<?> recipe, List<String> stringPool,
            Map<String, Integer> stringMap) throws IOException {
        String idStr = recipe.getId().toString();
        String groupStr = recipe.getGroup() != null ? recipe.getGroup() : "";
        ResourceLocation resultItemKey = getItemKeyFromRegistry(recipe.getResultItem().getItem());
        String resultStr = resultItemKey != null ? resultItemKey.toString() : "minecraft:air";

        out.writeInt(getStringIndex(idStr, stringPool, stringMap));
        out.writeInt(getStringIndex(groupStr, stringPool, stringMap));
        out.writeInt(getStringIndex(resultStr, stringPool, stringMap));
        out.writeInt(recipe.getResultItem().getCount());
        String resultNbt = recipe.getResultItem().hasTag() ? recipe.getResultItem().getTag().toString() : "";
        out.writeInt(getStringIndex(resultNbt, stringPool, stringMap));

        if (recipe instanceof com.kingodogo.buildscape.recipe.ShapedDurabilityRecipe sdr) {
            out.writeByte(9); // Type Shaped Durability
            out.writeInt(sdr.getWidth());
            out.writeInt(sdr.getHeight());
            NonNullList<Ingredient> ingredients = sdr.getIngredients();
            out.writeInt(ingredients.size());
            for (Ingredient ing : ingredients) {
                writeIngredient(out, ing, stringPool, stringMap);
            }
        } else if (recipe instanceof com.kingodogo.buildscape.recipe.ShapelessDurabilityRecipe sdr) {
            out.writeByte(10); // Type Shapeless Durability
            NonNullList<Ingredient> ingredients = sdr.getIngredients();
            out.writeInt(ingredients.size());
            for (Ingredient ing : ingredients) {
                writeIngredient(out, ing, stringPool, stringMap);
            }
        } else if (recipe instanceof ShapedRecipe shaped) {
            out.writeByte(1); // Type Shaped
            out.writeInt(shaped.getWidth());
            out.writeInt(shaped.getHeight());
            NonNullList<Ingredient> ingredients = shaped.getIngredients();
            out.writeInt(ingredients.size());
            for (Ingredient ing : ingredients) {
                writeIngredient(out, ing, stringPool, stringMap);
            }
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            out.writeByte(2); // Type Shapeless
            NonNullList<Ingredient> ingredients = shapeless.getIngredients();
            out.writeInt(ingredients.size());
            for (Ingredient ing : ingredients) {
                writeIngredient(out, ing, stringPool, stringMap);
            }
        } else if (recipe instanceof StonecutterRecipe sc) {
            out.writeByte(3); // Type Stonecutter
            writeIngredient(out, getSafeIngredient(sc, 0), stringPool, stringMap);
        } else if (recipe instanceof SmeltingRecipe smelting) {
            out.writeByte(4); // Smelting
            writeIngredient(out, getSafeIngredient(smelting, 0), stringPool, stringMap);
            out.writeFloat(smelting.getExperience());
            out.writeInt(smelting.getCookingTime());
        } else if (recipe instanceof BlastingRecipe blasting) {
            out.writeByte(5); // Blasting
            writeIngredient(out, getSafeIngredient(blasting, 0), stringPool, stringMap);
            out.writeFloat(blasting.getExperience());
            out.writeInt(blasting.getCookingTime());
        } else if (recipe instanceof SmokingRecipe smoking) {
            out.writeByte(6); // Smoking
            writeIngredient(out, getSafeIngredient(smoking, 0), stringPool, stringMap);
            out.writeFloat(smoking.getExperience());
            out.writeInt(smoking.getCookingTime());
        } else if (recipe instanceof CampfireCookingRecipe campfire) {
            out.writeByte(7); // Campfire
            writeIngredient(out, getSafeIngredient(campfire, 0), stringPool, stringMap);
            out.writeFloat(campfire.getExperience());
            out.writeInt(campfire.getCookingTime());
        } else if (recipe instanceof UpgradeRecipe smithing) {
            out.writeByte(8); // Smithing
            writeIngredient(out, smithing.base, stringPool, stringMap);
            writeIngredient(out, smithing.addition, stringPool, stringMap);
        } else if (recipe instanceof com.kingodogo.buildscape.recipe.ConfettiConfigureRecipe) {
            out.writeByte(11); // Confetti Configure
        } else if (recipe instanceof com.kingodogo.buildscape.recipe.ClearShulkerFiltersRecipe) {
            out.writeByte(12); // Clear Shulker Filters
        } else {
            throw new IOException("Unsupported recipe class for BDRE cache: " + recipe.getClass().getName());
        }
    }

    private static Ingredient getSafeIngredient(Recipe<?> recipe, int index) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        if (ingredients != null && index >= 0 && index < ingredients.size()) {
            return ingredients.get(index);
        }
        return Ingredient.EMPTY;
    }

    private static Recipe<?> readRecipe(
            DataInputStream in,
            String[] stringPool,
            Ingredient[] ingredientCache,
            Item[] itemCache,
            ResourceLocation[] rlCache) throws IOException {
        int idIdx = in.readInt();
        ResourceLocation id = getResourceLocation(idIdx, stringPool, rlCache);

        int groupIdx = in.readInt();
        String group = stringPool[groupIdx];

        int itemIdx = in.readInt();
        Item resultItem = itemCache[itemIdx];
        if (resultItem == null) {
            resultItem = getItemFromRegistry(getResourceLocation(itemIdx, stringPool, rlCache));
            if (resultItem == null)
                resultItem = Items.AIR;
            itemCache[itemIdx] = resultItem;
        }

        int count = in.readInt();
        ItemStack result = resultItem != Items.AIR ? new ItemStack(resultItem, count) : ItemStack.EMPTY;
        int resultNbtIdx = in.readInt();
        String resultNbt = stringPool[resultNbtIdx];
        if (!result.isEmpty() && resultNbt != null && !resultNbt.isEmpty()) {
            try {
                result.setTag(net.minecraft.nbt.TagParser.parseTag(resultNbt));
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
                throw new IOException("Invalid cached recipe result NBT", e);
            }
        }

        byte type = in.readByte();
        switch (type) {
            case 1 -> { // Shaped
                int width = in.readInt();
                int height = in.readInt();
                int ingSize = in.readInt();
                NonNullList<Ingredient> ingredients = NonNullList.withSize(ingSize, Ingredient.EMPTY);
                for (int i = 0; i < ingSize; i++) {
                    ingredients.set(i, readIngredient(in, stringPool, ingredientCache));
                }
                return new ShapedRecipe(id, group, width, height, ingredients, result);
            }
            case 2 -> { // Shapeless
                int ingSize = in.readInt();
                NonNullList<Ingredient> ingredients = NonNullList.create();
                for (int i = 0; i < ingSize; i++) {
                    ingredients.add(readIngredient(in, stringPool, ingredientCache));
                }
                return new ShapelessRecipe(id, group, result, ingredients);
            }
            case 3 -> { // Stonecutter
                Ingredient ing = readIngredient(in, stringPool, ingredientCache);
                return new StonecutterRecipe(id, group, ing, result);
            }
            case 4 -> { // Smelting
                Ingredient ing = readIngredient(in, stringPool, ingredientCache);
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new SmeltingRecipe(id, group, ing, result, xp, cookTime);
            }
            case 5 -> { // Blasting
                Ingredient ing = readIngredient(in, stringPool, ingredientCache);
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new BlastingRecipe(id, group, ing, result, xp, cookTime);
            }
            case 6 -> { // Smoking
                Ingredient ing = readIngredient(in, stringPool, ingredientCache);
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new SmokingRecipe(id, group, ing, result, xp, cookTime);
            }
            case 7 -> { // Campfire
                Ingredient ing = readIngredient(in, stringPool, ingredientCache);
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new CampfireCookingRecipe(id, group, ing, result, xp, cookTime);
            }
            case 8 -> { // Smithing (UpgradeRecipe)
                Ingredient base = readIngredient(in, stringPool, ingredientCache);
                Ingredient addition = readIngredient(in, stringPool, ingredientCache);
                return new UpgradeRecipe(id, base, addition, result);
            }
            case 9 -> { // ShapedDurability
                int width = in.readInt();
                int height = in.readInt();
                int ingSize = in.readInt();
                NonNullList<Ingredient> ingredients = NonNullList.withSize(ingSize, Ingredient.EMPTY);
                for (int i = 0; i < ingSize; i++) {
                    ingredients.set(i, readIngredient(in, stringPool, ingredientCache));
                }
                return new com.kingodogo.buildscape.recipe.ShapedDurabilityRecipe(id, group, width, height, ingredients,
                        result, 1);
            }
            case 10 -> { // ShapelessDurability
                int ingSize = in.readInt();
                NonNullList<Ingredient> ingredients = NonNullList.create();
                for (int i = 0; i < ingSize; i++) {
                    ingredients.add(readIngredient(in, stringPool, ingredientCache));
                }
                return new com.kingodogo.buildscape.recipe.ShapelessDurabilityRecipe(id, group, result, ingredients, 1);
            }
            case 11 -> { // ConfettiConfigure
                return new com.kingodogo.buildscape.recipe.ConfettiConfigureRecipe(id);
            }
            case 12 -> { // ClearShulkerFilters
                return new com.kingodogo.buildscape.recipe.ClearShulkerFiltersRecipe(id);
            }
            default -> {
                return null;
            }
        }
    }

    private static void writeIngredient(DataOutputStream out, Ingredient ing, List<String> stringPool,
            Map<String, Integer> stringMap) throws IOException {
        String jsonStr = "";
        if (ing != null && !ing.isEmpty()) {
            try {
                jsonStr = ing.toJson().toString();
            } catch (Exception ignored) {
            }
        }
        out.writeInt(getStringIndex(jsonStr, stringPool, stringMap));
    }

    private static Ingredient readIngredient(DataInputStream in, String[] stringPool, Ingredient[] ingredientCache)
            throws IOException {
        int idx = in.readInt();
        Ingredient ing = ingredientCache[idx];
        if (ing == null) {
            String jsonStr = stringPool[idx];
            if (jsonStr == null || jsonStr.isEmpty() || "{}".equals(jsonStr)) {
                ing = Ingredient.EMPTY;
            } else {
                try {
                    com.google.gson.JsonElement jsonElement = com.google.gson.JsonParser.parseString(jsonStr);
                    ing = Ingredient.fromJson(jsonElement);
                } catch (Exception e) {
                    ing = Ingredient.EMPTY;
                }
            }
            ingredientCache[idx] = ing;
        }
        return ing;
    }

    private static Path findWorkspaceRecipesPackDir() {
        Path relativePath = Path.of("src/main/resources/data/buildscape/recipes_pack");
        if (Files.exists(relativePath)) return relativePath;

        Path parentRelative = Path.of("../src/main/resources/data/buildscape/recipes_pack");
        if (Files.exists(parentRelative)) return parentRelative;

        try {
            Path current = FMLPaths.GAMEDIR.get();
            while (current != null) {
                Path candidate = current.resolve("src/main/resources/data/buildscape/recipes_pack");
                if (Files.exists(candidate)) return candidate;
                current = current.getParent();
            }
        } catch (Exception ignored) {}

        return null;
    }
}
