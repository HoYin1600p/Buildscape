package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = com.kingodogo.buildscape.BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ParticleTrailHandler {
    
    private static final Map<UUID, Vec3> lastPositions = new HashMap<>();
    
    private static final Map<UUID, Long> lastSpawnTimes = new HashMap<>();
    
    private static final long SPAWN_INTERVAL_MS = 50;

    private static final double MIN_MOVEMENT_DISTANCE = 0.01;
    
    private static boolean isParticleTrail(String cosmeticId) {
        return CosmeticManager.getInstance().isParticleTrail(cosmeticId);
    }
    
    private static ParticleOptions getParticleType(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return ModParticles.GLOW_LIME_SPARKLE.get();
        }
        
        CosmeticManager manager = CosmeticManager.getInstance();
        String shape = manager.getParticleShape(cosmeticId);

        switch (shape) {
            case "heart":
                return ParticleTypes.HEART;
            case "note":
                return ParticleTypes.NOTE;
            case "smoke":
                return ParticleTypes.SMOKE;
            case "cloud":
                return ParticleTypes.CLOUD;
            case "portal":
                return ParticleTypes.PORTAL;
            case "enchant":
                return ParticleTypes.ENCHANT;
            case "totem":
                return ParticleTypes.TOTEM_OF_UNDYING;
            case "crit":
                return ParticleTypes.CRIT;
            case "snowflake":
                return ModParticles.SNOWFLAKE.get();
            case "sparkle":
            default:
                return ModParticles.GLOW_LIME_SPARKLE.get();
        }
    }
    
    public static float[] getParticleColor(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return new float[]{1.0f, 1.0f, 1.0f};
        }
        
        String idLower = cosmeticId.toLowerCase();

        if (idLower.contains("star")) {
            return new float[]{1.0f, 0.9f, 0.2f};
        }

        if (idLower.contains("sparkle")) {
            return new float[]{0.5f, 1.0f, 1.0f};
        }

        if (idLower.contains("emerald")) {
            return new float[]{0.2f, 1.0f, 0.3f};
        }

        if (idLower.contains("diamond")) {
            return new float[]{0.5f, 0.8f, 1.0f};
        }

        if (idLower.contains("netherite")) {
            return new float[]{0.3f, 0.1f, 0.4f};
        }

        if (idLower.contains("rainbow")) {
            return new float[]{1.0f, 1.0f, 1.0f};
        }

        if (idLower.contains("flame")) {
            return new float[]{1.0f, 0.4f, 0.1f};
        }

        if (idLower.contains("ice")) {
            return new float[]{0.7f, 0.9f, 1.0f};
        }

        if (idLower.contains("snowflake")) {
            return new float[]{0.9f, 0.95f, 1.0f};
        }

        return new float[]{0.5f, 1.0f, 1.0f};
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        Player player = mc.player;
        UUID playerUuid = player.getUUID();

        com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState state =
            com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState.getInstance();

        Set<String> equippedCosmeticsSet = state.getEquippedCosmetics();

        CosmeticsConfig config = CosmeticsConfig.get();
        Map<Integer, String> equippedCosmeticsBySlot = config.getEquippedCosmetics(playerUuid);

        boolean hasParticleTrail = false;
        String particleTrailId = null;
        
        for (String cosmeticId : equippedCosmeticsSet) {
            if (isParticleTrail(cosmeticId)) {
                hasParticleTrail = true;
                particleTrailId = cosmeticId;
                break;
            }
        }

        if (!hasParticleTrail) {
            for (String cosmeticId : equippedCosmeticsBySlot.values()) {
                if (isParticleTrail(cosmeticId)) {
                    hasParticleTrail = true;
                    particleTrailId = cosmeticId;
                    break;
                }
            }
        }
        
        if (!hasParticleTrail) {
            lastPositions.remove(playerUuid);
            lastSpawnTimes.remove(playerUuid);
            return;
        }

        Vec3 currentPos = player.position();
        Vec3 lastPos = lastPositions.get(playerUuid);

        if (lastPos == null) {
            lastPositions.put(playerUuid, currentPos);
            return;
        }

        double distance = currentPos.distanceTo(lastPos);
        if (distance < MIN_MOVEMENT_DISTANCE) {
            lastPositions.put(playerUuid, currentPos);
            return;
        }

        long currentTime = System.currentTimeMillis();
        Long lastSpawnTime = lastSpawnTimes.get(playerUuid);
        if (lastSpawnTime != null && (currentTime - lastSpawnTime) < SPAWN_INTERVAL_MS) {
            lastPositions.put(playerUuid, currentPos);
            return;
        }

        Vec3 direction = currentPos.subtract(lastPos).normalize();
        Vec3 spawnPos = lastPos.add(0, 0.1, 0);

        Random random = mc.level.random;
        double offsetX = (random.nextDouble() - 0.5) * 0.3;
        double offsetZ = (random.nextDouble() - 0.5) * 0.3;
        spawnPos = spawnPos.add(offsetX, 0, offsetZ);

        ParticleOptions particleType = getParticleType(particleTrailId);

        CosmeticsConfig cosmeticsConfig = CosmeticsConfig.get();
        String storedColor = cosmeticsConfig.getCosmeticColor(playerUuid, particleTrailId);
        float[] color;
        
        if (storedColor != null && !storedColor.isEmpty()) {
            try {
                String hex = storedColor.startsWith("#") ? storedColor.substring(1) : storedColor;
                int rgb = Integer.parseInt(hex, 16);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                color = new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
            } catch (NumberFormatException e) {
                color = getParticleColor(particleTrailId);
            }
        } else {
            color = getParticleColor(particleTrailId);
        }

        double velocityX = -direction.x * 0.1 + (random.nextDouble() - 0.5) * 0.05;
        double velocityY = 0.08 + random.nextDouble() * 0.05;
        double velocityZ = -direction.z * 0.1 + (random.nextDouble() - 0.5) * 0.05;

        CosmeticManager manager = CosmeticManager.getInstance();
        if (particleType == ModParticles.GLOW_LIME_SPARKLE.get() && manager.supportsColor(particleTrailId)) {
            String colorCode = String.format("#%02X%02X%02X", 
                (int)(color[0] * 255), 
                (int)(color[1] * 255), 
                (int)(color[2] * 255));
            com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
                spawnPos.x, spawnPos.y, spawnPos.z, colorCode);
        }

        try {
            mc.level.addParticle(particleType, 
                spawnPos.x, spawnPos.y, spawnPos.z,
                velocityX, velocityY, velocityZ);
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to spawn particle trail: " + e.getMessage());
        }

        lastPositions.put(playerUuid, currentPos);
        lastSpawnTimes.put(playerUuid, currentTime);
    }
    
    public static void clearTracking() {
        lastPositions.clear();
        lastSpawnTimes.clear();
    }
}

