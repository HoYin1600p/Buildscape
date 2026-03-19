package com.kingodogo.buildscape.variantengine.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Caches scan results from VariantRegistrar to disk.
 * On load, computes a fingerprint of the mod list. If it matches the saved
 * fingerprint, it returns cached base-block IDs so that detectFamily() can be
 * skipped entirely, dramatically reducing startup time.
 */
public class VariantScanCache {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CACHE_FILE = "variant_scan_cache.json";

    /** Holds the blocks that should have variants generated. key=baseBlockId, value=[slab genId, stair genId (or null)] */
    public static final class CacheData {
        // Maps baseBlockId -> Set of shapes that should be generated (as strings e.g. "VERTICAL_SLAB")
        public final Map<String, Set<String>> baseToShapes = new LinkedHashMap<>();
        // Maps baseBlockId -> slab companion id (may be null)
        public final Map<String, String> baseToSlabCompanion = new LinkedHashMap<>();
        // Maps baseBlockId -> stair companion id (may be null)
        public final Map<String, String> baseToStairCompanion = new LinkedHashMap<>();
        public boolean valid = false;
    }

    public static String computeFingerprint() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();

            // 1. Mod list + versions
            List<String> mods = ModList.get().getMods().stream()
                    .map(m -> m.getModId() + ":" + m.getVersion())
                    .sorted()
                    .collect(Collectors.toList());
            sb.append("mods:");
            for (String m : mods) sb.append(m).append("|");

            // 2. VerticalConfig state — any blocklist/allowlist change invalidates the cache
            com.kingodogo.buildscape.config.VerticalConfig cfg =
                    com.kingodogo.buildscape.config.VerticalConfig.get();

            List<String> allowedFams = new ArrayList<>(cfg.getAllowedFamilies());
            Collections.sort(allowedFams);
            sb.append("allowedFam:");
            for (String s : allowedFams) sb.append(s).append("|");

            List<String> blockedFams = new ArrayList<>(cfg.getBlocklistedFamilies());
            Collections.sort(blockedFams);
            sb.append("blockedFam:");
            for (String s : blockedFams) sb.append(s).append("|");

            List<String> allowedMods = new ArrayList<>(cfg.getAllowedMods());
            Collections.sort(allowedMods);
            sb.append("allowedMod:");
            for (String s : allowedMods) sb.append(s).append("|");

            List<String> blockedMods = new ArrayList<>(cfg.getBlocklistedMods());
            Collections.sort(blockedMods);
            sb.append("blockedMod:");
            for (String s : blockedMods) sb.append(s).append("|");

            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "no-hash";
        }
    }

    private static File getCacheFile() {
        File dir = FMLPaths.GAMEDIR.get().resolve("buildscape").resolve("data").toFile();
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, CACHE_FILE);
    }

    /**
     * Try to load the cache from disk. Returns a valid CacheData if the
     * fingerprint matches (i.e. no mods changed). Returns null otherwise.
     */
    public static CacheData tryLoad(String currentFingerprint) {
        File file = getCacheFile();
        if (!file.exists()) return null;
        try {
            String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            JsonObject root = GSON.fromJson(json, JsonObject.class);
            String savedFingerprint = root.has("fingerprint") ? root.get("fingerprint").getAsString() : "";

            if (!savedFingerprint.equals(currentFingerprint)) {
                BuildScape.LOGGER.info("BuildScape: Mods or vertical config changed (fingerprint mismatch). Full rescan required.");
                return null;
            }

            CacheData data = new CacheData();
            JsonArray entries = root.getAsJsonArray("entries");
            for (int i = 0; i < entries.size(); i++) {
                JsonObject entry = entries.get(i).getAsJsonObject();
                String baseId = entry.get("baseId").getAsString();

                Set<String> shapes = new LinkedHashSet<>();
                JsonArray shapesArr = entry.getAsJsonArray("shapes");
                for (int s = 0; s < shapesArr.size(); s++) {
                    shapes.add(shapesArr.get(s).getAsString());
                }
                data.baseToShapes.put(baseId, shapes);

                if (entry.has("slabCompanion") && !entry.get("slabCompanion").isJsonNull()) {
                    data.baseToSlabCompanion.put(baseId, entry.get("slabCompanion").getAsString());
                }
                if (entry.has("stairCompanion") && !entry.get("stairCompanion").isJsonNull()) {
                    data.baseToStairCompanion.put(baseId, entry.get("stairCompanion").getAsString());
                }
            }
            data.valid = true;
            BuildScape.LOGGER.info("BuildScape: Loaded scan cache with {} base blocks. Skipping full rescan.", data.baseToShapes.size());
            return data;
        } catch (Exception e) {
            BuildScape.LOGGER.warn("BuildScape: Failed to read scan cache, will do full scan: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Persist the scan results to disk with the given fingerprint.
     */
    public static void save(String fingerprint, CacheData data) {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("fingerprint", fingerprint);

            JsonArray entries = new JsonArray();
            for (Map.Entry<String, Set<String>> e : data.baseToShapes.entrySet()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("baseId", e.getKey());

                JsonArray shapesArr = new JsonArray();
                for (String shape : e.getValue()) shapesArr.add(shape);
                entry.add("shapes", shapesArr);

                String slabC = data.baseToSlabCompanion.get(e.getKey());
                entry.addProperty("slabCompanion", slabC);

                String stairC = data.baseToStairCompanion.get(e.getKey());
                entry.addProperty("stairCompanion", stairC);

                entries.add(entry);
            }
            root.add("entries", entries);
            Files.writeString(getCacheFile().toPath(), GSON.toJson(root), StandardCharsets.UTF_8);
            BuildScape.LOGGER.info("BuildScape: Scan cache saved ({} base blocks).", data.baseToShapes.size());
        } catch (IOException e) {
            BuildScape.LOGGER.warn("BuildScape: Failed to save scan cache: {}", e.getMessage());
        }
    }
}
