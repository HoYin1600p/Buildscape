package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.SupportersApiClient;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import net.minecraft.client.Minecraft;

import java.util.*;

public class SupportersTabState {
    private static SupportersTabState instance;
    
    public static final int SLOT_HEAD = 0;
    public static final int SLOT_CHEST = 1;
    public static final int SLOT_LEGS = 2;
    public static final int SLOT_FEET = 3;
    public static final int SLOT_TRAIL = -1;
    public static final int SLOT_WINGS = -2;
    public static final int SLOT_BACK = -3;
    public static final int SLOT_SHOULDER = -4;
    public static final int SLOT_OTHER = -100;
    
    private String selectedCosmeticId;
    private Set<String> unlockedCosmetics = new HashSet<>();
    private Set<String> equippedCosmetics = new HashSet<>();
    private final Map<Integer, String> equippedCosmeticsBySlot = new HashMap<>();
    private UUID playerUuid;
    private Runnable onSelectionChanged;
    private Runnable onEquippedChanged;
    
    private SupportersTabState() {
    }
    
    public static SupportersTabState getInstance() {
        if (instance == null) {
            instance = new SupportersTabState();
        }
        return instance;
    }
    
    public void reset() {
        selectedCosmeticId = null;
        unlockedCosmetics.clear();
        equippedCosmetics.clear();
        equippedCosmeticsBySlot.clear();
        playerUuid = null;
        onSelectionChanged = null;
        onEquippedChanged = null;
    }
    
    public String getSelectedCosmeticId() {
        return selectedCosmeticId;
    }
    
    public void setSelectedCosmeticId(String cosmeticId) {
        if (this.selectedCosmeticId != cosmeticId) {
            this.selectedCosmeticId = cosmeticId;
            if (onSelectionChanged != null) {
                onSelectionChanged.run();
            }
            // Clear preview state when selection changes
            this.previewCosmeticId = null;
        }
    }
    
    private String previewCosmeticId;
    
    public String getPreviewCosmeticId() {
        return previewCosmeticId;
    }
    
    public void setPreviewCosmeticId(String previewCosmeticId) {
        this.previewCosmeticId = previewCosmeticId;
    }
    
    public Set<String> getUnlockedCosmetics() {
        if (com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance().isDevUnlockAll()) {
            return com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance().getAllCosmetics();
        }
        return new HashSet<>(unlockedCosmetics);
    }
    
    public void setUnlockedCosmetics(Set<String> unlockedCosmetics) {
        this.unlockedCosmetics = new HashSet<>(unlockedCosmetics != null ? unlockedCosmetics : new HashSet<>());
    }
    
    public boolean isUnlocked(String cosmeticId) {
        if (com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance().isDevUnlockAll()) {
            return true;
        }
        if (com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance().isDefaultCosmetic(cosmeticId)) {
            return true;
        }
        return unlockedCosmetics.contains(cosmeticId);
    }
    
    public Set<String> getEquippedCosmetics() {
        return new HashSet<>(equippedCosmetics);
    }
    
    public void setEquippedCosmetics(Set<String> equippedCosmetics) {
        if (equippedCosmetics == null) return;
        this.equippedCosmetics = new HashSet<>(equippedCosmetics);
        updateSlotMapFromSet();
        if (onEquippedChanged != null) {
            onEquippedChanged.run();
        }
    }
    
    public void equipCosmeticToSlot(int slotIndex, String cosmeticId) {
        equippedCosmeticsBySlot.values().remove(cosmeticId);

        String oldCosmetic = equippedCosmeticsBySlot.remove(slotIndex);
        if (oldCosmetic != null) equippedCosmetics.remove(oldCosmetic);

        if (cosmeticId != null && !cosmeticId.isEmpty()) {
            equippedCosmeticsBySlot.put(slotIndex, cosmeticId);
            equippedCosmetics.add(cosmeticId);
        }

        if (playerUuid != null) {
            CosmeticsConfig.get().equipCosmetic(playerUuid, slotIndex, cosmeticId);
        }

        if (onEquippedChanged != null) {
            onEquippedChanged.run();
        }
    }
    
    public void unequipCosmeticFromSlot(int slotIndex) {
        String cosmeticId = equippedCosmeticsBySlot.remove(slotIndex);
        if (cosmeticId != null) {
            equippedCosmetics.remove(cosmeticId);
            if (playerUuid != null) {
                CosmeticsConfig.get().unequipCosmetic(playerUuid, slotIndex);
            }
            if (onEquippedChanged != null) {
                onEquippedChanged.run();
            }
        }
    }
    
    public void equipCosmetic(String cosmeticId) {
        if (cosmeticId == null) return;
        
        boolean isUnlocked = isUnlocked(cosmeticId);
        
        if (isUnlocked) {
            int bestSlot = getBestSlotForCosmetic(cosmeticId);
            equipCosmeticToSlot(bestSlot, cosmeticId);
        }
    }

