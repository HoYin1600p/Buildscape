package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.BuildScape;

import java.util.*;

public class CosmeticManager {
    private static final CosmeticManager INSTANCE = new CosmeticManager();
    
    private final Set<String> allCosmetics = new HashSet<>();
    
    private final Map<String, CosmeticMetadata> cosmeticMetadata = new HashMap<>();
    
    private final Map<String, String> particleShapes = new HashMap<>();
    
    private static final String DEV_USERNAME = "Dev";
    
    private CosmeticManager() {
        registerBuiltInCosmetics();
    }
    
    public static CosmeticManager getInstance() {
        return INSTANCE;
    }
    
    private void registerBuiltInCosmetics() {
        registerParticleTrail("buildscape:cosmatics/particle/star_trail", "Star Trail", "Golden stars follow behind you", 1, "sparkle");
        registerParticleTrail("buildscape:cosmatics/particle/sparkle_trail", "Sparkle Trail", "Magical sparkles trail behind you", 1, "sparkle");
        registerParticleTrail("buildscape:cosmatics/particle/emerald_trail", "Emerald Trail", "Emerald particles follow you", 2, "sparkle");
        registerParticleTrail("buildscape:cosmatics/particle/diamond_trail", "Diamond Trail", "Diamond sparkles trail behind you", 2, "sparkle");
        registerParticleTrail("buildscape:cosmatics/particle/netherite_trail", "Netherite Trail", "Dark netherite particles follow you", 3, "sparkle");
        registerParticleTrail("buildscape:cosmatics/particle/rainbow_trail", "Rainbow Trail", "Colorful rainbow particles trail behind you", 3, "sparkle");
        registerParticleTrail("buildscape:cosmatics/particle/flame_trail", "Flame Trail", "Fiery particles follow you", 2, "sparkle");
        registerParticleTrail("buildscape:cosmatics/particle/ice_trail", "Ice Trail", "Frosty ice particles trail behind you", 2, "sparkle");

        registerParticleTrail("buildscape:cosmatics/particle/heart_trail", "Heart Trail", "Hearts float behind you", 2, "heart");
        registerParticleTrail("buildscape:cosmatics/particle/note_trail", "Note Trail", "Musical notes follow you", 1, "note");
        registerParticleTrail("buildscape:cosmatics/particle/smoke_trail", "Smoke Trail", "Smoke billows behind you", 1, "smoke");
        registerParticleTrail("buildscape:cosmatics/particle/cloud_trail", "Cloud Trail", "Fluffy clouds trail behind you", 2, "cloud");
        registerParticleTrail("buildscape:cosmatics/particle/portal_trail", "Portal Trail", "Portal particles follow you", 3, "portal");
        registerParticleTrail("buildscape:cosmatics/particle/enchant_trail", "Enchant Trail", "Enchantment particles trail behind you", 2, "enchant");
        registerParticleTrail("buildscape:cosmatics/particle/totem_trail", "Totem Trail", "Totem particles follow you", 3, "totem");
        registerParticleTrail("buildscape:cosmatics/particle/crit_trail", "Critical Trail", "Critical hit particles trail behind you", 2, "crit");
        registerParticleTrail("buildscape:cosmatics/particle/snowflake_trail", "Snowflake Trail", "Snowflakes drift behind you", 2, "snowflake");

        registerItemCosmetic("buildscape:cosmatics/gear/diamond_sword", "Diamond Sword", "A sharp diamond blade", 1, "item:minecraft:diamond_sword");
        registerItemCosmetic("buildscape:cosmatics/gear/golden_apple", "Golden Apple", "A golden apple", 1, "item:minecraft:golden_apple");
        registerItemCosmetic("buildscape:cosmatics/wings/elytra", "Elytra Wings", "Wings for gliding", 2, "item:minecraft:elytra");
        registerHeadCosmetic("buildscape:cosmatics/gear/builders_hat", "Builder's Hat", "A stylish builder's hat", 1);
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_helmet", "Netherite Helmet", "Powerful netherite helmet", 3, "item:minecraft:netherite_helmet");
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_chestplate", "Netherite Chestplate", "Powerful netherite chestplate", 3, "item:minecraft:netherite_chestplate");
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_leggings", "Netherite Leggings", "Powerful netherite leggings", 3, "item:minecraft:netherite_leggings");
        registerItemCosmetic("buildscape:cosmatics/gear/netherite_boots", "Netherite Boots", "Powerful netherite boots", 3, "item:minecraft:netherite_boots");
        registerItemCosmetic("buildscape:cosmatics/gear/trident", "Trident", "A powerful trident", 2, "item:minecraft:trident");
        registerItemCosmetic("buildscape:cosmatics/gear/bow", "Bow", "A sturdy bow", 1, "item:minecraft:bow");

        registerBlockCosmetic("buildscape:cosmatics/gear/gold_block", "Gold Block", "A block of gold", 1, "block:minecraft:gold_block");
        registerBlockCosmetic("buildscape:cosmatics/gear/diamond_block", "Diamond Block", "A block of diamonds", 2, "block:minecraft:diamond_block");
        registerBlockCosmetic("buildscape:cosmatics/gear/emerald_block", "Emerald Block", "A block of emeralds", 2, "block:minecraft:emerald_block");
        registerBlockCosmetic("buildscape:cosmatics/gear/netherite_block", "Netherite Block", "A block of netherite", 3, "block:minecraft:netherite_block");
        
        BuildScape.getLogger().info("Registered " + allCosmetics.size() + " built-in cosmetics");
    }
    
