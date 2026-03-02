package com.kingodogo.buildscape.cosmetics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages cosmetic registration and provides access to all available cosmetics.
 * Automatically registers built-in cosmetics and provides dev access.
 */
public class CosmeticManager {
    private static final CosmeticManager INSTANCE = new CosmeticManager();

    // All registered cosmetics
    private final Set<String> allCosmetics = new HashSet<>();

    // Cosmetic metadata (name, description, tier, etc.)
    private final Map<String, CosmeticMetadata> cosmeticMetadata = new HashMap<>();

    // Particle shape mapping (cosmeticId -> shape type)
    private final Map<String, String> particleShapes = new HashMap<>();

    // Universal Cosmetics Registries
    private final Map<String, CosHead<?>> headCosmetics = new HashMap<>();
    private final Map<String, CosChest<?>> chestCosmetics = new HashMap<>();
    private final Map<String, CosLegs<?>> legsCosmetics = new HashMap<>();
    private final Map<String, CosFeet<?>> feetCosmetics = new HashMap<>();

    // Default cosmetics that are free for everyone (particle trails)
    private final Set<String> defaultCosmetics = new HashSet<>();

    private boolean devUnlockAll = false;

    private CosmeticManager() {
        registerBuiltInCosmetics();
    }

    public static CosmeticManager getInstance() {
        return INSTANCE;
    }

    public void setDevUnlockAll(boolean devUnlockAll) {
        this.devUnlockAll = devUnlockAll;
    }

    public boolean isDevUnlockAll() {
        return this.devUnlockAll;
    }

    /**
     * Register all built-in cosmetics.
     */
    private void registerBuiltInCosmetics() {
        // Register particle trail cosmetics with different shapes
        // Default / Dyeable Particles
        registerParticleTrail("buildscape:cosmatics/particle/heart_trail", "Heart Trail", "Hearts float behind you", 2,
                "heart");
        registerParticleTrail("buildscape:cosmatics/particle/sparkle_trail", "Sparkle Trail",
                "Magical sparkles trail behind you", 1, "sparkle");

        // Other Default Particles
        registerParticleTrail("buildscape:cosmatics/particle/bubble_trail", "Bubble Trail", "Bubbles float behind you",
                1, "bubble");
        registerParticleTrail("buildscape:cosmatics/particle/cherry_leaves_trail", "Cherry Leaves Trail",
                "Falling pink leaves trail behind you", 1, "cherry"); // Changed from "cherry_leaves" to "cherry" to use ModParticles.CHERRY
        registerParticleTrail("buildscape:cosmatics/particle/note_trail", "Note Trail", "Musical notes follow you", 1,
                "note");

        // Custom Particles (Redeemable - not added to defaultCosmetics)
        registerRedeemableParticleTrail("buildscape:cosmatics/particle/snowflake_trail", "Snowflake Trail",
                "Snowflakes drift behind you", 2, "snowflake");

        registerRedeemableParticleTrail("buildscape:cosmatics/particle/cake_trail", "Cake Trail",
                "Sweet cake particles follow you", 3, "cake");

        // Register gear cosmetics
        registerHeadCosmetic("buildscape:cosmatics/gear/builders_hat", "Builder's Hat", "A stylish builder's hat", 1);


        // Register block cosmetics


    }

