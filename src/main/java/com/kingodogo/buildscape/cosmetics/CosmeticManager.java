package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.BuildScape;

import java.util.*;

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

    // Dev username for development access
    private static final String DEV_USERNAME = "Dev";

    private CosmeticManager() {
        registerBuiltInCosmetics();
    }

    public static CosmeticManager getInstance() {
        return INSTANCE;
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
                "Falling pink leaves trail behind you", 1, "cherry_leaves");
        registerParticleTrail("buildscape:cosmatics/particle/note_trail", "Note Trail", "Musical notes follow you", 1,
                "note");

        // Custom Particles
        registerParticleTrail("buildscape:cosmatics/particle/snowflake_trail", "Snowflake Trail",
                "Snowflakes drift behind you", 2, "snowflake");
        registerParticleTrail("buildscape:cosmatics/particle/firework_trail", "Firework Trail",
                "Miniature fireworks trail behind you", 3, "firework");
        registerParticleTrail("buildscape:cosmatics/particle/cake_trail", "Cake Trail",
                "Sweet cake particles follow you", 3, "cake");

        // Register some item cosmetics for variety

        registerItemCosmetic("buildscape:cosmatics/wings/elytra", "Elytra Wings", "Wings for gliding", 2,
                "item:minecraft:elytra");
        registerHeadCosmetic("buildscape:cosmatics/gear/builders_hat", "Builder's Hat", "A stylish builder's hat", 1);
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_helmet", "Netherite Helmet",
                "Powerful netherite helmet", 3, "item:minecraft:netherite_helmet");
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_chestplate", "Netherite Chestplate",
                "Powerful netherite chestplate", 3, "item:minecraft:netherite_chestplate");
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_leggings", "Netherite Leggings",
                "Powerful netherite leggings", 3, "item:minecraft:netherite_leggings");
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_boots", "Netherite Boots", "Powerful netherite boots",
                3, "item:minecraft:netherite_boots");

        // Register block cosmetics

        BuildScape.getLogger().info("Registered " + allCosmetics.size() + " built-in cosmetics");
    }

    /**
     * Register a particle trail cosmetic.
     */
    private void registerParticleTrail(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId,
                new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_TRAIL, null));
        particleShapes.put(cosmeticId, shape);
    }

    /**
     * Get particle shape for a cosmetic.
     */
    public String getParticleShape(String cosmeticId) {
        return particleShapes.getOrDefault(cosmeticId, "sparkle");
    }

    /**
     * Check if a particle trail supports color customization.
     */
    public boolean supportsColor(String cosmeticId) {
        // Only particle trails support color (and specifically only sparkle/heart
        // types)
        if (!isParticleTrail(cosmeticId)) {
            return false;
        }

        String shape = getParticleShape(cosmeticId);
        // Sparkle and Heart shapes support colors
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
        BuildScape.getLogger().info("Registered HEAD cosmetic: " + cosmeticId + " (" + name + ")");
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
     * Check if a player has access to a cosmetic (dev always has access).
     */
    public boolean hasAccess(String playerUsername, String cosmeticId) {
        // Dev always has access to everything
        if (playerUsername != null && playerUsername.equalsIgnoreCase(DEV_USERNAME)) {
            return true;
        }

        // For now, all cosmetics are accessible (will be replaced with API check)
        return isRegistered(cosmeticId);
    }

    /**
     * Get unlocked cosmetics for a player (dev gets everything).
     */
    public Set<String> getUnlockedCosmetics(String playerUsername) {
        Set<String> unlocked = new HashSet<>();

        // Dev gets everything
        if (playerUsername != null && playerUsername.equalsIgnoreCase(DEV_USERNAME)) {
            unlocked.addAll(allCosmetics);
            return unlocked;
        }

        // For now, return empty (will be replaced with API check)
        // API will determine which cosmetics are unlocked
        return unlocked;
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
        EFFECT,
        HEAD // Custom head/armor model cosmetics
    }
}
