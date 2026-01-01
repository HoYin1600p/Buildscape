package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.BuildScape;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Configuration manager for equipped cosmetics.
 * Persists equipped cosmetics per player UUID.
 */
public class CosmeticsConfig {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    
    private static CosmeticsConfig INSTANCE;
    
    // Map of player UUID to their equipped cosmetics by slot
    // Slot: 0=head, 1=chest, 2=legs, 3=feet
    private Map<String, Map<Integer, String>> playerCosmetics = new HashMap<>();
    
    // Map of player UUID to cosmetic colors (cosmeticId -> hex color string)
    // Stores custom colors for particle trails and other colorable cosmetics
    private Map<String, Map<String, String>> playerCosmeticColors = new HashMap<>();
    
    private CosmeticsConfig() {
        load();
    }
    
    public static CosmeticsConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new CosmeticsConfig();
        }
        return INSTANCE;
    }
    
    private File getConfigFile() {
        String configPath = Paths.get("config", BuildScape.MODID).toString();
        File dir = new File(configPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "equipped-cosmetics.json");
    }
    
    /**
     * Load equipped cosmetics from config file.
     */
    public void load() {
        File file = getConfigFile();
        if (!file.exists()) {
            playerCosmetics = new HashMap<>();
            return;
        }
        
        try (FileReader reader = new FileReader(file)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> loaded = GSON.fromJson(reader, Map.class);
            if (loaded != null) {
                playerCosmetics = new HashMap<>();
                playerCosmeticColors = new HashMap<>();
                for (Map.Entry<String, Object> entry : loaded.entrySet()) {
                    String playerUuid = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> playerData = (Map<String, Object>) value;
                        
                        // Load equipped cosmetics by slot
                        Object cosmeticsObj = playerData.get("cosmetics");
                        if (cosmeticsObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> slotMap = (Map<String, Object>) cosmeticsObj;
                            Map<Integer, String> cosmeticsBySlot = new HashMap<>();
                            for (Map.Entry<String, Object> slotEntry : slotMap.entrySet()) {
                                try {
                                    int slot = Integer.parseInt(slotEntry.getKey());
                                    if (slotEntry.getValue() instanceof String) {
                                        cosmeticsBySlot.put(slot, (String) slotEntry.getValue());
                                    }
                                } catch (NumberFormatException e) {
                                    // Skip invalid slot
                                }
                            }
                            playerCosmetics.put(playerUuid, cosmeticsBySlot);
                        }
                        
                        // Load cosmetic colors
                        Object colorsObj = playerData.get("colors");
                        if (colorsObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> colorMap = (Map<String, Object>) colorsObj;
                            Map<String, String> cosmeticColors = new HashMap<>();
                            for (Map.Entry<String, Object> colorEntry : colorMap.entrySet()) {
                                if (colorEntry.getValue() instanceof String) {
                                    cosmeticColors.put(colorEntry.getKey(), (String) colorEntry.getValue());
                                }
                            }
                            playerCosmeticColors.put(playerUuid, cosmeticColors);
                        }
                    } else {
                        // Legacy format: just slot map
                        @SuppressWarnings("unchecked")
                        Map<String, Object> slotMap = (Map<String, Object>) value;
                        Map<Integer, String> cosmeticsBySlot = new HashMap<>();
                        for (Map.Entry<String, Object> slotEntry : slotMap.entrySet()) {
                            try {
                                int slot = Integer.parseInt(slotEntry.getKey());
                                if (slotEntry.getValue() instanceof String) {
                                    cosmeticsBySlot.put(slot, (String) slotEntry.getValue());
                                }
                            } catch (NumberFormatException e) {
                                // Skip invalid slot
                            }
                        }
                        playerCosmetics.put(playerUuid, cosmeticsBySlot);
                    }
                }
            }
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to load cosmetics config: " + e.getMessage());
            playerCosmetics = new HashMap<>();
            playerCosmeticColors = new HashMap<>();
        }
    }
    
    /**
     * Save equipped cosmetics to config file.
     */
    public void save() {
        File file = getConfigFile();
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Combine cosmetics and colors into a single structure
            Map<String, Object> combined = new HashMap<>();
            for (String uuid : playerCosmetics.keySet()) {
                Map<String, Object> playerData = new HashMap<>();
                playerData.put("cosmetics", playerCosmetics.get(uuid));
                if (playerCosmeticColors.containsKey(uuid)) {
                    playerData.put("colors", playerCosmeticColors.get(uuid));
                }
                combined.put(uuid, playerData);
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(combined, writer);
                writer.flush();
            }
            
            BuildScape.getLogger().debug("Saved cosmetics config to " + file.getAbsolutePath());
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to save cosmetics config: " + e.getMessage());
        }
    }
    
    /**
     * Get equipped cosmetics for a player.
     * Fallback to "global" if UUID not found.
     */
    public Map<Integer, String> getEquippedCosmetics(UUID playerUuid) {
        if (playerUuid == null) {
            return playerCosmetics.getOrDefault("global", new HashMap<>());
        }
        String uuidStr = playerUuid.toString();
        Map<Integer, String> cosmetics = playerCosmetics.get(uuidStr);
        if (cosmetics == null || cosmetics.isEmpty()) {
            // Fallback to global profile
            cosmetics = playerCosmetics.get("global");
        }
        return cosmetics != null ? new HashMap<>(cosmetics) : new HashMap<>();
    }
    
    /**
     * Set equipped cosmetics for a player.
     * Also updates "global" profile.
     */
    public void setEquippedCosmetics(UUID playerUuid, Map<Integer, String> cosmeticsBySlot) {
        if (playerUuid != null) {
            String uuidStr = playerUuid.toString();
            playerCosmetics.put(uuidStr, new HashMap<>(cosmeticsBySlot));
        }
        // Always update global profile for persistence across different UUIDs/SP
        playerCosmetics.put("global", new HashMap<>(cosmeticsBySlot));
        save();
    }
    
    /**
     * Equip a cosmetic to a specific slot for a player.
     * Also updates "global" profile.
     */
    public void equipCosmetic(UUID playerUuid, int slotIndex, String cosmeticId) {
        if (playerUuid != null) {
            String uuidStr = playerUuid.toString();
            Map<Integer, String> cosmetics = playerCosmetics.computeIfAbsent(uuidStr, k -> new HashMap<>());
            cosmetics.values().remove(cosmeticId);
            cosmetics.remove(slotIndex);
            if (cosmeticId != null && !cosmeticId.isEmpty()) {
                cosmetics.put(slotIndex, cosmeticId);
            }
        }
        
        // Update global profile
        Map<Integer, String> globalCosmetics = playerCosmetics.computeIfAbsent("global", k -> new HashMap<>());
        globalCosmetics.values().remove(cosmeticId);
        globalCosmetics.remove(slotIndex);
        if (cosmeticId != null && !cosmeticId.isEmpty()) {
            globalCosmetics.put(slotIndex, cosmeticId);
        }
        
        save();
    }
    
    /**
     * Unequip cosmetic from a specific slot for a player.
     * Also updates "global" profile.
     */
    public void unequipCosmetic(UUID playerUuid, int slotIndex) {
        if (playerUuid != null) {
            String uuidStr = playerUuid.toString();
            Map<Integer, String> cosmetics = playerCosmetics.get(uuidStr);
            if (cosmetics != null) {
                cosmetics.remove(slotIndex);
                if (cosmetics.isEmpty()) {
                    playerCosmetics.remove(uuidStr);
                }
            }
        }
        
        // Update global profile
        Map<Integer, String> globalCosmetics = playerCosmetics.get("global");
        if (globalCosmetics != null) {
            globalCosmetics.remove(slotIndex);
            if (globalCosmetics.isEmpty()) {
                playerCosmetics.remove("global");
            }
        }
        
        save();
    }
    
    /**
     * Get color for a cosmetic (hex string like "#FF0000").
     * Fallback to "global" if UUID not found.
     */
    public String getCosmeticColor(UUID playerUuid, String cosmeticId) {
        if (cosmeticId == null) return null;
        
        if (playerUuid != null) {
            String uuidStr = playerUuid.toString();
            Map<String, String> colors = playerCosmeticColors.get(uuidStr);
            if (colors != null && colors.containsKey(cosmeticId)) {
                return colors.get(cosmeticId);
            }
        }
        
        // Fallback to global profile
        Map<String, String> globalColors = playerCosmeticColors.get("global");
        if (globalColors != null) {
            return globalColors.get(cosmeticId);
        }
        
        return null;
    }
    
    /**
     * Set color for a cosmetic (hex string like "#FF0000").
     * Also updates "global" profile.
     */
    public void setCosmeticColor(UUID playerUuid, String cosmeticId, String hexColor) {
        if (cosmeticId == null) return;
        
        if (playerUuid != null) {
            String uuidStr = playerUuid.toString();
            Map<String, String> colors = playerCosmeticColors.computeIfAbsent(uuidStr, k -> new HashMap<>());
            if (hexColor != null && !hexColor.isEmpty()) {
                colors.put(cosmeticId, hexColor);
            } else {
                colors.remove(cosmeticId);
            }
        }
        
        // Update global profile
        Map<String, String> globalColors = playerCosmeticColors.computeIfAbsent("global", k -> new HashMap<>());
        if (hexColor != null && !hexColor.isEmpty()) {
            globalColors.put(cosmeticId, hexColor);
        } else {
            globalColors.remove(cosmeticId);
        }
        
        save();
    }
    
    /**
     * Check if a cosmetic supports color customization.
     */
    public boolean supportsColor(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return false;
        }
        String idLower = cosmeticId.toLowerCase();
        // Particle trails support colors
        return idLower.contains("particle") && idLower.contains("trail");
    }
}

