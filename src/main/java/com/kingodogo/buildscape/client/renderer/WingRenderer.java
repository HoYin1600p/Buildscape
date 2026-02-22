package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Professional wing renderer that spawns particles in animated elytra wing shapes.
 * Properly handles animation states and particle lifecycle.
 */
public class WingRenderer {

    private static final Map<UUID, Long> lastSpawnTime = new HashMap<>();
    private static final long SPAWN_INTERVAL = 40; // Spawn every 40ms for smooth animation

    /**
     * Check if player has wings equipped and render them.
     */
    public static void renderWingsForPlayer(AbstractClientPlayer player, float ageInTicks) {
        // Check if player has wings equipped
        if (!hasWingsEquipped(player)) {
            return;
        }

        renderPlayerWings(player, ageInTicks);
    }

    /**
     * Check if a player has any wings cosmetic equipped.
     */
    private static boolean hasWingsEquipped(Player player) {
        try {
            var state = com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState.getInstance();
            Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();

            // Check all slots for wings
            if (equippedBySlot != null && !equippedBySlot.isEmpty()) {
                for (Map.Entry<Integer, String> entry : equippedBySlot.entrySet()) {
                    String cosmeticId = entry.getValue();
                    if (cosmeticId != null && !cosmeticId.isEmpty()) {
                        if (isWingsCosmetic(cosmeticId)) {
                            com.kingodogo.buildscape.BuildScape.getLogger().debug("Wings equipped in slot " + entry.getKey() + ": " + cosmeticId);
                            return true;
                        }
                    }
                }
            }

            // Also check direct equipped cosmetics set
            var equippedSet = state.getEquippedCosmetics();
            if (equippedSet != null && !equippedSet.isEmpty()) {
                for (String cosmeticId : equippedSet) {
                    if (cosmeticId != null && !cosmeticId.isEmpty() && isWingsCosmetic(cosmeticId)) {
                        com.kingodogo.buildscape.BuildScape.getLogger().debug("Wings found in equipped set: " + cosmeticId);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().error("Error checking if wings equipped", e);
        }
        return false;
    }

    /**
     * Check if a cosmetic ID is a wings cosmetic.
     */
    private static boolean isWingsCosmetic(String cosmeticId) {
        try {
            CosmeticManager manager = CosmeticManager.getInstance();
            CosmeticManager.CosmeticMetadata meta = manager.getMetadata(cosmeticId);
            return meta != null && meta.type() == CosmeticManager.CosmeticType.PARTICLE_WINGS;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Render wings for a player with proper animation.
     */
    public static void renderPlayerWings(Player player, float ageInTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.particleEngine == null) return;

        UUID playerUuid = player.getUUID();
        long now = System.currentTimeMillis();
        long lastSpawn = lastSpawnTime.getOrDefault(playerUuid, 0L);

        // Only spawn particles at intervals
        if (now - lastSpawn < SPAWN_INTERVAL) {
            return;
        }
        lastSpawnTime.put(playerUuid, now);

        com.kingodogo.buildscape.BuildScape.getLogger().debug("Spawning wing particles for player");

        // Calculate wing animation angle
        float wingAngle = calculateWingAngle(player, ageInTicks);

        // Get player position and rotation
        double playerX = player.getX();
        double playerY = player.getY() + 1.2; // Upper body
        double playerZ = player.getZ();

        float bodyYaw = player.yBodyRot;
        double yawRad = Math.toRadians(bodyYaw);

        // Direction vectors based on player facing
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);

        double forwardX = -sinYaw;
        double forwardZ = cosYaw;
        double rightX = cosYaw;
        double rightZ = sinYaw;

        // Wing attachment point on player's back
        double attachX = playerX - forwardX * 0.35;
        double attachY = playerY - 0.4;
        double attachZ = playerZ - forwardZ * 0.35;

        // Convert wing angle to radians for rotation calculations
        float wingAngleRad = (float) Math.toRadians(wingAngle);
        float sinAngle = (float) Math.sin(wingAngleRad);
        float cosAngle = (float) Math.cos(wingAngleRad);

        // Spawn left and right wings
        spawnWingParticles(mc, attachX, attachY, attachZ, rightX, rightZ, sinAngle, cosAngle, 1.0f);  // Left wing
        spawnWingParticles(mc, attachX, attachY, attachZ, rightX, rightZ, sinAngle, cosAngle, -1.0f); // Right wing
    }

    /**
     * Spawn particles to form one wing shape.
     */
    private static void spawnWingParticles(Minecraft mc, double centerX, double centerY, double centerZ,
            double rightX, double rightZ, float sinAngle, float cosAngle, float side) {

        // Wing outline - defines elytra wing shape
        // Each point is [x-distance, y-height] from attachment
        float[][] wingPoints = {
            // Top feather row
            {0.0f, 1.0f},
            {0.3f, 1.15f},
            {0.6f, 1.1f},
            {0.9f, 0.9f},
            {1.2f, 0.6f},
            {1.5f, 0.2f},
            // Middle feather row
            {0.0f, 0.5f},
            {0.3f, 0.5f},
            {0.6f, 0.4f},
            {0.9f, 0.2f},
            {1.2f, 0.0f},
            {1.5f, -0.2f},
            // Bottom feather row
            {0.0f, 0.0f},
            {0.3f, 0.0f},
            {0.6f, -0.1f},
            {0.9f, -0.3f},
            {1.2f, -0.5f},
            {1.5f, -0.7f}
        };

        // Spawn each point as a snowflake particle
        for (float[] point : wingPoints) {
            float x = point[0];
            float y = point[1];

            // Apply wing angle rotation (wings open/close around the attachment axis)
            float rotY = sinAngle * x;
            float rotZ = cosAngle * x;

            // Calculate world position with proper rotation
            double worldX = centerX + rightX * side * rotZ;
            double worldY = centerY + rotY + y;
            double worldZ = centerZ + rightZ * side * rotZ;

            // Spawn snowflake particle
            try {
                if (mc.level != null && mc.particleEngine != null) {
                    mc.particleEngine.createParticle(
                        ModParticles.SNOWFLAKE.get(),
                        worldX, worldY, worldZ,
                        0.0, 0.0, 0.0  // No velocity - particles spawn at exact position
                    );
                }
            } catch (Exception e) {
                // Silently fail
            }
        }
    }

    /**
     * Calculate wing animation angle based on player movement state.
     */
    private static float calculateWingAngle(Player player, float ageInTicks) {
        // Check player movement state
        boolean isSprinting = player.isSprinting();
        boolean isSneaking = player.isCrouching();
        boolean isOnGround = player.isOnGround();
        boolean isFalling = !isOnGround && player.getDeltaMovement().y < -0.1;
        boolean isJumping = !isOnGround && player.getDeltaMovement().y > 0.1;

        boolean isMoving = false;
        if (player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
            isMoving = localPlayer.input.forwardImpulse != 0 || localPlayer.input.leftImpulse != 0;
        }

        // Return wing angle based on state
        if (isSneaking) {
            // Sneaking: wings completely closed
            return 0.0f;
        } else if (isFalling) {
            // Falling: wings fully extended with fast flutter
            return 70.0f + (float) Math.sin(ageInTicks * 0.012f) * 10.0f;
        } else if (isJumping) {
            // Jumping: wings extended
            return 60.0f;
        } else if (isSprinting) {
            // Sprinting: wings open with fast flutter
            return 45.0f + (float) Math.sin(ageInTicks * 0.015f) * 20.0f;
        } else if (isMoving) {
            // Walking: wings open with moderate flutter
            return 30.0f + (float) Math.sin(ageInTicks * 0.008f) * 12.0f;
        } else {
            // Idle: wings slightly open with slow breathing motion
            return 12.0f + (float) Math.sin(ageInTicks * 0.003f) * 5.0f;
        }
    }
}
