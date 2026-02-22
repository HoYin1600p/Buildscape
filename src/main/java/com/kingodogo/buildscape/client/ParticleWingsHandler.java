package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.particle.SnowflakeParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * Handles particle wings cosmetics using snowflake particles.
 * Spawns particles in elytra wing shape that animates with player movement.
 */
// DEPRECATED: Use CosWings cosmetic rendered via CosmeticLayer instead
// This handler is kept for backwards compatibility but is disabled
//@Mod.EventBusSubscriber(modid = com.kingodogo.buildscape.BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
//@OnlyIn(Dist.CLIENT)
public class ParticleWingsHandler {

    private static final Map<UUID, Float> wingAngles = new HashMap<>();
    private static final Map<UUID, Long> lastSpawnTime = new HashMap<>();
    private static final long SPAWN_INTERVAL = 30; // Spawn particles every 30ms for smooth animation

    /**
     * Check if a cosmetic ID is a particle wings cosmetic.
     */
    public static boolean isParticleWings(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) return false;
        CosmeticManager.CosmeticMetadata meta = CosmeticManager.getInstance().getMetadata(cosmeticId);
        return meta != null && meta.type() == CosmeticManager.CosmeticType.PARTICLE_WINGS;
    }

    public static boolean supportsColor(String cosmeticId) {
        return cosmeticId != null && cosmeticId.toLowerCase().contains("snowflake");
    }

    private static float[] getDefaultColor(String cosmeticId) {
        if (cosmeticId != null && cosmeticId.toLowerCase().contains("snowflake")) {
            return new float[]{0.85f, 0.95f, 1.0f};
        }
        return new float[]{1.0f, 1.0f, 1.0f};
    }

    /**
     * Render wings on player - called after player is rendered.
     */
    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getPlayer();
        if (!(player instanceof net.minecraft.client.player.AbstractClientPlayer)) return;

        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) return; // Only render for local player
        if (mc.level == null) return;

        UUID playerUuid = player.getUUID();

        // Get equipped cosmetics
        var state = com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState.getInstance();
        Set<String> equippedCosmetics = state.getEquippedCosmetics();

        String particleWingsId = null;
        for (String cosmeticId : equippedCosmetics) {
            if (isParticleWings(cosmeticId)) {
                particleWingsId = cosmeticId;
                break;
            }
        }

        if (particleWingsId == null) {
            Map<Integer, String> slotCosmetics = state.getEquippedCosmeticsBySlot();
            for (String cosmeticId : slotCosmetics.values()) {
                if (isParticleWings(cosmeticId)) {
                    particleWingsId = cosmeticId;
                    break;
                }
            }
        }

        if (particleWingsId == null) {
            wingAngles.remove(playerUuid);
            lastSpawnTime.remove(playerUuid);
            return;
        }

        // Calculate wing angle
        float targetWingAngle = calculateElytraWingAngle(player);
        float currentAngle = wingAngles.getOrDefault(playerUuid, targetWingAngle);
        currentAngle = currentAngle + (targetWingAngle - currentAngle) * 0.12f;
        wingAngles.put(playerUuid, currentAngle);

        // Get color
        CosmeticsConfig config = CosmeticsConfig.get();
        String storedColor = config.getCosmeticColor(playerUuid, particleWingsId);
        float[] color = getDefaultColor(particleWingsId);

        if (storedColor != null && !storedColor.isEmpty()) {
            try {
                String hex = storedColor.startsWith("#") ? storedColor.substring(1) : storedColor;
                int rgb = Integer.parseInt(hex, 16);
                color = new float[]{
                    ((rgb >> 16) & 0xFF) / 255.0f,
                    ((rgb >> 8) & 0xFF) / 255.0f,
                    (rgb & 0xFF) / 255.0f
                };
            } catch (NumberFormatException ignored) {}
        }

        // Spawn wing particles at intervals
        long now = System.currentTimeMillis();
        long lastSpawn = lastSpawnTime.getOrDefault(playerUuid, 0L);
        if (now - lastSpawn >= SPAWN_INTERVAL) {
            spawnWingParticles(mc, player, currentAngle, color);
            lastSpawnTime.put(playerUuid, now);
        }
    }

    /**
     * Calculate wing angle like real elytra.
     */
    private static float calculateElytraWingAngle(Player player) {
        boolean isMoving = false;
        boolean isSprinting = player.isSprinting();
        boolean isSneaking = player.isCrouching();
        boolean isOnGround = player.isOnGround();
        boolean isFalling = !isOnGround && player.getDeltaMovement().y < -0.1;
        boolean isJumping = !isOnGround && player.getDeltaMovement().y > 0.1;

        if (player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
            isMoving = localPlayer.input.forwardImpulse != 0 || localPlayer.input.leftImpulse != 0;
        }

        long time = System.currentTimeMillis();

        if (isFalling) {
            return 70.0f + (float) Math.sin(time * 0.008) * 8.0f;
        } else if (isJumping) {
            return 55.0f;
        } else if (isSneaking) {
            return 5.0f + (float) Math.sin(time * 0.002) * 2.0f;
        } else if (isSprinting) {
            return 40.0f + (float) Math.sin(time * 0.015) * 25.0f;
        } else if (isMoving) {
            return 25.0f + (float) Math.sin(time * 0.008) * 15.0f;
        } else {
            return 12.0f + (float) Math.sin(time * 0.002) * 4.0f;
        }
    }

    /**
     * Spawn particles in wing shape.
     */
    private static void spawnWingParticles(Minecraft mc, Player player, float wingAngleDegrees, float[] color) {
        float bodyYaw = player.yBodyRot;
        double yawRad = Math.toRadians(bodyYaw);

        // Direction vectors
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);

        // Wing attachment point
        Vec3 playerEyes = player.getEyePosition();
        double attachX = playerEyes.x - forwardX * 0.35;
        double attachY = playerEyes.y - 0.4;
        double attachZ = playerEyes.z - forwardZ * 0.35;

        float wingAngleRad = (float) Math.toRadians(wingAngleDegrees);
        float sin_angle = (float) Math.sin(wingAngleRad);
        float cos_angle = (float) Math.cos(wingAngleRad);

        // Spawn left wing particles
        spawnWingShape(mc, attachX, attachY, attachZ, rightX, rightZ, forwardX, forwardZ, sin_angle, cos_angle, 1.0f, color);
        // Spawn right wing particles
        spawnWingShape(mc, attachX, attachY, attachZ, rightX, rightZ, forwardX, forwardZ, sin_angle, cos_angle, -1.0f, color);
    }

    /**
     * Spawn particles in a single wing shape.
     */
    private static void spawnWingShape(Minecraft mc, double centerX, double centerY, double centerZ,
            double rightX, double rightZ, double forwardX, double forwardZ,
            float sin_angle, float cos_angle, float side, float[] color) {

        // Wing outline points (2D coordinates) - proper elytra shape
        // Top edge goes out and up, bottom edge extends down
        float[] xPoints = {
            0.1f, 0.4f, 0.8f, 1.2f, 1.6f,  // Top leading edge
            0.05f, 0.3f, 0.7f, 1.1f, 1.5f, // Middle outline
            0.0f, 0.2f, 0.6f, 1.0f, 1.4f   // Bottom trailing edge
        };
        float[] yPoints = {
            1.0f, 1.15f, 1.1f, 0.9f, 0.5f,   // Top edge (higher)
            0.5f, 0.4f, 0.3f, 0.1f, -0.2f,   // Middle (curves down)
            -0.3f, -0.2f, -0.3f, -0.4f, -0.5f // Bottom trailing edge (extends down)
        };

        for (int i = 0; i < xPoints.length; i++) {
            float x = xPoints[i];
            float y = yPoints[i];

            // Apply wing angle rotation
            float rotY = sin_angle * x;
            float rotZ = cos_angle * x;

            // Convert to 3D world position
            double worldX = centerX + rightX * side * rotZ;
            double worldY = centerY + rotY + y;
            double worldZ = centerZ + rightZ * side * rotZ;

            // Spawn snowflake particle
            try {
                if (mc.level != null && mc.particleEngine != null) {
                    // Convert color to hex string
                    int r = (int)(color[0] * 255);
                    int g = (int)(color[1] * 255);
                    int b = (int)(color[2] * 255);
                    String hexColor = String.format("#%02X%02X%02X", r, g, b);

                    // Queue color before spawning
                    SnowflakeParticle.queueColor(worldX, worldY, worldZ, hexColor);

                    Particle particle = mc.particleEngine.createParticle(
                        com.kingodogo.buildscape.particle.ModParticles.SNOWFLAKE.get(),
                        worldX, worldY, worldZ,
                        0, 0, 0
                    );
                }
            } catch (Exception e) {
                // Silently fail if particle can't be spawned
            }
        }
    }

    public static void clearTracking() {
        wingAngles.clear();
        lastSpawnTime.clear();
    }
}
