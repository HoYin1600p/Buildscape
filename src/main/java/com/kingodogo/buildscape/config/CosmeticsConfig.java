package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.io.AtomicFiles;
import com.kingodogo.buildscape.core.io.Json;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmeticsConfig {
    private static final Gson GSON = Json.PRETTY;
    /** Key and file-name part for settings that belong to no player. */
    public static final String GLOBAL_SCOPE = "global";
    private static CosmeticsConfig INSTANCE;
    private final Path dataDirectory;
    private final Path legacyConfigDirectory;

    private final Map<String, Map<Integer, String>> playerCosmetics = new HashMap<>();

    private final Map<String, Map<String, String>> playerCosmeticColors = new HashMap<>();

    private final Map<String, Boolean> playerCreativeTreeBreaker = new HashMap<>();

    private final Map<String, Boolean> playerShulkerPreview = new HashMap<>();

    private Integer colorPickerX = null;
    private Integer colorPickerY = null;

    private CosmeticsConfig() {
        this(FMLPaths.GAMEDIR.get().resolve("buildscape").resolve("data"),
                FMLPaths.CONFIGDIR.get().resolve(BuildScape.MODID));
    }

    CosmeticsConfig(Path dataDirectory, Path legacyConfigDirectory) {
        this.dataDirectory = dataDirectory;
        this.legacyConfigDirectory = legacyConfigDirectory;
        File dataDir = getDataDir();
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        migrateLegacyData();

        loadGlobalSettings();
    }

    public static CosmeticsConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new CosmeticsConfig();
        }
        return INSTANCE;
    }

    private File getDataDir() {
        return dataDirectory.toFile();
    }

    private File getPlayerFile(UUID playerUuid) {
        String fileName = (playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE) + "-cosmetic.dat";
        return new File(getDataDir(), fileName);
    }

    private File getGlobalSettingsFile() {
        return new File(getDataDir(), "global-settings.dat");
    }

    private void migrateLegacyData() {
        Path legacyConfigDir = legacyConfigDirectory;
        File legacyJson = legacyConfigDir.resolve("equipped-cosmetics.json").toFile();
        File legacyDataDir = legacyConfigDir.toFile();

        if (legacyDataDir.exists() && legacyDataDir.isDirectory()) {
            File[] files = legacyDataDir.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File oldFile : files) {
                    try {
                        File newFile = new File(getDataDir(), oldFile.getName());
                        if (!newFile.exists()) {
                            Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Files.move(oldFile.toPath(),
                                    oldFile.toPath().resolveSibling(oldFile.getName() + ".migrated"));
                        }
                    } catch (Exception e) {
                        BuildScape.getLogger().error("CosmeticsConfig: Failed to relocate {}", oldFile.getName(), e);
                    }
                }
            }
        }

        if (legacyJson.exists()) {
            boolean loadedSuccessfully = false;
            try {
                String source = ConfigLayoutIO.readText(legacyJson.toPath());
                if (ConfigLayoutIO.protectInvalidEncoding(legacyJson.toPath())) {
                    return;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> loaded = GSON.fromJson(source, Map.class);
                if (loaded != null) {
                    processLegacyJsonMap(loaded);
                }
                loadedSuccessfully = true;
            } catch (Exception e) {
                BuildScape.getLogger().warn("CosmeticsConfig: Unable to read the legacy JSON cosmetics", e);
            }

            if (loadedSuccessfully) {
                Path legacyPath = legacyJson.toPath();
                Path backupPath = legacyPath.resolveSibling(legacyJson.getName() + ".bak");
                try {
                    int suffix = 1;
                    while (true) {
                        try {
                            Files.move(legacyPath, backupPath);
                            break;
                        } catch (FileAlreadyExistsException e) {
                            backupPath = legacyPath.resolveSibling(legacyJson.getName() + ".bak." + suffix++);
                        }
                    }
                } catch (Exception e) {
                    BuildScape.getLogger().warn("Unable to retain legacy cosmetics backup: {}", legacyPath, e);
                }
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
                    if (globalData.containsKey("colorPickerX")
                            && globalData.get(NbtKeys.COLOR_PICKER_X) instanceof Number) {
                        colorPickerX = ((Number) globalData.get(NbtKeys.COLOR_PICKER_X)).intValue();
                    }
                    if (globalData.containsKey("colorPickerY")
                            && globalData.get(NbtKeys.COLOR_PICKER_Y) instanceof Number) {
                        colorPickerY = ((Number) globalData.get(NbtKeys.COLOR_PICKER_Y)).intValue();
                    }
                }
                continue;
            }

            UUID uuid = null;
            if (!key.equals(GLOBAL_SCOPE)) {
                try {
                    uuid = UUID.fromString(key);
                } catch (IllegalArgumentException ignored) {
                    // the key is not a UUID; the entry stays without an owner
                }
            }

            if (getPlayerFile(uuid).exists()) {
                continue;
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
                            if (cosEntry.getValue() instanceof String) {
                                cosmetics.put(slot, (String) cosEntry.getValue());
                            }
                        } catch (NumberFormatException ignored) {
                            // skip entries whose slot key is not a number
                        }
                    }
                }

                Object colObj = playerData.get("colors");
                if (colObj instanceof Map) {
                    for (Map.Entry<String, Object> colEntry : ((Map<String, Object>) colObj).entrySet()) {
                        if (colEntry.getValue() instanceof String) {
                            colors.put(colEntry.getKey(), (String) colEntry.getValue());
                        }
                    }
                }
            }

            playerCosmetics.put(key, cosmetics);
            playerCosmeticColors.put(key, colors);
            savePlayer(uuid);
        }
        saveGlobalSettings();
    }

    private void loadPlayer(UUID playerUuid) {
        File file = getPlayerFile(playerUuid);
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;

        if (!file.exists()) {
            playerCosmetics.putIfAbsent(uuidStr, null);
            playerCosmeticColors.putIfAbsent(uuidStr, null);
            playerCreativeTreeBreaker.putIfAbsent(uuidStr, false);
            playerShulkerPreview.putIfAbsent(uuidStr, true);
            return;
        }

        try {
            CompoundTag nbt = NbtIo.readCompressed(file);
            if (nbt != null) {
                if (playerUuid != null && nbt.hasUUID(NbtKeys.PLAYER_UUID)) {
                    UUID storedUuid = nbt.getUUID(NbtKeys.PLAYER_UUID);
                    if (!storedUuid.equals(playerUuid)) {
                        BuildScape.getLogger().error("CosmeticsConfig: SECURITY MISMATCH for {}", file.getName());
                        PillarParticleConfig.backupUnreadableFile(file);
                        playerCosmetics.putIfAbsent(uuidStr, null);
                        playerCosmeticColors.putIfAbsent(uuidStr, null);
                        playerCreativeTreeBreaker.putIfAbsent(uuidStr, false);
                        playerShulkerPreview.putIfAbsent(uuidStr, true);
                        return;
                    }
                }

                Map<Integer, String> cosmetics = new HashMap<>();
                if (nbt.contains(NbtKeys.EQUIPPED_COSMETICS, 10)) {
                    CompoundTag equipped = nbt.getCompound(NbtKeys.EQUIPPED_COSMETICS);
                    for (String key : equipped.getAllKeys()) {
                        try {
                            cosmetics.put(Integer.parseInt(key), equipped.getString(key));
                        } catch (NumberFormatException ignored) {
                            // skip entries whose slot key is not a number
                        }
                    }
                }
                playerCosmetics.put(uuidStr, cosmetics);

                Map<String, String> colors = new HashMap<>();
                if (nbt.contains(NbtKeys.COSMETIC_COLORS, 10)) {
                    CompoundTag colorsTag = nbt.getCompound(NbtKeys.COSMETIC_COLORS);
                    for (String key : colorsTag.getAllKeys()) {
                        colors.put(key, colorsTag.getString(key));
                    }
                }
                playerCosmeticColors.put(uuidStr, colors);

                if (nbt.contains(NbtKeys.CREATIVE_TREE_BREAKER)) {
                    playerCreativeTreeBreaker.put(uuidStr, nbt.getBoolean(NbtKeys.CREATIVE_TREE_BREAKER));
                } else {
                    playerCreativeTreeBreaker.put(uuidStr, false);
                }

                if (nbt.contains(NbtKeys.SHULKER_PREVIEW)) {
                    playerShulkerPreview.put(uuidStr, nbt.getBoolean(NbtKeys.SHULKER_PREVIEW));
                } else {
                    playerShulkerPreview.put(uuidStr, true);
                }
            }
        } catch (Exception e) {
            BuildScape.getLogger().error("CosmeticsConfig: Failed to read data for {}", uuidStr, e);
            PillarParticleConfig.backupUnreadableFile(file);
            playerCosmetics.putIfAbsent(uuidStr, null);
            playerCosmeticColors.putIfAbsent(uuidStr, null);
            playerCreativeTreeBreaker.putIfAbsent(uuidStr, false);
            playerShulkerPreview.putIfAbsent(uuidStr, true);
        }
    }

    private void savePlayer(UUID playerUuid) {
        File file = getPlayerFile(playerUuid);
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;

        Map<Integer, String> cosmetics = playerCosmetics.get(uuidStr);
        Map<String, String> colors = playerCosmeticColors.get(uuidStr);

        CompoundTag nbt = new CompoundTag();
        if (playerUuid != null) {
            nbt.putUUID(NbtKeys.PLAYER_UUID, playerUuid);
        }

        CompoundTag equippedTag = new CompoundTag();
        if (cosmetics != null) {
            for (Map.Entry<Integer, String> entry : cosmetics.entrySet()) {
                equippedTag.putString(entry.getKey().toString(), entry.getValue());
            }
        }
        nbt.put(NbtKeys.EQUIPPED_COSMETICS, equippedTag);

        CompoundTag colorsTag = new CompoundTag();
        if (colors != null) {
            for (Map.Entry<String, String> entry : colors.entrySet()) {
                colorsTag.putString(entry.getKey(), entry.getValue());
            }
        }
        nbt.put(NbtKeys.COSMETIC_COLORS, colorsTag);

        Boolean treeBreaker = playerCreativeTreeBreaker.get(uuidStr);
        if (treeBreaker != null) {
            nbt.putBoolean(NbtKeys.CREATIVE_TREE_BREAKER, treeBreaker);
        }

        Boolean shulkerPreview = playerShulkerPreview.get(uuidStr);
        if (shulkerPreview != null) {
            nbt.putBoolean(NbtKeys.SHULKER_PREVIEW, shulkerPreview);
        }

        try {
            AtomicFiles.write(file.toPath(), file.getName(),
                    temporary -> NbtIo.writeCompressed(nbt, temporary.toFile()),
                    e -> BuildScape.getLogger().warn("CosmeticsConfig: Failed to remove temporary data for {}", uuidStr,
                            e));
        } catch (Exception e) {
            BuildScape.getLogger().error("CosmeticsConfig: Failed to write data for {}", uuidStr, e);
        }
    }

    private void loadGlobalSettings() {
        File file = getGlobalSettingsFile();
        if (file.exists()) {
            try {
                CompoundTag nbt = NbtIo.readCompressed(file);
                if (nbt.contains(NbtKeys.COLOR_PICKER_X)) {
                    colorPickerX = nbt.getInt(NbtKeys.COLOR_PICKER_X);
                }
                if (nbt.contains(NbtKeys.COLOR_PICKER_Y)) {
                    colorPickerY = nbt.getInt(NbtKeys.COLOR_PICKER_Y);
                }
            } catch (Exception e) {
                BuildScape.getLogger().debug("CosmeticsConfig: Unable to read the colour picker position", e);
            }
        }
    }

    private void saveGlobalSettings() {
        File file = getGlobalSettingsFile();
        CompoundTag nbt = new CompoundTag();
        if (colorPickerX != null) {
            nbt.putInt(NbtKeys.COLOR_PICKER_X, colorPickerX);
        }
        if (colorPickerY != null) {
            nbt.putInt(NbtKeys.COLOR_PICKER_Y, colorPickerY);
        }

        try {
            NbtIo.writeCompressed(nbt, file);
        } catch (Exception e) {
            BuildScape.getLogger().warn("CosmeticsConfig: Unable to write the cosmetics file", e);
        }
    }

    public Map<Integer, String> getEquippedCosmetics(UUID playerUuid) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        if (!playerCosmetics.containsKey(uuidStr)) {
            loadPlayer(playerUuid);
        }
        Map<Integer, String> cosmetics = playerCosmetics.get(uuidStr);
        return cosmetics != null ? new HashMap<>(cosmetics) : new HashMap<>();
    }

    public void setEquippedCosmetics(UUID playerUuid, Map<Integer, String> cosmeticsBySlot) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        playerCosmetics.put(uuidStr, new HashMap<>(cosmeticsBySlot));
        savePlayer(playerUuid);
    }

    public void equipCosmetic(UUID playerUuid, int slotIndex, String cosmeticId) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        if (!playerCosmetics.containsKey(uuidStr)) {
            loadPlayer(playerUuid);
        }

        updateMap(playerCosmetics.computeIfAbsent(uuidStr, k -> new HashMap<>()), slotIndex, cosmeticId);
        savePlayer(playerUuid);
    }

    private void updateMap(Map<Integer, String> map, int slot, String id) {
        map.values().remove(id);
        map.remove(slot);
        if (id != null && !id.isEmpty()) {
            map.put(slot, id);
        }
    }

    public void unequipCosmetic(UUID playerUuid, int slotIndex) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        if (!playerCosmetics.containsKey(uuidStr)) {
            loadPlayer(playerUuid);
        }

        Map<Integer, String> playerMap = playerCosmetics.get(uuidStr);
        if (playerMap != null) {
            playerMap.remove(slotIndex);
            savePlayer(playerUuid);
        }
    }

    public String getCosmeticColor(UUID playerUuid, String cosmeticId) {
        if (cosmeticId == null) {
            return null;
        }
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        if (!playerCosmeticColors.containsKey(uuidStr)) {
            loadPlayer(playerUuid);
        }
        Map<String, String> colors = playerCosmeticColors.get(uuidStr);
        if (colors != null && colors.containsKey(cosmeticId)) {
            return colors.get(cosmeticId);
        }
        if (!playerCosmeticColors.containsKey(GLOBAL_SCOPE)) {
            loadPlayer(null);
        }
        Map<String, String> globalColors = playerCosmeticColors.get(GLOBAL_SCOPE);
        return globalColors != null ? globalColors.get(cosmeticId) : null;
    }

    public void setCosmeticColor(UUID playerUuid, String cosmeticId, String hexColor) {
        if (cosmeticId == null) {
            return;
        }
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        if (!playerCosmeticColors.containsKey(uuidStr)) {
            loadPlayer(playerUuid);
        }

        Map<String, String> colors = playerCosmeticColors.computeIfAbsent(uuidStr, k -> new HashMap<>());
        if (hexColor == null || hexColor.isEmpty()) {
            colors.remove(cosmeticId);
        } else {
            colors.put(cosmeticId, hexColor);
        }
        savePlayer(playerUuid);
    }

    public boolean getCreativeTreeBreaker(UUID playerUuid) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        if (!playerCreativeTreeBreaker.containsKey(uuidStr)) {
            loadPlayer(playerUuid);
        }
        return playerCreativeTreeBreaker.getOrDefault(uuidStr, false);
    }

    public void setCreativeTreeBreaker(UUID playerUuid, boolean enabled) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        playerCreativeTreeBreaker.put(uuidStr, enabled);
        savePlayer(playerUuid);
    }

    public boolean getShulkerPreview(UUID playerUuid) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        if (!playerShulkerPreview.containsKey(uuidStr)) {
            loadPlayer(playerUuid);
        }
        return playerShulkerPreview.getOrDefault(uuidStr, true);
    }

    public void setShulkerPreview(UUID playerUuid, boolean enabled) {
        String uuidStr = playerUuid != null ? playerUuid.toString() : GLOBAL_SCOPE;
        playerShulkerPreview.put(uuidStr, enabled);
        savePlayer(playerUuid);
    }

    public int getColorPickerX(int defaultX) {
        return colorPickerX != null ? colorPickerX : defaultX;
    }

    public int getColorPickerY(int defaultY) {
        return colorPickerY != null ? colorPickerY : defaultY;
    }

    public void setColorPickerPos(int x, int y) {
        this.colorPickerX = x;
        this.colorPickerY = y;
        saveGlobalSettings();
    }
}
