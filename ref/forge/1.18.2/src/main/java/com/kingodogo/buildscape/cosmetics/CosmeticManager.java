package com.kingodogo.buildscape.cosmetics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosmeticManager {
    private static final CosmeticManager INSTANCE = new CosmeticManager();

    private final Set<String> allCosmetics = new HashSet<>();

    private final Map<String, CosmeticMetadata> cosmeticMetadata = new HashMap<>();

    private final Map<String, String> particleShapes = new HashMap<>();

    private final Map<String, CosHead<?>> headCosmetics = new HashMap<>();
    private final Map<String, CosChest<?>> chestCosmetics = new HashMap<>();
    private final Map<String, CosLegs<?>> legsCosmetics = new HashMap<>();
    private final Map<String, CosFeet<?>> feetCosmetics = new HashMap<>();

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

    private void registerBuiltInCosmetics() {
        registerRedeemableParticleTrail("buildscape:cosmatics/particle/heart_trail", "Heart Trail", "Hearts float behind you", 2,
                "heart");
        registerParticleTrail("buildscape:cosmatics/particle/sparkle_trail", "Sparkle Trail",
                "Magical sparkles trail behind you", 1, "sparkle");

        registerParticleTrail("buildscape:cosmatics/particle/bubble_trail", "Bubble Trail", "Bubbles float behind you",
                1, "bubble");
        registerParticleTrail("buildscape:cosmatics/particle/cherry_leaves_trail", "Cherry Leaves Trail",
                "Falling pink leaves trail behind you", 1, "cherry");
        registerParticleTrail("buildscape:cosmatics/particle/note_trail", "Note Trail", "Musical notes follow you", 1,
                "note");

        registerRedeemableParticleTrail("buildscape:cosmatics/particle/snowflake_trail", "Snowflake Trail",
                "Snowflakes drift behind you", 2, "snowflake");

        registerRedeemableParticleTrail("buildscape:cosmatics/particle/cake_trail", "Cake Trail",
                "Sweet cake particles follow you", 3, "cake");

        registerHeadCosmetic("buildscape:cosmatics/gear/builders_hat", "Builder's Hat", "A stylish builder's hat", 1);

        cosmeticMetadata.put("buildscape:cosmatics/pets/kingodogo_pet", new CosmeticMetadata("Annoying Kingo Pet", "Adopt KingoDogo as your annoying loyal companion!", 1, CosmeticType.PET, null));
        allCosmetics.add("buildscape:cosmatics/pets/kingodogo_pet");
        defaultCosmetics.add("buildscape:cosmatics/pets/kingodogo_pet");




    }

    private void registerParticleTrail(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        defaultCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId,
                new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_TRAIL, null));
        particleShapes.put(cosmeticId, shape);
    }

    private void registerRedeemableParticleTrail(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId,
                new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_TRAIL, null));
        particleShapes.put(cosmeticId, shape);
    }

    private void registerParticleWings(String cosmeticId, String name, String description, int tier, String shape) {
        allCosmetics.add(cosmeticId);
        defaultCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId,
                new CosmeticMetadata(name, description, tier, CosmeticType.PARTICLE_WINGS, null));
        particleShapes.put(cosmeticId, shape);

    }

    public String getParticleShape(String cosmeticId) {
        return particleShapes.getOrDefault(cosmeticId, "sparkle");
    }

    public boolean supportsColor(String cosmeticId) {
        CosmeticMetadata meta = cosmeticMetadata.get(cosmeticId);
        if (meta == null) return false;

        if (meta.type() == CosmeticType.PARTICLE_WINGS) {
            return true;
        }

        if (!isParticleTrail(cosmeticId)) {
            return false;
        }

        String shape = getParticleShape(cosmeticId);
        return shape.equals("sparkle") || shape.equals("heart");
    }

    private void registerItemCosmetic(String cosmeticId, String name, String description, int tier, String legacyId) {
        allCosmetics.add(cosmeticId);
        CosmeticType type = cosmeticId.contains("/wings/") ? CosmeticType.WINGS : CosmeticType.ITEM;
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, type, legacyId));
    }

    private void registerHeadCosmetic(String cosmeticId, String name, String description, int tier) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, CosmeticType.HEAD, null));

    }

    private void registerArmorCosmetic(String cosmeticId, String name, String description, int tier, CosmeticType type) {
        allCosmetics.add(cosmeticId);
        cosmeticMetadata.put(cosmeticId, new CosmeticMetadata(name, description, tier, type, null));

    }

    public void registerCosHead(String cosmeticId, String name, String description, int tier, CosHead<?> cosHead) {
        registerHeadCosmetic(cosmeticId, name, description, tier);
        headCosmetics.put(cosmeticId, cosHead);
    }

    public void registerCosChest(String cosmeticId, String name, String description, int tier, CosChest<?> cosChest) {
        registerArmorCosmetic(cosmeticId, name, description, tier, CosmeticType.CHEST);
        chestCosmetics.put(cosmeticId, cosChest);
    }

    public void registerCosLegs(String cosmeticId, String name, String description, int tier, CosLegs<?> cosLegs) {
        registerArmorCosmetic(cosmeticId, name, description, tier, CosmeticType.LEGS);
        legsCosmetics.put(cosmeticId, cosLegs);
    }

    public void registerCosFeet(String cosmeticId, String name, String description, int tier, CosFeet<?> cosFeet) {
        registerArmorCosmetic(cosmeticId, name, description, tier, CosmeticType.FEET);
        feetCosmetics.put(cosmeticId, cosFeet);
    }

    public CosHead<?> getCosHead(String cosmeticId) {
        return headCosmetics.get(cosmeticId);
    }

    public CosChest<?> getCosChest(String cosmeticId) {
        return chestCosmetics.get(cosmeticId);
    }

    public CosLegs<?> getCosLegs(String cosmeticId) {
        return legsCosmetics.get(cosmeticId);
    }

    public CosFeet<?> getCosFeet(String cosmeticId) {
        return feetCosmetics.get(cosmeticId);
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

    public boolean isDefaultCosmetic(String cosmeticId) {
        return defaultCosmetics.contains(cosmeticId);
    }

    public Set<String> getDefaultCosmetics() {
        return new HashSet<>(defaultCosmetics);
    }

    public Set<String> getUnlockedCosmetics(String playerUsername) {
        return new HashSet<>(defaultCosmetics);
    }

    public boolean isParticleTrail(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty())
            return false;

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

        public record CosmeticMetadata(String name, String description, int tier, CosmeticType type, String legacyId) {
    }

    public enum CosmeticType {
        ITEM,
        BLOCK,
        PARTICLE_TRAIL,
        WINGS,
        PARTICLE_WINGS,
        EFFECT,
        PET,
        HEAD,
        CHEST,
        LEGS,
        FEET
    }
}