    /**
     * Register a particle trail cosmetic (default = free for everyone).
     */
    private void registerParticleTrail(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        defaultCosmetics.add(cosmeticId); // Particle trails are free for everyone
        cosmeticMetadata.put(cosmeticId,
                new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_TRAIL, null));
        particleShapes.put(cosmeticId, shape);
    }

    /**
     * Register a particle trail cosmetic that requires redemption (not free by default).
     */
    private void registerRedeemableParticleTrail(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        // NOT added to defaultCosmetics - requires redemption code
        cosmeticMetadata.put(cosmeticId,
                new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_TRAIL, null));
        particleShapes.put(cosmeticId, shape);
    }

    /**
     * Register a particle wings cosmetic (free for everyone).
     */
    private void registerParticleWings(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        defaultCosmetics.add(cosmeticId); // Free for everyone
        cosmeticMetadata.put(cosmeticId,
                new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_WINGS, null));
        particleShapes.put(cosmeticId, shape);

    }

    /**
     * Get particle shape for a cosmetic.
     */
    public String getParticleShape(String cosmeticId) {
        return particleShapes.getOrDefault(cosmeticId, "sparkle");
    }

    /**
     * Check if a cosmetic supports color customization.
     */
    public boolean supportsColor(String cosmeticId) {
        CosmeticMetadata meta = cosmeticMetadata.get(cosmeticId);
        if (meta == null) return false;

        // Particle wings - all shapes support color
        if (meta.type() == CosmeticType.PARTICLE_WINGS) {
            return true;
        }

        // For particle trails, only specific shapes support color
        if (!isParticleTrail(cosmeticId)) {
            return false;
        }

        String shape = getParticleShape(cosmeticId);
        // Only Sparkle and Heart shapes support colors
        // Cherry, Cake, Snowflake trail, Firework, Note, Bubble, Cherry Leaves are NOT colorable
        return shape.equals("sparkle") || shape.equals("heart");
    }

    /**
     * Register an item cosmetic.
     */
    private void registerItemCosmetic(String cosmeticId, String name, String description, int tier, String legacyId) {
        allCosmetics.add(cosmeticId);
        CosmeticType type = cosmeticId.contains("/wings/") ? CosmeticType.WINGS : CosmeticType.ITEM;
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, type, legacyId));
    }

    /**
     * Register a custom head/armor cosmetic with a custom model.
     */
    private void registerHeadCosmetic(String cosmeticId, String name, String description, int tier) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, CosmeticType.HEAD, null));

    }

    /**
     * Register a custom armor cosmetic (Chest, Legs, Feet) with a custom model.
     */
    private void registerArmorCosmetic(String cosmeticId, String name, String description, int tier, CosmeticType type) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, type, null));

    }

    /**
     * Register a universal head cosmetic.
     */
    public void registerCosHead(String cosmeticId, String name, String description, int tier, CosHead<?> cosHead) {
        registerHeadCosmetic(cosmeticId, name, description, tier);
        headCosmetics.put(cosmeticId, cosHead);
    }

    /**
     * Register a universal chest cosmetic.
     */
    public void registerCosChest(String cosmeticId, String name, String description, int tier, CosChest<?> cosChest) {
        registerArmorCosmetic(cosmeticId, name, description, tier, CosmeticType.CHEST);
        chestCosmetics.put(cosmeticId, cosChest);
    }

    /**
     * Register a universal legs cosmetic.
     */
    public void registerCosLegs(String cosmeticId, String name, String description, int tier, CosLegs<?> cosLegs) {
        registerArmorCosmetic(cosmeticId, name, description, tier, CosmeticType.LEGS);
        legsCosmetics.put(cosmeticId, cosLegs);
    }

    /**
     * Register a universal feet cosmetic.
     */
    public void registerCosFeet(String cosmeticId, String name, String description, int tier, CosFeet<?> cosFeet) {
        registerArmorCosmetic(cosmeticId, name, description, tier, CosmeticType.FEET);
        feetCosmetics.put(cosmeticId, cosFeet);
    }

    /**
     * Get a universal head cosmetic by ID.
     */
    public CosHead<?> getCosHead(String cosmeticId) {
        return headCosmetics.get(cosmeticId);
    }

    /**
     * Get a universal chest cosmetic by ID.
     */
    public CosChest<?> getCosChest(String cosmeticId) {
        return chestCosmetics.get(cosmeticId);
    }

    /**
     * Get a universal legs cosmetic by ID.
     */
    public CosLegs<?> getCosLegs(String cosmeticId) {
        return legsCosmetics.get(cosmeticId);
    }

    /**
     * Get a universal feet cosmetic by ID.
     */
    public CosFeet<?> getCosFeet(String cosmeticId) {
        return feetCosmetics.get(cosmeticId);
    }

    /**
     * Get all registered cosmetics.
     */
    public Set<String> getAllCosmetics() {
        return new HashSet<>(allCosmetics);
    }

    /**
     * Get metadata for a cosmetic.
     */
    public CosmeticMetadata getMetadata(String cosmeticId) {
        return cosmeticMetadata.get(cosmeticId);
    }

    /**
     * Check if a cosmetic is registered.
     */
    public boolean isRegistered(String cosmeticId) {
        return allCosmetics.contains(cosmeticId);
    }

    /**
     * Check if a cosmetic is a default (free for everyone).
     */
    public boolean isDefaultCosmetic(String cosmeticId) {
        return defaultCosmetics.contains(cosmeticId);
    }

    /**
     * Get all default cosmetics (free for everyone).
     */
    public Set<String> getDefaultCosmetics() {
        return new HashSet<>(defaultCosmetics);
    }

    /**
     * Get unlocked cosmetics for offline/default use.
     * Returns only default cosmetics (particle trails).
     * For full unlocks including redeemed items, use SupportersTabState which gets data from the API.
     */
    public Set<String> getUnlockedCosmetics(String playerUsername) {
        // Return only default cosmetics - no hardcoded bypasses
        // Admin/redeemed cosmetics come from the API via SupportersTabState
        return new HashSet<>(defaultCosmetics);
    }

    /**
     * Check if a cosmetic ID is a particle trail.
     */
    public boolean isParticleTrail(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty())
            return false;

        // Check metadata type if available
        CosmeticMetadata metadata = cosmeticMetadata.get(cosmeticId);
        if (metadata != null && metadata.type == CosmeticType.PARTICLE_TRAIL) {
            return true;
        }

        // Fallback to string check
        String idLower = cosmeticId.toLowerCase();
        return idLower.contains("particle") &&
                (idLower.contains("trail") ||
                        idLower.contains("star") ||
                        idLower.contains("sparkle") ||
                        idLower.contains("effect"));
    }

    /**
     * Cosmetic metadata.
     *
     * @param tier     1 = Bronze, 2 = Silver, 3 = Gold
     * @param legacyId For resolving to Item/Block
     */
        public record CosmeticMetadata(String name, String description, int tier, CosmeticType type, String legacyId) {
    }

    /**
     * Cosmetic type enum.
     */
    public enum CosmeticType {
        ITEM,
        BLOCK,
        PARTICLE_TRAIL,
        WINGS,
        PARTICLE_WINGS,
        EFFECT,
        HEAD, // Custom head model cosmetics
        CHEST, // Custom chest/torso model cosmetics
        LEGS, // Custom leggings model cosmetics
        FEET // Custom boots model cosmetics
    }
}
