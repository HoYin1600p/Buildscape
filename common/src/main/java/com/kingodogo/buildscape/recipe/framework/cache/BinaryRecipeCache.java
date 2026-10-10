package com.kingodogo.buildscape.recipe.framework.cache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;

public class BinaryRecipeCache {

    private static final int MAGIC_HEADER = 0x4B59524F;
    private static final int CACHE_VERSION = 5;
    private static final int SOURCE_SCHEMA_VERSION = 1;
    private static final int MAX_STRING_POOL_SIZE = 262_144;
    private static final int MAX_RECIPE_COUNT = 262_144;
    private static final int MAX_INGREDIENT_COUNT = 4_096;

    public static Path getCacheDir() {
        Path dir = Paths.get("buildscape", "cache");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            BuildscapeCommon.LOGGER.error("Failed to create cache directory: {}", dir, e);
        }
        return dir;
    }

    public static String computeSourceHash(String[] categories, Map<String, byte[]> rawCategoryData) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(ByteBuffer.allocate(4).putInt(SOURCE_SCHEMA_VERSION).array());

            for (String category : categories) {
                byte[] data = rawCategoryData.get(category);
                if (data != null) {
                    digest.update(category.getBytes(StandardCharsets.UTF_8));
                    digest.update(data);
                }
            }

            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            BuildscapeCommon.LOGGER.error("BDRE Cache: SHA-256 algorithm not available", e);
            return "";
        }
    }

    public static boolean isCacheValid(String expectedHash) {
        Path cacheFile = getCacheDir().resolve("recipes.bscb");
        if (!Files.exists(cacheFile)) {
            return false;
        }

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(cacheFile)))) {
            int header = in.readInt();
            int version = in.readInt();
            if (header != MAGIC_HEADER || version != CACHE_VERSION) {
                return false;
            }
            String cachedHash = in.readUTF();
            return expectedHash != null && !expectedHash.isBlank() && expectedHash.equals(cachedHash);
        } catch (IOException e) {
            // The reference treats unreadable cache headers as a cache miss.
            return false;
        }
    }

    public static void saveCache(String contentHash, List<RecipeIR.CompiledRecipe> recipes) {
        Path cacheDir = getCacheDir();
        Path tempFile = cacheDir.resolve("recipes.bscb.tmp." + System.nanoTime());
        Path finalFile = cacheDir.resolve("recipes.bscb");

        try {
            List<String> stringPool = new ArrayList<>();
            Map<String, Integer> stringMap = new HashMap<>();

            ByteArrayOutputStream recipeByteStream = new ByteArrayOutputStream(recipes.size() * 128);
            DataOutputStream recipeDataOut = new DataOutputStream(recipeByteStream);

            int savedCount = 0;
            for (RecipeIR.CompiledRecipe r : recipes) {
                if (r == null) continue;
                writeRecipe(recipeDataOut, r, stringPool, stringMap);
                savedCount++;
            }
            recipeDataOut.flush();

            try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(tempFile))) {
                out.writeInt(MAGIC_HEADER);
                out.writeInt(CACHE_VERSION);
                out.writeUTF(contentHash != null ? contentHash : "");

                out.writeInt(stringPool.size());
                for (String s : stringPool) {
                    out.writeUTF(s);
                }

                out.writeInt(savedCount);
                out.write(recipeByteStream.toByteArray());
                out.flush();
            }

            try {
                Files.move(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                // The reference falls back to a regular move on filesystems without atomic moves.
                Files.move(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING);
            }

            BuildscapeCommon.LOGGER.info("BDRE Binary Cache saved successfully ({} recipes).", savedCount);
        } catch (Exception e) {
            BuildscapeCommon.LOGGER.error("BDRE Binary Cache: Failed to save recipes to cache", e);
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
                BuildscapeCommon.LOGGER.warn("Unable to remove failed recipe cache temporary file {}", tempFile, ignored);
            }
        }
    }

    public static List<RecipeIR.CompiledRecipe> loadCache(String expectedHash) {
        Path cacheFile = getCacheDir().resolve("recipes.bscb");
        if (!Files.exists(cacheFile)) {
            return new ArrayList<>();
        }
        try {
            return loadCacheFromStream(Files.newInputStream(cacheFile), expectedHash);
        } catch (IOException e) {
            // The reference recompiles recipes when a cache file cannot be opened.
            return new ArrayList<>();
        }
    }

    public static List<RecipeIR.CompiledRecipe> loadCacheFromStream(InputStream rawStream, String expectedHash) {
        List<RecipeIR.CompiledRecipe> recipes = new ArrayList<>();
        if (rawStream == null) {
            return recipes;
        }

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(rawStream, 65536))) {
            int header = in.readInt();
            int version = in.readInt();
            if (header != MAGIC_HEADER || version != CACHE_VERSION) {
                BuildscapeCommon.LOGGER.warn("BDRE Binary Cache stream version mismatch.");
                return recipes;
            }

            String cachedHash = in.readUTF();
            if (expectedHash != null && !expectedHash.isBlank() && !expectedHash.equals(cachedHash)) {
                BuildscapeCommon.LOGGER.warn("BDRE Binary Cache content hash mismatch; recompiling from source recipes.");
                return recipes;
            }

            int poolSize = in.readInt();
            validateSize(poolSize, MAX_STRING_POOL_SIZE, "string pool");
            String[] stringPool = new String[poolSize];
            for (int i = 0; i < poolSize; i++) {
                stringPool[i] = in.readUTF();
            }

            int recipeCount = in.readInt();
            validateSize(recipeCount, MAX_RECIPE_COUNT, "recipe count");
            recipes = new ArrayList<>(recipeCount);
            for (int i = 0; i < recipeCount; i++) {
                RecipeIR.CompiledRecipe r = readRecipe(in, stringPool);
                if (r != null) {
                    recipes.add(r);
                }
            }

            BuildscapeCommon.LOGGER.info("BDRE Binary Cache stream loaded successfully ({} recipes).", recipes.size());
        } catch (Exception e) {
            BuildscapeCommon.LOGGER.warn("BDRE Binary Cache rejected: {}; recompiling from compact sources.", e.getMessage(), e);
            recipes.clear();
        }

        return recipes;
    }

    private static int getStringIndex(String str, List<String> stringPool, Map<String, Integer> stringIndexMap) {
        if (str == null) str = "";
        return stringIndexMap.computeIfAbsent(str, s -> {
            int idx = stringPool.size();
            stringPool.add(s);
            return idx;
        });
    }

    private static void writeRecipe(DataOutputStream out, RecipeIR.CompiledRecipe recipe, List<String> stringPool,
            Map<String, Integer> stringMap) throws IOException {
        String idStr = recipe.id();
        String groupStr = recipe.group() != null ? recipe.group() : "";
        String resultStr = recipe.resultItem() != null ? recipe.resultItem() : "minecraft:air";

        out.writeInt(getStringIndex(idStr, stringPool, stringMap));
        out.writeInt(getStringIndex(groupStr, stringPool, stringMap));
        out.writeInt(getStringIndex(resultStr, stringPool, stringMap));
        out.writeInt(recipe.resultCount());
        out.writeInt(getStringIndex(recipe.resultNbt(), stringPool, stringMap));

        String type = recipe.type().toLowerCase(java.util.Locale.ROOT);
        if (type.startsWith("buildscape:")) type = type.substring("buildscape:".length());

        switch (type) {
            case "shaped" -> {
                out.writeByte(1);
                out.writeInt(recipe.width());
                out.writeInt(recipe.height());
                List<String> ings = recipe.ingredients();
                out.writeInt(ings.size());
                for (String ing : ings) {
                    out.writeInt(getStringIndex(ing, stringPool, stringMap));
                }
            }
            case "shapeless" -> {
                out.writeByte(2);
                List<String> ings = recipe.ingredients();
                out.writeInt(ings.size());
                for (String ing : ings) {
                    out.writeInt(getStringIndex(ing, stringPool, stringMap));
                }
            }
            case "stonecutting" -> {
                out.writeByte(3);
                out.writeInt(getStringIndex(recipe.input(), stringPool, stringMap));
            }
            case "smelting" -> {
                out.writeByte(4);
                out.writeInt(getStringIndex(recipe.input(), stringPool, stringMap));
                out.writeFloat(recipe.experience());
                out.writeInt(recipe.cookingTime());
            }
            case "blasting" -> {
                out.writeByte(5);
                out.writeInt(getStringIndex(recipe.input(), stringPool, stringMap));
                out.writeFloat(recipe.experience());
                out.writeInt(recipe.cookingTime());
            }
            case "smoking" -> {
                out.writeByte(6);
                out.writeInt(getStringIndex(recipe.input(), stringPool, stringMap));
                out.writeFloat(recipe.experience());
                out.writeInt(recipe.cookingTime());
            }
            case "campfire", "campfire_cooking" -> {
                out.writeByte(7);
                out.writeInt(getStringIndex(recipe.input(), stringPool, stringMap));
                out.writeFloat(recipe.experience());
                out.writeInt(recipe.cookingTime());
            }
            case "smithing" -> {
                out.writeByte(8);
                out.writeInt(getStringIndex(recipe.input(), stringPool, stringMap));
                out.writeInt(getStringIndex(recipe.addition(), stringPool, stringMap));
            }
            case "shaped_durability" -> {
                out.writeByte(9);
                out.writeInt(recipe.width());
                out.writeInt(recipe.height());
                List<String> ings = recipe.ingredients();
                out.writeInt(ings.size());
                for (String ing : ings) {
                    out.writeInt(getStringIndex(ing, stringPool, stringMap));
                }
            }
            case "shapeless_durability" -> {
                out.writeByte(10);
                List<String> ings = recipe.ingredients();
                out.writeInt(ings.size());
                for (String ing : ings) {
                    out.writeInt(getStringIndex(ing, stringPool, stringMap));
                }
            }
            case "confetti_configure" -> {
                out.writeByte(11);
            }
            case "clear_shulker_filters" -> {
                out.writeByte(12);
            }
            default -> {
                throw new IOException("Unsupported recipe type for caching: " + type);
            }
        }
    }

    private static RecipeIR.CompiledRecipe readRecipe(DataInputStream in, String[] stringPool) throws IOException {
        int idIdx = readPoolIndex(in, stringPool.length, "recipe id");
        int groupIdx = readPoolIndex(in, stringPool.length, "group");
        int resultItemIdx = readPoolIndex(in, stringPool.length, "result item");
        int count = in.readInt();
        int resultNbtIdx = readPoolIndex(in, stringPool.length, "result nbt");

        String id = stringPool[idIdx];
        String group = stringPool[groupIdx];
        String resultItem = stringPool[resultItemIdx];
        String resultNbt = stringPool[resultNbtIdx];

        byte type = in.readByte();
        switch (type) {
            case 1 -> {
                int width = in.readInt();
                int height = in.readInt();
                int ingSize = in.readInt();
                validateDimensions(width, height, ingSize);
                List<String> ingredients = new ArrayList<>(ingSize);
                for (int i = 0; i < ingSize; i++) {
                    int ingIdx = readPoolIndex(in, stringPool.length, "ingredient");
                    ingredients.add(stringPool[ingIdx]);
                }
                return new RecipeIR.CompiledRecipe(id, "shaped", group, width, height, ingredients, "", "", resultItem, count, resultNbt, 0f, 0, 0);
            }
            case 2 -> {
                int ingSize = in.readInt();
                validateSize(ingSize, MAX_INGREDIENT_COUNT, "shapeless ingredient count");
                List<String> ingredients = new ArrayList<>(ingSize);
                for (int i = 0; i < ingSize; i++) {
                    int ingIdx = readPoolIndex(in, stringPool.length, "ingredient");
                    ingredients.add(stringPool[ingIdx]);
                }
                return new RecipeIR.CompiledRecipe(id, "shapeless", group, 0, 0, ingredients, "", "", resultItem, count, resultNbt, 0f, 0, 0);
            }
            case 3 -> {
                int inputIdx = readPoolIndex(in, stringPool.length, "stonecutter input");
                String input = stringPool[inputIdx];
                return new RecipeIR.CompiledRecipe(id, "stonecutting", group, 0, 0, List.of(), input, "", resultItem, count, resultNbt, 0f, 0, 0);
            }
            case 4 -> {
                int inputIdx = readPoolIndex(in, stringPool.length, "smelting input");
                String input = stringPool[inputIdx];
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new RecipeIR.CompiledRecipe(id, "smelting", group, 0, 0, List.of(), input, "", resultItem, count, resultNbt, xp, cookTime, 0);
            }
            case 5 -> {
                int inputIdx = readPoolIndex(in, stringPool.length, "blasting input");
                String input = stringPool[inputIdx];
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new RecipeIR.CompiledRecipe(id, "blasting", group, 0, 0, List.of(), input, "", resultItem, count, resultNbt, xp, cookTime, 0);
            }
            case 6 -> {
                int inputIdx = readPoolIndex(in, stringPool.length, "smoking input");
                String input = stringPool[inputIdx];
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new RecipeIR.CompiledRecipe(id, "smoking", group, 0, 0, List.of(), input, "", resultItem, count, resultNbt, xp, cookTime, 0);
            }
            case 7 -> {
                int inputIdx = readPoolIndex(in, stringPool.length, "campfire input");
                String input = stringPool[inputIdx];
                float xp = in.readFloat();
                int cookTime = in.readInt();
                return new RecipeIR.CompiledRecipe(id, "campfire", group, 0, 0, List.of(), input, "", resultItem, count, resultNbt, xp, cookTime, 0);
            }
            case 8 -> {
                int baseIdx = readPoolIndex(in, stringPool.length, "smithing base");
                int additionIdx = readPoolIndex(in, stringPool.length, "smithing addition");
                String base = stringPool[baseIdx];
                String addition = stringPool[additionIdx];
                return new RecipeIR.CompiledRecipe(id, "smithing", group, 0, 0, List.of(), base, addition, resultItem, count, resultNbt, 0f, 0, 0);
            }
            case 9 -> {
                int width = in.readInt();
                int height = in.readInt();
                int ingSize = in.readInt();
                validateDimensions(width, height, ingSize);
                List<String> ingredients = new ArrayList<>(ingSize);
                for (int i = 0; i < ingSize; i++) {
                    int ingIdx = readPoolIndex(in, stringPool.length, "ingredient");
                    ingredients.add(stringPool[ingIdx]);
                }
                return new RecipeIR.CompiledRecipe(id, "shaped_durability", group, width, height, ingredients, "", "", resultItem, count, resultNbt, 0f, 0, 1);
            }
            case 10 -> {
                int ingSize = in.readInt();
                validateSize(ingSize, MAX_INGREDIENT_COUNT, "shapeless durability ingredient count");
                List<String> ingredients = new ArrayList<>(ingSize);
                for (int i = 0; i < ingSize; i++) {
                    int ingIdx = readPoolIndex(in, stringPool.length, "ingredient");
                    ingredients.add(stringPool[ingIdx]);
                }
                return new RecipeIR.CompiledRecipe(id, "shapeless_durability", group, 0, 0, ingredients, "", "", resultItem, count, resultNbt, 0f, 0, 1);
            }
            case 11 -> {
                return new RecipeIR.CompiledRecipe(id, "confetti_configure", "", 0, 0, List.of(), "", "", "minecraft:air", 1, "", 0f, 0, 0);
            }
            case 12 -> {
                return new RecipeIR.CompiledRecipe(id, "clear_shulker_filters", "", 0, 0, List.of(), "", "", "minecraft:air", 1, "", 0f, 0, 0);
            }
            default -> {
                throw new IOException("Unsupported cached recipe type: " + type);
            }
        }
    }

    private static int readPoolIndex(DataInputStream in, int poolSize, String context) throws IOException {
        int idx = in.readInt();
        if (idx < 0 || idx >= poolSize) {
            throw new IOException("Corrupt cache: " + context + " string pool index " + idx + " out of bounds (" + poolSize + ")");
        }
        return idx;
    }

    private static void validateSize(int size, int max, String context) throws IOException {
        if (size < 0 || size > max) {
            throw new IOException("Corrupt cache: " + context + " size " + size + " exceeds maximum " + max);
        }
    }

    private static void validateDimensions(int width, int height, int ingSize) throws IOException {
        if (width <= 0 || width > 3 || height <= 0 || height > 3) {
            throw new IOException("Corrupt cache: invalid shaped recipe dimensions " + width + "x" + height);
        }
        if (ingSize != width * height) {
            throw new IOException("Corrupt cache: ingredient count " + ingSize + " doesn't match dimensions " + width + "x" + height);
        }
    }
}
