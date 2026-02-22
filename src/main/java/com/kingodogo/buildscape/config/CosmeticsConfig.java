package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.BuildScape;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Configuration manager for equipped cosmetics.
 * Persists data in a private 'buildscape/data' directory to keep the config folder clean.
 */
public class CosmeticsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static CosmeticsConfig INSTANCE;

    // Cache of player UUID string to their equipped cosmetics by slot
    private final Map<String, Map<Integer, String>> playerCosmetics = new HashMap<>();

    // Cache of player UUID string to cosmetic colors (cosmeticId -> hex color string)
    private final Map<String, Map<String, String>> playerCosmeticColors = new HashMap<>();

    // Color picker window position
    private Integer colorPickerX = null;
    private Integer colorPickerY = null;

    private CosmeticsConfig() {
        // 1. First, ensure our private data directory exists
        File dataDir = getDataDir();
        if (!dataDir.exists()) dataDir.mkdirs();

        // 2. Migrate from legacy locations
        migrateLegacyData();
        
        // 3. Load global settings from private storage
        loadGlobalSettings();
    }

    public static CosmeticsConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new CosmeticsConfig();
        }
        return INSTANCE;
    }

    /**
     * @return The "private" data directory for BuildScape (not in config).
     */
    private File getDataDir() {
        return FMLPaths.GAMEDIR.get().resolve("buildscape").resolve("data").toFile();
    }

    private File getPlayerFile(UUID playerUuid) {
        String fileName = (playerUuid != null ? playerUuid.toString() : "global") + "-cosmetic.dat";
        return new File(getDataDir(), fileName);
    }

    private File getGlobalSettingsFile() {
        return new File(getDataDir(), "global-settings.dat");
    }

    /**
     * Handles migration from BOTH the old JSON config AND the temporary NBT config location.
     */
    private void migrateLegacyData() {
        Path legacyConfigDir = FMLPaths.CONFIGDIR.get().resolve(BuildScape.MODID);
        File legacyJson = legacyConfigDir.resolve("equipped-cosmetics.json").toFile();
        File legacyDataDir = legacyConfigDir.toFile();

        // 1. Move any .dat files from config/buildscape/ to buildscape/data/
        if (legacyDataDir.exists() && legacyDataDir.isDirectory()) {
            File[] files = legacyDataDir.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File oldFile : files) {
                    try {
                        File newFile = new File(getDataDir(), oldFile.getName());
                        if (!newFile.exists()) {
                            Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            BuildScape.getLogger().info("CosmeticsConfig: Relocated " + oldFile.getName() + " to private storage.");
                        } else {
                            oldFile.delete(); // Already exists in new location
                        }
                    } catch (Exception e) {
                        BuildScape.getLogger().error("CosmeticsConfig: Failed to relocate " + oldFile.getName(), e);
                    }
                }
            }
        }

        // 2. Migrate from the way-old JSON format if it still exists
        if (legacyJson.exists()) {
            BuildScape.getLogger().info("CosmeticsConfig: Found legacy JSON. Migrating to private NBT storage...");
            try (FileReader reader = new FileReader(legacyJson)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> loaded = GSON.fromJson(reader, Map.class);
                if (loaded != null) {
                    processLegacyJsonMap(loaded);
                }
                
                // Backup or delete old JSON
                File backup = new File(legacyJson.getAbsolutePath() + ".bak");
                if (!legacyJson.renameTo(backup)) legacyJson.delete();
                
            } catch (Exception e) {
                BuildScape.getLogger().error("CosmeticsConfig: Legacy JSON migration failed!", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processLegacyJsonMap(Map<String, Object> loaded) {
        for (Map.Entry<String, Object> entry : loaded.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            if (key.equals("global_settings")) {
                if (val instanceof Map) {
                    Map<String, Object> globalData = (Map<String, Object>) val;
                    if (globalData.containsKey("colorPickerX") && globalData.get("colorPickerX") instanceof Number)
                        colorPickerX = ((Number) globalData.get("colorPickerX")).intValue();
                    if (globalData.containsKey("colorPickerY") && globalData.get("colorPickerY") instanceof Number)
                        colorPickerY = ((Number) globalData.get("colorPickerY")).intValue();
                }
                continue;
            }

            UUID uuid = null;
            if (!key.equals("global")) {
                try { uuid = UUID.fromString(key); } catch (Exception ignored) {}
            }

            Map<Integer, String> cosmetics = new HashMap<>();
            Map<String, String> colors = new HashMap<>();

            if (val instanceof Map) {
                Map<String, Object> playerData = (Map<String, Object>) val;
                
                Object cosObj = playerData.get("cosmetics");
                if (cosObj instanceof Map) {
                    for (Map.Entry<String, Object> cosEntry : ((Map<String, Object>) cosObj).entrySet()) {
                        try {
                            int slot = Integer.parseInt(cosEntry.getKey());
                            if (cosEntry.getValue() instanceof String) cosmetics.put(slot, (String) cosEntry.getValue());
                        } catch (Exception ignored) {}
                    }
                }

                Object colObj = playerData.get("colors");
                if (colObj instanceof Map) {
                    for (Map.Entry<String, Object> colEntry : ((Map<String, Object>) colObj).entrySet()) {
                        if (colEntry.getValue() instanceof String) colors.put(colEntry.getKey(), (String) colEntry.getValue());
                    }
                }
            }

            playerCosmetics.put(key, cosmetics);
            playerCosmeticColors.put(key, colors);
            savePlayer(uuid);
        }
        saveGlobalSettings();
    }

    /**
     * Internal method to load player data from their specific NBT file.
     */
    private void loadPlayer(UUID playerUuid) {
        File file = getPlayerFile(playerUuid);
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";

        playerCosmetics.putIfAbsent(uuidStr, new HashMap<>());
        playerCosmeticColors.putIfAbsent(uuidStr, new HashMap<>());

        if (!file.exists()) return;

        try {
            CompoundTag nbt = NbtIo.readCompressed(file);
            if (nbt != null) {
                if (playerUuid != null && nbt.hasUUID("player_uuid")) {
                    UUID storedUuid = nbt.getUUID("player_uuid");
                    if (!storedUuid.equals(playerUuid)) {
                        BuildScape.getLogger().error("CosmeticsConfig: SECURITY MISMATCH for " + file.getName());
                        return;
                    }
                }

                Map<Integer, String> cosmetics = new HashMap<>();
                if (nbt.contains("equipped_cosmetics", 10)) {
                    CompoundTag equipped = nbt.getCompound("equipped_cosmetics");
                    for (String key : equipped.getAllKeys()) {
                        try { cosmetics.put(Integer.parseInt(key), equipped.getString(key)); } catch (Exception ignored) {}
                    }
                }
                playerCosmetics.put(uuidStr, cosmetics);

                Map<String, String> colors = new HashMap<>();
                if (nbt.contains("cosmetic_colors", 10)) {
                    CompoundTag colorsTag = nbt.getCompound("cosmetic_colors");
                    for (String key : colorsTag.getAllKeys()) {
                        colors.put(key, colorsTag.getString(key));
                    }
                }
                playerCosmeticColors.put(uuidStr, colors);
            }
        } catch (Exception e) {
            BuildScape.getLogger().error("CosmeticsConfig: Failed to read data for " + uuidStr, e);
        }
    }

    private void savePlayer(UUID playerUuid) {
        File file = getPlayerFile(playerUuid);
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";

        Map<Integer, String> cosmetics = playerCosmetics.get(uuidStr);
        Map<String, String> colors = playerCosmeticColors.get(uuidStr);

        CompoundTag nbt = new CompoundTag();
        if (playerUuid != null) nbt.putUUID("player_uuid", playerUuid);

        CompoundTag equippedTag = new CompoundTag();
        if (cosmetics != null) {
            for (Map.Entry<Integer, String> entry : cosmetics.entrySet()) {
                equippedTag.putString(entry.getKey().toString(), entry.getValue());
            }
        }
        nbt.put("equipped_cosmetics", equippedTag);

        CompoundTag colorsTag = new CompoundTag();
        if (colors != null) {
            for (Map.Entry<String, String> entry : colors.entrySet()) {
                colorsTag.putString(entry.getKey(), entry.getValue());
            }
        }
        nbt.put("cosmetic_colors", colorsTag);

        try {
            NbtIo.writeCompressed(nbt, file);
        } catch (Exception e) {
            BuildScape.getLogger().error("CosmeticsConfig: Failed to write data for " + uuidStr, e);
        }
    }

    private void loadGlobalSettings() {
        File file = getGlobalSettingsFile();
        if (file.exists()) {
            try {
                CompoundTag nbt = NbtIo.readCompressed(file);
                if (nbt.contains("colorPickerX")) colorPickerX = nbt.getInt("colorPickerX");
                if (nbt.contains("colorPickerY")) colorPickerY = nbt.getInt("colorPickerY");
            } catch (Exception ignored) {}
        }
    }

    private void saveGlobalSettings() {
        File file = getGlobalSettingsFile();
        CompoundTag nbt = new CompoundTag();
        if (colorPickerX != null) nbt.putInt("colorPickerX", colorPickerX);
        if (colorPickerY != null) nbt.putInt("colorPickerY", colorPickerY);

        try {
            NbtIo.writeCompressed(nbt, file);
        } catch (Exception ignored) {}
    }

    public Map<Integer, String> getEquippedCosmetics(UUID playerUuid) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";
        if (!playerCosmetics.containsKey(uuidStr)) loadPlayer(playerUuid);
        Map<Integer, String> cosmetics = playerCosmetics.get(uuidStr);
        if (playerUuid != null && (cosmetics == null || cosmetics.isEmpty())) return getEquippedCosmetics(null); 
        return cosmetics != null ? new HashMap<>(cosmetics) : new HashMap<>();
    }

    public void setEquippedCosmetics(UUID playerUuid, Map<Integer, String> cosmeticsBySlot) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";
        playerCosmetics.put(uuidStr, new HashMap<>(cosmeticsBySlot));
        savePlayer(playerUuid);
        playerCosmetics.put("global", new HashMap<>(cosmeticsBySlot));
        savePlayer(null);
    }

    public void equipCosmetic(UUID playerUuid, int slotIndex, String cosmeticId) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";
        if (!playerCosmetics.containsKey(uuidStr)) loadPlayer(playerUuid);
        if (!playerCosmetics.containsKey("global")) loadPlayer(null);

        updateMap(playerCosmetics.computeIfAbsent(uuidStr, k -> new HashMap<>()), slotIndex, cosmeticId);
        savePlayer(playerUuid);
        updateMap(playerCosmetics.computeIfAbsent("global", k -> new HashMap<>()), slotIndex, cosmeticId);
        savePlayer(null);
    }

    private void updateMap(Map<Integer, String> map, int slot, String id) {
        map.values().remove(id);
        map.remove(slot);
        if (id != null && !id.isEmpty()) map.put(slot, id);
    }

    public void unequipCosmetic(UUID playerUuid, int slotIndex) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";
        if (!playerCosmetics.containsKey(uuidStr)) loadPlayer(playerUuid);
        if (!playerCosmetics.containsKey("global")) loadPlayer(null);

        Map<Integer, String> playerMap = playerCosmetics.get(uuidStr);
        if (playerMap != null) {
            playerMap.remove(slotIndex);
            savePlayer(playerUuid);
        }

        Map<Integer, String> globalMap = playerCosmetics.get("global");
        if (globalMap != null) {
            globalMap.remove(slotIndex);
            savePlayer(null);
        }
    }

    public String getCosmeticColor(UUID playerUuid, String cosmeticId) {
        if (cosmeticId == null) return null;
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";
        if (!playerCosmeticColors.containsKey(uuidStr)) loadPlayer(playerUuid);
        Map<String, String> colors = playerCosmeticColors.get(uuidStr);
        if (colors != null && colors.containsKey(cosmeticId)) return colors.get(cosmeticId);
        if (!playerCosmeticColors.containsKey("global")) loadPlayer(null);
        Map<String, String> globalColors = playerCosmeticColors.get("global");
        return globalColors != null ? globalColors.get(cosmeticId) : null;
    }

    public void setCosmeticColor(UUID playerUuid, String cosmeticId, String hexColor) {
        if (cosmeticId == null) return;
        String uuidStr = playerUuid != null ? playerUuid.toString() : "global";
        if (!playerCosmeticColors.containsKey(uuidStr)) loadPlayer(playerUuid);
        if (!playerCosmeticColors.containsKey("global")) loadPlayer(null);

        Map<String, String> playerMap = playerCosmeticColors.computeIfAbsent(uuidStr, k -> new HashMap<>());
        if (hexColor != null && !hexColor.isEmpty()) playerMap.put(cosmeticId, hexColor);
        else playerMap.remove(cosmeticId);
        savePlayer(playerUuid);

        Map<String, String> globalMap = playerCosmeticColors.computeIfAbsent("global", k -> new HashMap<>());
        if (hexColor != null && !hexColor.isEmpty()) globalMap.put(cosmeticId, hexColor);
        else globalMap.remove(cosmeticId);
        savePlayer(null);
    }

    public boolean supportsColor(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) return false;
        String idLower = cosmeticId.toLowerCase();
        return idLower.contains("particle") && idLower.contains("trail");
    }

    public Integer getColorPickerX() { return colorPickerX; }
    public Integer getColorPickerY() { return colorPickerY; }

    public void setColorPickerPosition(int x, int y) {
        this.colorPickerX = x;
        this.colorPickerY = y;
        saveGlobalSettings();
    }

    public void clearColorPickerPosition() {
        this.colorPickerX = null;
        this.colorPickerY = null;
        saveGlobalSettings();
    }
}
