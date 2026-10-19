package com.kingodogo.buildscape.core;

import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class RecipeIdMigration {
    private static final String TABLE = "/data/buildscape/recipes_pack/recipe_id_migration_405.txt";

    private RecipeIdMigration() {
    }

    public static ResourceLocation translate(ResourceLocation id) {
        if (!"buildscape".equals(id.getNamespace()) || !id.getPath().startsWith("buildscape_autogen/")) {
            return id;
        }
        return Holder.MAPPINGS.getOrDefault(id, id);
    }

    private static Map<ResourceLocation, ResourceLocation> loadMappings() {
        InputStream stream = RecipeIdMigration.class.getResourceAsStream(TABLE);
        if (stream == null) {
            throw new IllegalStateException("Missing recipe id migration table: " + TABLE);
        }
        Map<ResourceLocation, ResourceLocation> mappings = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] ids = line.split("\\s+");
                ResourceLocation oldId = ids.length == 2 ? ResourceLocation.tryParse(ids[0]) : null;
                ResourceLocation newId = ids.length == 2 ? ResourceLocation.tryParse(ids[1]) : null;
                if (oldId == null || newId == null || !"buildscape".equals(oldId.getNamespace())
                        || !oldId.getPath().startsWith("buildscape_autogen/")
                        || !"buildscape".equals(newId.getNamespace())) {
                    throw new IllegalStateException("Invalid recipe id migration at line " + lineNumber);
                }
                if (mappings.putIfAbsent(oldId, newId) != null) {
                    throw new IllegalStateException("Duplicate recipe id migration: " + oldId);
                }
            }
            return Map.copyOf(mappings);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read recipe id migration table", e);
        }
    }

    private static final class Holder {
        private static final Map<ResourceLocation, ResourceLocation> MAPPINGS = loadMappings();
    }
}
