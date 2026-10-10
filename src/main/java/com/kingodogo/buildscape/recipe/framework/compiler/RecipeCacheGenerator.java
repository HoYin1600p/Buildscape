package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import com.kingodogo.buildscape.recipe.framework.util.PatternBounds;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Compiles BDRE recipe pack JSON sources into binary cache (recipes.bscb) with SHA-256 integrity validation.
public class RecipeCacheGenerator {

    public static final int MAGIC_HEADER = 0x4B59524F;
    public static final int CACHE_VERSION = 5;
    public static final int SOURCE_SCHEMA_VERSION = 1;
    // Bump whenever compilation logic changes, even if the binary layout does not.
    public static final int GENERATOR_VERSION = 2;

    public static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    public static String computeSourceHash(String[] categoryOrder, Map<String, byte[]> categoryData) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            updateDigestInt(digest, SOURCE_SCHEMA_VERSION);
            updateDigestInt(digest, CACHE_VERSION);
            updateDigestInt(digest, GENERATOR_VERSION);
            for (String category : categoryOrder) {
                byte[] name = category.getBytes(StandardCharsets.UTF_8);
                updateDigestInt(digest, name.length);
                digest.update(name);
                byte[] content = categoryData.get(category);
                digest.update((byte) (content == null ? 0 : 1));
                if (content != null) {
                    content = new String(content, StandardCharsets.UTF_8).replace("\r\n", "\n")
                            .getBytes(StandardCharsets.UTF_8);
                    updateDigestInt(digest, content.length);
                    digest.update(content);
                }
            }
            byte[] hashBytes = digest.digest();
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private static void updateDigestInt(MessageDigest digest, int value) {
        digest.update(ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
    }

    public static boolean isCacheValid(Path cacheFile, String currentHash) {
        if (!Files.isRegularFile(cacheFile)) {
            return false;
        }
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(cacheFile)))) {
            return in.readInt() == MAGIC_HEADER
                    && in.readInt() == CACHE_VERSION
                    && in.readUTF().equals(currentHash);
        } catch (IOException e) {
            return false;
        }
    }

    public static byte[] generateCache(Path sourceDirectory) throws IOException {
        Map<String, byte[]> rawCategoryData = new LinkedHashMap<>();

        for (String category : CATEGORIES) {
            Path sourceFile = sourceDirectory.resolve(category + ".json");
            if (Files.isRegularFile(sourceFile)) {
                rawCategoryData.put(category, Files.readAllBytes(sourceFile));
                com.kingodogo.buildscape.recipe.framework.util.RecipePackFormatter.validateJson(
                        Files.readString(sourceFile, StandardCharsets.UTF_8));
            }
        }

        String contentHash = computeSourceHash(CATEGORIES, rawCategoryData);
        Set<String> runtimeCategories = findRuntimeCategories(rawCategoryData);

        List<String> stringPool = new ArrayList<>();
        Map<String, Integer> stringIndexMap = new HashMap<>();

        ByteArrayOutputStream recipeByteStream = new ByteArrayOutputStream();
        DataOutputStream recipeDataOut = new DataOutputStream(recipeByteStream);

        int totalRecipeCount = 0;
        Set<String> registeredRecipeIds = new HashSet<>();

        for (String category : CATEGORIES) {
            if (runtimeCategories.contains(category)) {
                continue;
            }

            byte[] bytes = rawCategoryData.get(category);
            if (bytes == null) {
                continue;
            }

            try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                AliasResolver aliasResolver = new AliasResolver();
                aliasResolver.registerAliases(categoryPack.aliases());

                TemplateEngine templateEngine = new TemplateEngine();
                templateEngine.registerTemplates(categoryPack.templates());

                FamilyExpander familyExpander = new FamilyExpander(templateEngine);

                List<RecipeIR.RecipeSpec> allSpecs = new ArrayList<>();

                if (categoryPack.families() != null) {
                    for (RecipeIR.FamilySpec family : categoryPack.families()) {
                        allSpecs.addAll(familyExpander.expandFamily(family));
                    }
                }

                if (categoryPack.recipes() != null) {
                    allSpecs.addAll(categoryPack.recipes());
                }

                for (RecipeIR.RecipeSpec spec : allSpecs) {
                    if (spec == null) continue;

                    String specType = spec.type() != null ? spec.type().toLowerCase(Locale.ROOT) : category.toLowerCase(Locale.ROOT);
                    if ("forge:conditional".equals(specType) || (spec.rawJson() != null && spec.rawJson().contains("\"conditions\""))) {
                        continue;
                    }

                    String rawId = spec.id() != null ? spec.id() : generateRecipeId(spec, aliasResolver);
                    String cleanPath = sanitizePath("autogen/" + category + "/" + rawId, aliasResolver);
                    String recipeId = "buildscape:" + cleanPath;

                    if (registeredRecipeIds.contains(recipeId)) {
                        System.err.println("WARNING: Duplicate recipe ID in " + category + ": " + recipeId
                                + "; retaining the first recipe only");
                        continue;
                    }
                    registeredRecipeIds.add(recipeId);

                    String group = spec.group() != null ? spec.group() : "";
                    String resultItemStr = (spec.result() != null && spec.result().item() != null)
                            ? aliasResolver.resolveString(spec.result().item()) : "minecraft:air";
                    int count = spec.result() != null ? spec.result().count() : 1;
                    String resultNbt = (spec.result() != null && spec.result().nbt() != null) ? spec.result().nbt() : "";

                    recipeDataOut.writeInt(getStringIndex(recipeId, stringPool, stringIndexMap));
                    recipeDataOut.writeInt(getStringIndex(group, stringPool, stringIndexMap));
                    recipeDataOut.writeInt(getStringIndex(resultItemStr, stringPool, stringIndexMap));
                    recipeDataOut.writeInt(count);
                    recipeDataOut.writeInt(getStringIndex(resultNbt, stringPool, stringIndexMap));

                    switch (specType) {
                        case "shaped" -> {
                            recipeDataOut.writeByte(1);
                            writeShapedRecipe(recipeDataOut, spec, aliasResolver, stringPool, stringIndexMap);
                        }
                        case "shapeless" -> {
                            recipeDataOut.writeByte(2);
                            writeShapelessRecipe(recipeDataOut, spec, aliasResolver, stringPool, stringIndexMap);
                        }
                        case "stonecutting" -> {
                            recipeDataOut.writeByte(3);
                            String ingJson = resolveIngredientJson(spec.input(), aliasResolver);
                            recipeDataOut.writeInt(getStringIndex(ingJson, stringPool, stringIndexMap));
                        }
                        case "smelting" -> {
                            recipeDataOut.writeByte(4);
                            String ingJson = resolveIngredientJson(spec.input(), aliasResolver);
                            recipeDataOut.writeInt(getStringIndex(ingJson, stringPool, stringIndexMap));
                            recipeDataOut.writeFloat(spec.experience());
                            recipeDataOut.writeInt(spec.cookingTime());
                        }
                        case "blasting" -> {
                            recipeDataOut.writeByte(5);
                            String ingJson = resolveIngredientJson(spec.input(), aliasResolver);
                            recipeDataOut.writeInt(getStringIndex(ingJson, stringPool, stringIndexMap));
                            recipeDataOut.writeFloat(spec.experience());
                            recipeDataOut.writeInt(spec.cookingTime());
                        }
                        case "smoking" -> {
                            recipeDataOut.writeByte(6);
                            String ingJson = resolveIngredientJson(spec.input(), aliasResolver);
                            recipeDataOut.writeInt(getStringIndex(ingJson, stringPool, stringIndexMap));
                            recipeDataOut.writeFloat(spec.experience());
                            recipeDataOut.writeInt(spec.cookingTime());
                        }
                        case "campfire", "campfire_cooking" -> {
                            recipeDataOut.writeByte(7);
                            String ingJson = resolveIngredientJson(spec.input(), aliasResolver);
                            recipeDataOut.writeInt(getStringIndex(ingJson, stringPool, stringIndexMap));
                            recipeDataOut.writeFloat(spec.experience());
                            recipeDataOut.writeInt(spec.cookingTime());
                        }
                        case "smithing" -> {
                            recipeDataOut.writeByte(8);
                            String baseIngJson = resolveIngredientJson(spec.input(), aliasResolver);
                            String addIngJson = (spec.ingredients() != null && !spec.ingredients().isEmpty())
                                    ? resolveIngredientJson(spec.ingredients().get(0), aliasResolver) : "";
                            recipeDataOut.writeInt(getStringIndex(baseIngJson, stringPool, stringIndexMap));
                            recipeDataOut.writeInt(getStringIndex(addIngJson, stringPool, stringIndexMap));
                        }
                        case "shaped_durability", "buildscape:shaped_durability" -> {
                            recipeDataOut.writeByte(9);
                            writeShapedRecipe(recipeDataOut, spec, aliasResolver, stringPool, stringIndexMap);
                        }
                        case "shapeless_durability", "buildscape:shapeless_durability" -> {
                            recipeDataOut.writeByte(10);
                            writeShapelessRecipe(recipeDataOut, spec, aliasResolver, stringPool, stringIndexMap);
                        }
                        case "confetti_configure", "buildscape:confetti_configure" -> {
                            recipeDataOut.writeByte(11);
                        }
                        case "clear_shulker_filters", "buildscape:clear_shulker_filters" -> {
                            recipeDataOut.writeByte(12);
                        }
                        default -> {
                            throw new IllegalArgumentException("Unknown recipe type: " + specType);
                        }
                    }

                    totalRecipeCount++;
                }
            }
        }

        ByteArrayOutputStream finalOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(finalOut);
        out.writeInt(MAGIC_HEADER);
        out.writeInt(CACHE_VERSION);
        out.writeUTF(contentHash);

        out.writeInt(stringPool.size());
        for (String s : stringPool) {
            out.writeUTF(s);
        }

        out.writeInt(totalRecipeCount);
        out.write(recipeByteStream.toByteArray());

        return finalOut.toByteArray();
    }

    private static void writeShapedRecipe(
            DataOutputStream out,
            RecipeIR.RecipeSpec spec,
            AliasResolver aliasResolver,
            List<String> stringPool,
            Map<String, Integer> stringIndexMap) throws IOException {

        List<String> patternList = spec.pattern();
        if (patternList == null) patternList = List.of();
        int height = patternList.size();
        int width = 0;
        for (String line : patternList) {
            width = Math.max(width, line.length());
        }

        Map<Character, String> keyMap = new HashMap<>();
        if (spec.keys() != null) {
            for (Map.Entry<String, String> entry : spec.keys().entrySet()) {
                if (!entry.getKey().isEmpty()) {
                    char c = entry.getKey().charAt(0);
                    keyMap.put(c, resolveIngredientJson(entry.getValue(), aliasResolver));
                }
            }
        }

        List<String> ingredients = new ArrayList<>(width * height);
        for (int row = 0; row < height; row++) {
            String line = patternList.get(row);
            for (int col = 0; col < width; col++) {
                char c = col < line.length() ? line.charAt(col) : ' ';
                if (c != ' ') {
                    ingredients.add(keyMap.getOrDefault(c, ""));
                } else {
                    ingredients.add("");
                }
            }
        }

        PatternBounds bounds = PatternBounds.of(width, height, index -> {
            String s = ingredients.get(index);
            return s != null && !s.isEmpty();
        });

        int trimmedWidth = bounds.width();
        int trimmedHeight = bounds.height();
        int trimmedSize = trimmedWidth * trimmedHeight;

        out.writeInt(trimmedWidth);
        out.writeInt(trimmedHeight);
        out.writeInt(trimmedSize);

        for (int row = 0; row < trimmedHeight; row++) {
            for (int col = 0; col < trimmedWidth; col++) {
                String ingJson = ingredients.get(bounds.sourceIndex(row, col, width));
                out.writeInt(getStringIndex(ingJson != null ? ingJson : "", stringPool, stringIndexMap));
            }
        }
    }

    private static void writeShapelessRecipe(
            DataOutputStream out,
            RecipeIR.RecipeSpec spec,
            AliasResolver aliasResolver,
            List<String> stringPool,
            Map<String, Integer> stringIndexMap) throws IOException {

        List<String> ingredients = spec.ingredients();
        if (ingredients == null) ingredients = List.of();

        List<String> resolved = new ArrayList<>();
        for (String ingStr : ingredients) {
            String json = resolveIngredientJson(ingStr, aliasResolver);
            if (!json.isEmpty()) {
                resolved.add(json);
            }
        }

        out.writeInt(resolved.size());
        for (String ingJson : resolved) {
            out.writeInt(getStringIndex(ingJson, stringPool, stringIndexMap));
        }
    }

    public static String resolveIngredientJson(String rawSpec, AliasResolver aliasResolver) {
        if (rawSpec == null || rawSpec.isEmpty()) {
            return "";
        }

        if (rawSpec.startsWith("[") && rawSpec.endsWith("]")) {
            String inner = rawSpec.substring(1, rawSpec.length() - 1);
            String[] parts = inner.split(",");
            List<String> entries = new ArrayList<>();
            for (String part : parts) {
                String itemJson = resolveIngredientJson(part.trim(), aliasResolver);
                if (!itemJson.isEmpty()) {
                    entries.add(itemJson);
                }
            }
            if (entries.isEmpty()) return "";
            return "[" + String.join(",", entries) + "]";
        }

        String resolved = aliasResolver.resolveString(rawSpec);
        if (resolved.startsWith("#")) {
            return "{\"tag\":\"" + resolved.substring(1) + "\"}";
        } else {
            return "{\"item\":\"" + resolved + "\"}";
        }
    }

    private static String generateRecipeId(RecipeIR.RecipeSpec spec, AliasResolver aliasResolver) {
        String res = spec.result() != null ? spec.result().item() : "unknown";
        return sanitizePath(res, aliasResolver) + "_" + Math.abs(spec.hashCode());
    }

    private static String sanitizePath(String str, AliasResolver aliasResolver) {
        if (str == null) return "unknown";
        String resolved = aliasResolver.resolveString(str);
        return resolved.toLowerCase(Locale.ROOT)
                .replace(":", "_")
                .replaceAll("[^a-z0-9/._-]", "_");
    }

    private static int getStringIndex(String str, List<String> stringPool, Map<String, Integer> stringIndexMap) {
        if (str == null) str = "";
        return stringIndexMap.computeIfAbsent(str, s -> {
            int idx = stringPool.size();
            stringPool.add(s);
            return idx;
        });
    }

    private static Set<String> findRuntimeCategories(Map<String, byte[]> rawCategoryData) {
        Set<String> runtimeCategories = new HashSet<>();
        for (Map.Entry<String, byte[]> entry : rawCategoryData.entrySet()) {
            String source = new String(entry.getValue(), StandardCharsets.UTF_8);
            if (source.contains("\"forge:conditional\"")
                    || source.contains("\"conditions\"")
                    || source.contains("\"botanypots:crop\"")) {
                runtimeCategories.add(entry.getKey());
            }
        }
        return runtimeCategories;
    }

    public static void writeAtomically(Path targetFile, byte[] data) throws IOException {
        Path parent = targetFile.toAbsolutePath().getParent();
        if (parent == null) {
            throw new IOException("Target has no parent directory: " + targetFile);
        }
        Files.createDirectories(parent);
        Path temporaryFile = Files.createTempFile(parent, targetFile.getFileName().toString(), ".tmp");
        try {
            Files.write(temporaryFile, data);
            try {
                Files.move(temporaryFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    public static void main(String[] args) throws Exception {
        Path sourceDir = args.length > 0 ? Path.of(args[0]) : Path.of("src/main/resources/data/buildscape/recipes_pack");
        Path targetBundledCache = args.length > 1 ? Path.of(args[1]) : sourceDir.resolve("recipes.bscb");
        Path targetLocalCache = args.length > 2 ? Path.of(args[2]) : null;

        // Read sources and compute live SHA-256 hash
        Map<String, byte[]> rawCategoryData = new LinkedHashMap<>();
        for (String category : CATEGORIES) {
            Path sourceFile = sourceDir.resolve(category + ".json");
            if (Files.isRegularFile(sourceFile)) {
                rawCategoryData.put(category, Files.readAllBytes(sourceFile));
            }
        }
        String expectedHash = computeSourceHash(CATEGORIES, rawCategoryData);

        boolean bundledValid = isCacheValid(targetBundledCache, expectedHash);
        boolean localValid = targetLocalCache == null || isCacheValid(targetLocalCache, expectedHash);

        if (bundledValid && localValid) {
            System.out.println("Recipe cache is already up to date with source hash: " + expectedHash + ". Skipping rebuild.");
            return;
        }

        byte[] cacheBytes;
        if (bundledValid) {
            cacheBytes = Files.readAllBytes(targetBundledCache);
        } else {
            // Compile binary cache from JSON sources only when sources or hash changed
            cacheBytes = generateCache(sourceDir);
            writeAtomically(targetBundledCache, cacheBytes);
            System.out.println("Generated bundled binary recipe cache: " + targetBundledCache.toAbsolutePath() + " (" + cacheBytes.length + " bytes)");
        }

        if (targetLocalCache != null && !localValid) {
            try {
                writeAtomically(targetLocalCache, cacheBytes);
                System.out.println("Synchronized local runtime cache: " + targetLocalCache.toAbsolutePath());
            } catch (Exception e) {
                System.out.println("Note: Local runtime cache directory not present or skipped: " + e.getMessage());
            }
        }

        // Validate written cache matches computed source hash
        if (!isCacheValid(targetBundledCache, expectedHash)) {
            throw new IllegalStateException("Integrity check failed: Written bundled cache does not match the source hash!");
        }
        System.out.println("Integrity verified: " + targetBundledCache.toAbsolutePath() + " matches live source hash " + expectedHash);
    }
}
