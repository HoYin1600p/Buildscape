package com.kingodogo.buildscape.recipe.framework.cache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BundledRecipeCacheVerify {

    private static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected source directory and bundled cache paths");
        }

        Path sourceDirectory = Path.of(args[0]);
        Path bundledCache = Path.of(args[1]);
        Map<String, byte[]> sourceData = new LinkedHashMap<>();
        for (String category : CATEGORIES) {
            Path sourceFile = sourceDirectory.resolve(category + ".json");
            if (Files.isRegularFile(sourceFile)) {
                sourceData.put(category, Files.readAllBytes(sourceFile));
            }
        }

        String expectedHash = BinaryRecipeCache.computeSourceHash(CATEGORIES, sourceData);
        if (!BinaryRecipeCache.isCacheValid(bundledCache, expectedHash)) {
            throw new IllegalStateException("The bundled recipe cache does not match the compact sources");
        }

        System.out.println("Bundled recipe cache verified: " + bundledCache.toAbsolutePath());
    }
}