    public int getBestSlotForCosmetic(String cosmeticId) {
        CosmeticManager manager = CosmeticManager.getInstance();
        CosmeticRegistry registry = CosmeticRegistry.getInstance();

        if (manager.isParticleTrail(cosmeticId)) return SLOT_TRAIL;

        CosmeticManager.CosmeticMetadata metadata = manager.getMetadata(cosmeticId);
        if (metadata != null) {
            if (metadata.type() == CosmeticManager.CosmeticType.WINGS) return SLOT_WINGS;
            if (metadata.type() == CosmeticManager.CosmeticType.PARTICLE_WINGS) return SLOT_WINGS;  // Particle wings also go to SLOT_WINGS
            if (metadata.type() == CosmeticManager.CosmeticType.HEAD) return SLOT_HEAD;

            // Handle future categories based on ID path
            if (cosmeticId.contains("/back/")) return SLOT_BACK;
            if (cosmeticId.contains("/shoulder/")) return SLOT_SHOULDER;
        }

        net.minecraft.world.item.ItemStack stack = registry.resolveToItemStack(cosmeticId);
        if (stack != null && !stack.isEmpty()) {
            int slot = getSlotIndexForStack(stack);
            if (slot >= 0) return slot;
        }

        return SLOT_OTHER;
    }
    
    public void unequipCosmetic(String cosmeticId) {
        if (cosmeticId == null) return;

        Integer slotToUnequip = null;
        for (Map.Entry<Integer, String> entry : equippedCosmeticsBySlot.entrySet()) {
            if (cosmeticId.equals(entry.getValue())) {
                slotToUnequip = entry.getKey();
                break;
            }
        }
        
        if (slotToUnequip != null) {
            unequipCosmeticFromSlot(slotToUnequip);
        } else {
            equippedCosmetics.remove(cosmeticId);
            if (onEquippedChanged != null) onEquippedChanged.run();
        }
    }
    
    public boolean isEquipped(String cosmeticId) {
        return equippedCosmetics.contains(cosmeticId);
    }
    
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;

        equippedCosmeticsBySlot.clear();
        equippedCosmetics.clear();
        
        if (playerUuid != null) {
            Map<Integer, String> saved = CosmeticsConfig.get().getEquippedCosmetics(playerUuid);
            
            String lastEquippedId = null;
            boolean needsConfigCleanup = false;

            for (Map.Entry<Integer, String> entry : saved.entrySet()) {
                String cosmeticId = entry.getValue();
                if (cosmeticId != null && !cosmeticId.isEmpty()) {
                    // SECURITY & ACCESS VALIDATION: Only load and keep if player actually has access
                    if (isUnlocked(cosmeticId)) {
                        equippedCosmeticsBySlot.put(entry.getKey(), cosmeticId);
                        equippedCosmetics.add(cosmeticId);
                        lastEquippedId = cosmeticId;
                    } else {
                        // Player no longer has access (account change or permission revoked)
                        // Queue for removal from the .dat file
                        needsConfigCleanup = true;
                    }
                }
            }

            // If we found unauthorized cosmetics, purge them from the private storage
            if (needsConfigCleanup) {
                for (int slot : new java.util.HashSet<>(saved.keySet())) {
                    String id = saved.get(slot);
                    if (id != null && !id.isEmpty() && !isUnlocked(id)) {
                        CosmeticsConfig.get().unequipCosmetic(playerUuid, slot);
                    }
                }
            }

            if (selectedCosmeticId == null && !equippedCosmetics.isEmpty()) {
                String trailId = equippedCosmeticsBySlot.get(SLOT_TRAIL);
                setSelectedCosmeticId(trailId != null ? trailId : lastEquippedId);
            }
        }
        
        if (onEquippedChanged != null) {
            onEquippedChanged.run();
        }
    }
    
    private void updateSlotMapFromSet() {
        equippedCosmeticsBySlot.clear();
        for (String id : equippedCosmetics) {
            if (id == null || id.isEmpty()) continue;
            int slot = getBestSlotForCosmetic(id);
            equippedCosmeticsBySlot.put(slot, id);
        }
    }
    
    private int getSlotIndexForStack(net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.item.Item item = stack.getItem();
        if (item instanceof net.minecraft.world.item.ArmorItem armor) {
            switch (armor.getSlot()) {
                case HEAD -> { return SLOT_HEAD; }
                case CHEST -> { return SLOT_CHEST; }
                case LEGS -> { return SLOT_LEGS; }
                case FEET -> { return SLOT_FEET; }
                default -> { return -100; }
            }
        }
        return -100;
    }
    
    public Map<Integer, String> getEquippedCosmeticsBySlot() {
        return new HashMap<>(equippedCosmeticsBySlot);
    }
    
    public String getEquippedCosmeticInSlot(int slotIndex) {
        return equippedCosmeticsBySlot.get(slotIndex);
    }
    
    public void setOnSelectionChanged(Runnable callback) {
        this.onSelectionChanged = callback;
    }
    
    public void setOnEquippedChanged(Runnable callback) {
        this.onEquippedChanged = callback;
    }

}
