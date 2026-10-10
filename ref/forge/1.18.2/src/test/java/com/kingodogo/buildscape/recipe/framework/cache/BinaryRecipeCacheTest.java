package com.kingodogo.buildscape.recipe.framework.cache;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BinaryRecipeCacheTest {

    public static void main(String[] args) throws Exception {
        String[] categories = {"first", "second"};
        Map<String, byte[]> first = new LinkedHashMap<>();
        first.put("first", new byte[]{1});
        first.put("second", new byte[]{2, 3});
        Map<String, byte[]> second = new LinkedHashMap<>();
        second.put("first", new byte[]{1, 2});
        second.put("second", new byte[]{3});
        Map<String, byte[]> missing = new LinkedHashMap<>();
        missing.put("first", new byte[0]);
        Map<String, byte[]> present = new LinkedHashMap<>();
        present.put("first", new byte[0]);
        present.put("second", new byte[0]);

        require(!BinaryRecipeCache.computeSourceHash(categories, first)
                .equals(BinaryRecipeCache.computeSourceHash(categories, second)),
                "Category boundaries were not included in the cache key");
        require(!BinaryRecipeCache.computeSourceHash(categories, missing)
                .equals(BinaryRecipeCache.computeSourceHash(categories, present)),
                "Missing category state was not included in the cache key");

        byte[] malformed = BinaryRecipeCache.serializeCache("hash", List.of());
        int poolSizeOffset = Integer.BYTES * 2 + Short.BYTES + "hash".getBytes(StandardCharsets.UTF_8).length;
        ByteBuffer.wrap(malformed).putInt(poolSizeOffset, Integer.MAX_VALUE);
        require(BinaryRecipeCache.loadCacheFromStream(new ByteArrayInputStream(malformed), "hash").isEmpty(),
                "Malformed cache was not rejected");

        Path directory = Files.createTempDirectory("buildscape-cache-test");
        Path cacheFile = directory.resolve("recipes.bscb");
        try {
            BinaryRecipeCache.saveCacheToFile(cacheFile, "hash", List.of());
            require(BinaryRecipeCache.isCacheValid(cacheFile, "hash"), "Atomically written cache was not valid");
            require(!BinaryRecipeCache.isCacheValid(cacheFile, "other"), "Cache accepted the wrong source hash");
        } finally {
            Files.deleteIfExists(cacheFile);
            Files.deleteIfExists(directory);
        }
        System.out.println("Binary recipe cache: 5 checks passed.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
