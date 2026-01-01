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

/**
 * Handles particle trails for player cosmetics.
 * Spawns particles (like stars) behind the player when they walk.
 */
@Mod.EventBusSubscriber(modid = com.kingodogo.buildscape.BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ParticleTrailHandler {
    
    // Track last position for each player
    private static final Map<UUID, Vec3> lastPositions = new HashMap<>();
    
    // Track last particle spawn time to control spawn rate
    private static final Map<UUID, Long> lastSpawnTimes = new HashMap<>();
    
    // Particle spawn rate (milliseconds between spawns)
    private static final long SPAWN_INTERVAL_MS = 50; // Spawn every 50ms (20 particles per second)
    
    // Minimum movement distance to spawn particles (prevents spawning when standing still)
    private static final double MIN_MOVEMENT_DISTANCE = 0.01;
    
    /**
     * Check if a cosmetic ID is a particle trail cosmetic.
     */
    private static boolean isParticleTrail(String cosmeticId) {
        return CosmeticManager.getInstance().isParticleTrail(cosmeticId);
    }
    
    /**
     * Get particle type for a cosmetic ID based on shape.
     * Returns different particle types based on particle shape.
     */
    private static ParticleOptions getParticleType(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return ModParticles.GLOW_LIME_SPARKLE.get();
        }
        
        CosmeticManager manager = CosmeticManager.getInstance();
        String shape = manager.getParticleShape(cosmeticId);
        
        // Return different particle types based on shape
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
    
    /**
     * Get color for particle trail based on cosmetic ID.
     * Returns RGB values (0.0-1.0).
     */
    public static float[] getParticleColor(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return new float[]{1.0f, 1.0f, 1.0f}; // White
        }
        
        String idLower = cosmeticId.toLowerCase();
        
        // Star trail - golden/yellow
        if (idLower.contains("star")) {
            return new float[]{1.0f, 0.9f, 0.2f}; // Golden yellow
        }
        
        // Sparkle trail - cyan/white
        if (idLower.contains("sparkle")) {
            return new float[]{0.5f, 1.0f, 1.0f}; // Cyan
        }
        
        // Emerald trail - green
        if (idLower.contains("emerald")) {
            return new float[]{0.2f, 1.0f, 0.3f}; // Emerald green
        }
        
        // Diamond trail - light blue
        if (idLower.contains("diamond")) {
            return new float[]{0.5f, 0.8f, 1.0f}; // Light blue
        }
        
        // Netherite trail - dark purple
        if (idLower.contains("netherite")) {
            return new float[]{0.3f, 0.1f, 0.4f}; // Dark purple
        }
        
        // Rainbow trail - cycles through colors (use white as base)
        if (idLower.contains("rainbow")) {
            // Will be handled with color cycling in the particle itself
            return new float[]{1.0f, 1.0f, 1.0f}; // White (will cycle)
        }
        
        // Flame trail - orange/red
        if (idLower.contains("flame")) {
            return new float[]{1.0f, 0.4f, 0.1f}; // Orange-red
        }
        
        // Ice trail - light blue/cyan
        if (idLower.contains("ice")) {
            return new float[]{0.7f, 0.9f, 1.0f}; // Light blue/cyan
        }
        
        // Snowflake trail - white/light blue
        if (idLower.contains("snowflake")) {
            return new float[]{0.9f, 0.95f, 1.0f}; // White/light blue
        }
        
        // Default - white/cyan sparkle
        return new float[]{0.5f, 1.0f, 1.0f}; // Cyan
    }
    
    /**
     * Spawn particles behind the player.
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        Player player = mc.player;
        UUID playerUuid = player.getUUID();
        
        // Get equipped cosmetics from SupportersTabState (includes both slots and non-slot cosmetics)
        // This is the source of truth for what's currently equipped
        com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState state = 
            com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState.getInstance();
        
        // Get all equipped cosmetics (both slot-based and non-slot like particle trails)
        Set<String> equippedCosmeticsSet = state.getEquippedCosmetics();
        
        // Also check slot-based cosmetics from config (for backwards compatibility)
        CosmeticsConfig config = CosmeticsConfig.get();
        Map<Integer, String> equippedCosmeticsBySlot = config.getEquippedCosmetics(playerUuid);
        
        // Check if player has any particle trail cosmetics
        // Check equipped set first (includes all equipped cosmetics)
        boolean hasParticleTrail = false;
        String particleTrailId = null;
        
        for (String cosmeticId : equippedCosmeticsSet) {
            if (isParticleTrail(cosmeticId)) {
                hasParticleTrail = true;
                particleTrailId = cosmeticId;
                break;
            }
        }
        
        // Also check slot-based cosmetics (for backwards compatibility)
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
            // Clear tracking if no particle trail equipped
            lastPositions.remove(playerUuid);
            lastSpawnTimes.remove(playerUuid);
            return;
        }
        
        // Get current position
        Vec3 currentPos = player.position();
        Vec3 lastPos = lastPositions.get(playerUuid);
        
        // Initialize last position if first time
        if (lastPos == null) {
            lastPositions.put(playerUuid, currentPos);
            return;
        }
        
        // Check if player has moved enough
        double distance = currentPos.distanceTo(lastPos);
        if (distance < MIN_MOVEMENT_DISTANCE) {
            // Update last position but don't spawn particles
            lastPositions.put(playerUuid, currentPos);
            return;
        }
        
        // Check spawn rate limit
        long currentTime = System.currentTimeMillis();
        Long lastSpawnTime = lastSpawnTimes.get(playerUuid);
        if (lastSpawnTime != null && (currentTime - lastSpawnTime) < SPAWN_INTERVAL_MS) {
            // Update last position but don't spawn particles yet
            lastPositions.put(playerUuid, currentPos);
            return;
        }
        
        // Calculate spawn position (behind player, at feet level)
        Vec3 direction = currentPos.subtract(lastPos).normalize();
        Vec3 spawnPos = lastPos.add(0, 0.1, 0); // Slightly above ground
        
        // Add some randomness to spawn position
        Random random = mc.level.random;
        double offsetX = (random.nextDouble() - 0.5) * 0.3;
        double offsetZ = (random.nextDouble() - 0.5) * 0.3;
        spawnPos = spawnPos.add(offsetX, 0, offsetZ);
        
        // Get particle type and color
        ParticleOptions particleType = getParticleType(particleTrailId);
        
        // Get stored color from config, or use default color
        CosmeticsConfig cosmeticsConfig = CosmeticsConfig.get();
        String storedColor = cosmeticsConfig.getCosmeticColor(playerUuid, particleTrailId);
        float[] color;
        
        if (storedColor != null && !storedColor.isEmpty()) {
            // Parse stored hex color
            try {
                String hex = storedColor.startsWith("#") ? storedColor.substring(1) : storedColor;
                int rgb = Integer.parseInt(hex, 16);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                color = new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
            } catch (NumberFormatException e) {
                // Invalid color, use default
                color = getParticleColor(particleTrailId);
            }
        } else {
            // Use default color based on cosmetic ID
            color = getParticleColor(particleTrailId);
        }
        
        // Spawn particle with velocity opposite to movement direction
        // This makes particles spread behind the player
        // Particles glow up first, then fall down (pulsing effect)
        double velocityX = -direction.x * 0.1 + (random.nextDouble() - 0.5) * 0.05;
        double velocityY = 0.08 + random.nextDouble() * 0.05; // Start with upward velocity (glow up)
        double velocityZ = -direction.z * 0.1 + (random.nextDouble() - 0.5) * 0.05;
        
        // Queue color for the particle (if using PillarSparkleParticle and supports color)
        CosmeticManager manager = CosmeticManager.getInstance();
        if (particleType == ModParticles.GLOW_LIME_SPARKLE.get() && manager.supportsColor(particleTrailId)) {
            String colorCode = String.format("#%02X%02X%02X", 
                (int)(color[0] * 255), 
                (int)(color[1] * 255), 
                (int)(color[2] * 255));
            com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
                spawnPos.x, spawnPos.y, spawnPos.z, colorCode);
        }
        
        // Spawn the particle
        try {
            mc.level.addParticle(particleType, 
                spawnPos.x, spawnPos.y, spawnPos.z,
                velocityX, velocityY, velocityZ);
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to spawn particle trail: " + e.getMessage());
        }
        
        // Update tracking
        lastPositions.put(playerUuid, currentPos);
        lastSpawnTimes.put(playerUuid, currentTime);
    }
    
    /**
     * Clear tracking when player disconnects or world unloads.
     */
    public static void clearTracking() {
        lastPositions.clear();
        lastSpawnTimes.clear();
    }
}