    private void registerParticleTrail(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_TRAIL, null));
        particleShapes.put(cosmeticId, shape);
    }
    
    public String getParticleShape(String cosmeticId) {
        return particleShapes.getOrDefault(cosmeticId, "sparkle");
    }
    
    public boolean supportsColor(String cosmeticId) {
        String shape = getParticleShape(cosmeticId);
        return shape.equals("sparkle");
    }
    
    private void registerItemCosmetic(String cosmeticId, String name, String description, int tier, String legacyId) {
        allCosmetics.add(cosmeticId);
        CosmeticType type = cosmeticId.contains("/wings/") ? CosmeticType.WINGS : CosmeticType.ITEM;
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, type, legacyId));
    }
    
    private void registerBlockCosmetic(String cosmeticId, String name, String description, int tier, String legacyId) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, CosmeticType.BLOCK, legacyId));
    }
    
    private void registerHeadCosmetic(String cosmeticId, String name, String description, int tier) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, CosmeticType.HEAD, null));
        BuildScape.getLogger().info("Registered HEAD cosmetic: " + cosmeticId + " (" + name + ")");
    }
    
    public Set<String> getAllCosmetics() {
        return new HashSet<>(allCosmetics);
    }
    
    public CosmeticMetadata getMetadata(String cosmeticId) {
        return cosmeticMetadata.get(cosmeticId);
    }
    
    public boolean isRegistered(String cosmeticId) {
        return allCosmetics.contains(cosmeticId);
    }
    
    public boolean hasAccess(String playerUsername, String cosmeticId) {
        if (playerUsername != null && playerUsername.equalsIgnoreCase(DEV_USERNAME)) {
            return true;
        }

        return isRegistered(cosmeticId);
    }
    
    public Set<String> getUnlockedCosmetics(String playerUsername) {
        Set<String> unlocked = new HashSet<>();

        if (playerUsername != null && playerUsername.equalsIgnoreCase(DEV_USERNAME)) {
            unlocked.addAll(allCosmetics);
            return unlocked;
        }

        return unlocked;
    }
    
    public boolean isParticleTrail(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) return false;

        CosmeticMetadata metadata = cosmeticMetadata.get(cosmeticId);
        if (metadata != null && metadata.type == CosmeticType.PARTICLE_TRAIL) {
            return true;
        }

        String idLower = cosmeticId.toLowerCase();
        return idLower.contains("particle") && 
               (idLower.contains("trail") || 
                idLower.contains("star") || 
                idLower.contains("sparkle") ||
                idLower.contains("effect"));
    }
    
    public static class CosmeticMetadata {
        public final String name;
        public final String description;
        public final int tier;
        public final CosmeticType type;
        public final String legacyId;

        public CosmeticMetadata(String name, String description, int tier, CosmeticType type, String legacyId) {
            this.name = name;
            this.description = description;
            this.tier = tier;
            this.type = type;
            this.legacyId = legacyId;
        }
    }
    
    public enum CosmeticType {
        ITEM,
        BLOCK,
        PARTICLE_TRAIL,
        WINGS,
        EFFECT,
        HEAD
    }
}

