package com.kingodogo.buildscape.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

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

    /**
     * Get spawn amount for a cosmetic ID.
     * Controls density/frequency.
     * < 1.0 : Chance to spawn (e.g. 0.8 = 80% chance)
     * >= 1.0 : Number of particles to spawn
     */
    private static float getSpawnAmount(String cosmeticId) {
        if (cosmeticId == null)
            return 1.0f;
        String idLower = cosmeticId.toLowerCase();

        // Cherry-related and Cake trails spawn slightly less often (80% chance per tick)
        if (idLower.contains("cherry") || idLower.contains("cake")) {
            return 0.8f;
        }

        return 1.0f;
    }

    /**
     * Get velocity multiplier for a cosmetic ID.
     * Controls how far the particles go (spread/speed).
     */
    private static float getVelocityMultiplier(String cosmeticId) {
        return 1.0f; // Default multiplier
    }

    // Minimum movement distance to spawn particles (prevents spawning when standing
    // still)
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
            case "bubble":
                return ModParticles.BUBBLE.get();
            case "cherry_leaves":
                return ModParticles.CHERRY.get();
            case "heart":
                return ModParticles.TINTABLE_HEART.get();
            case "note":
                return ModParticles.TRAIL_NOTE.get();
            case "snowflake":
                return ModParticles.SNOWFLAKE.get();
            case "firework":
                // Use actual firework particle
                return ParticleTypes.FIREWORK;
            case "cake":
                return ModParticles.CAKE.get();
            case "cherry":
                return ModParticles.CHERRY.get();
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
            return new float[] { 1.0f, 1.0f, 1.0f }; // White
        }

        String idLower = cosmeticId.toLowerCase();

        // Snowflake trail - white/light blue
        if (idLower.contains("snowflake")) {
            return new float[] { 0.9f, 0.95f, 1.0f }; // White/light blue
        }

        // Heart trail - red
        if (idLower.contains("heart")) {
            return new float[] { 1.0f, 0.0f, 0.0f }; // Red
        }

        // Cherry/Cherry Leaves trail - Pink
        if (idLower.contains("cherry")) {
            return new float[] { 1.0f, 0.7f, 0.8f }; // Pink
        }

        // Cake trail - Cream/White
        if (idLower.contains("cake")) {
            return new float[] { 1.0f, 0.9f, 0.8f }; // Creamy White
        }

        // Default - Blue sparkle (as requested)
        return new float[] { 0.2f, 0.5f, 1.0f }; // Blue
    }

    /**
     * Spawn particles behind the player.
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
            return;

        Player player = mc.player;
        UUID playerUuid = player.getUUID();

        // Get equipped cosmetics from SupportersTabState (includes both slots and
        // non-slot cosmetics)
        // This is the source of truth for what's currently equipped
        com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState state = com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState
                .getInstance();

        // Get all equipped cosmetics (both slot-based and non-slot like particle
        // trails)
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

        // Check if player is climbing (don't spawn particles)
        if (player.onClimbable()) {
            lastPositions.put(playerUuid, currentPos);
            return;
        }

        // Check if moving forward (only spawn when walking/sprinting forward)
        // We need to check input for local player
        if (player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
            if (localPlayer.input.forwardImpulse <= 0) {
                // Not moving forward (could be backwards, strafing only, or standing still)
                lastPositions.put(playerUuid, currentPos);
                return;
            }
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
        // Use player's look vector for direction if moving forward
        Vec3 lookVec = player.getLookAngle();
        Vec3 direction = new Vec3(lookVec.x, 0, lookVec.z).normalize();
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
                color = new float[] { r / 255.0f, g / 255.0f, b / 255.0f };
            } catch (NumberFormatException e) {
                // Invalid color, use default
                color = getParticleColor(particleTrailId);
            }
        } else {
            // Use default color based on cosmetic ID
            color = getParticleColor(particleTrailId);
        }

        // Get spawn amount and velocity parameters
        float spawnAmount = getSpawnAmount(particleTrailId);
        float velocityMult = getVelocityMultiplier(particleTrailId);

        // Spawn particle with velocity opposite to movement direction
        // This makes particles spread behind the player
        // Particles glow up first, then fall down (pulsing effect)
        double velocityX = (-direction.x * 0.1 + (random.nextDouble() - 0.5) * 0.05) * velocityMult;
        double velocityY = (0.08 + random.nextDouble() * 0.05) * velocityMult; // Start with upward velocity (glow up)
        double velocityZ = (-direction.z * 0.1 + (random.nextDouble() - 0.5) * 0.05) * velocityMult;

        // Queue color for the particle (if supported)
        CosmeticManager manager = CosmeticManager.getInstance();
        if (manager.supportsColor(particleTrailId)) {
            String colorCode = String.format("#%02X%02X%02X",
                    (int) (color[0] * 255),
                    (int) (color[1] * 255),
                    (int) (color[2] * 255));

            if (particleType == ModParticles.GLOW_LIME_SPARKLE.get()) {
                com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
                        spawnPos.x, spawnPos.y, spawnPos.z, colorCode);
            } else if (particleType == ModParticles.TINTABLE_HEART.get()) {
                com.kingodogo.buildscape.particle.TintableHeartParticle.queueColor(
                        spawnPos.x, spawnPos.y, spawnPos.z, colorCode);
            } else if (particleType == ModParticles.TRAIL_NOTE.get()) {
                com.kingodogo.buildscape.particle.TrailNoteParticle.queueColor(
                        spawnPos.x, spawnPos.y, spawnPos.z, colorCode);
            }
        }

        // Determine number of particles to spawn based on spawnAmount
        int count = (int) spawnAmount;
        // Handle fractional part as probability
        if (random.nextFloat() < (spawnAmount - count)) {
            count++;
        }

        // Spawn the particle(s)
        try {
            for (int i = 0; i < count; i++) {
                // Slightly randomize spawn position for multiple particles
                double px = spawnPos.x;
                double pz = spawnPos.z;
                
                if (count > 1) {
                    px += (random.nextDouble() - 0.5) * 0.1;
                    pz += (random.nextDouble() - 0.5) * 0.1;
                }

                // Standard single particle for all types (including bubble, note, and cherry)
                // Cherry particles will randomize their shape internally (in CherryParticle class)
                mc.level.addParticle(particleType,
                        px, spawnPos.y, pz,
                        velocityX, velocityY, velocityZ);
            }
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
