package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.*;

/**
 * Professional wing renderer using a persistent animated particle plane system.
 * Particles are laid out on an invisible animated plane that moves with the player.
 * No more than 2 particles spawn at the same position.
 */
public class WingRenderer {

    // Per-player wing plane data
    private static final Map<UUID, WingPlane> wingPlanes = new HashMap<>();
    private static final int PARTICLES_PER_WING = 120;  // Increased from 40 for better density
    private static final int MAX_PARTICLES_PER_POS = 2; // Max 2 particles at same position
    private static final double WING_X_MAX = 1.8;       // Wing extends 1.8 blocks horizontally
    private static final double WING_Y_MAX = 1.3;       // Wing height
    private static final double WING_Y_MIN = -0.8;      // Wing bottom

    /**
     * Represents an animated particle plane for one player's wings.
     */
    private static class WingPlane {
        UUID playerId;
        List<ParticleInfo> leftWingParticles = new ArrayList<>();
        List<ParticleInfo> rightWingParticles = new ArrayList<>();

        WingPlane(UUID playerId) {
            this.playerId = playerId;
        }
    }

    /**
     * Stores information about a single particle in the wing plane.
     */
    private static class ParticleInfo {
        double baseX, baseY, baseZ;  // Position on the plane relative to attachment point
        Particle particle;           // The actual particle entity

        ParticleInfo(double x, double y, double z) {
            this.baseX = x;
            this.baseY = y;
            this.baseZ = z;
        }
    }

    /**
     * Check if player has wings equipped and render them.
     */
    public static void renderWingsForPlayer(AbstractClientPlayer player, float ageInTicks) {
        // Check if player has wings equipped
        if (!hasWingsEquipped(player)) {
            // Clean up if player no longer has wings
            wingPlanes.remove(player.getUUID());
            return;
        }

        renderWings(player, ageInTicks);
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
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail
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
     * Main rendering logic for animated wing planes.
     */
    private static void renderWings(Player player, float ageInTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.particleEngine == null) return;

        UUID playerId = player.getUUID();

        // Get or create wing plane for this player
        WingPlane plane = wingPlanes.computeIfAbsent(playerId, WingPlane::new);

        // Initialize wing particles on first load
        if (plane.leftWingParticles.isEmpty() && plane.rightWingParticles.isEmpty()) {
            initializeWingParticles(plane);
        }

        // Update particle positions based on current player state and animation
        updateWingPlane(mc, plane, player, ageInTicks);
    }

    /**
     * Initialize the particle positions on the wing plane.
     * Particles are randomly distributed but never more than 2 at same position.
     */
    private static void initializeWingParticles(WingPlane plane) {
        Random random = new Random();

        // Create particle positions for left wing
        createWingParticles(plane.leftWingParticles, random);

        // Create particle positions for right wing (mirror of left)
        createWingParticles(plane.rightWingParticles, random);
    }

    /**
     * Create particle positions for a single wing.
     */
    private static void createWingParticles(List<ParticleInfo> wingParticles, Random random) {
        int particlesAdded = 0;
        int maxAttempts = PARTICLES_PER_WING * 10;
        int attempts = 0;

        while (particlesAdded < PARTICLES_PER_WING && attempts < maxAttempts) {
            // Random position on wing plane
            double x = random.nextDouble() * WING_X_MAX;
            double y = WING_Y_MIN + random.nextDouble() * (WING_Y_MAX - WING_Y_MIN);
            double z = (random.nextDouble() - 0.5) * 0.4; // Slight spread perpendicular to wing

            String posKey = String.format("%.1f,%.1f", x, y);

            // Count particles at this position
            int count = (int) wingParticles.stream()
                    .filter(p -> String.format("%.1f,%.1f", p.baseX, p.baseY).equals(posKey))
                    .count();

            // Only add if not more than 2 particles at this position
            if (count < MAX_PARTICLES_PER_POS) {
                wingParticles.add(new ParticleInfo(x, y, z));
                particlesAdded++;
            }

            attempts++;
        }
    }

    /**
     * Update the wing plane position and animation.
     * The plane follows the player and animates based on movement state.
     */
    private static void updateWingPlane(Minecraft mc, WingPlane plane, Player player, float ageInTicks) {
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

        // Convert wing angle to radians for rotation
        float wingAngleRad = (float) Math.toRadians(wingAngle);
        float sinAngle = (float) Math.sin(wingAngleRad);
        float cosAngle = (float) Math.cos(wingAngleRad);

        // Update left wing particles (positioned to the left/up from center, side = 1.0)
        updateWing(mc, plane.leftWingParticles, attachX, attachY, attachZ,
                   rightX, rightZ, forwardX, forwardZ, sinAngle, cosAngle, 1.0f);

        // Update right wing particles (positioned to the right/up from center, side = -1.0)
        updateWing(mc, plane.rightWingParticles, attachX, attachY, attachZ,
                   rightX, rightZ, forwardX, forwardZ, sinAngle, cosAngle, -1.0f);
    }

    /**
     * Update positions of particles in one wing.
     * Particles move with the plane as it rotates.
     */
    private static void updateWing(Minecraft mc, List<ParticleInfo> particles,
                                   double centerX, double centerY, double centerZ,
                                   double rightX, double rightZ,
                                   double forwardX, double forwardZ,
                                   float sinAngle, float cosAngle, float side) {

        for (ParticleInfo info : particles) {
            // Distance along the wing (x-axis in plane)
            double distAlongWing = info.baseX;

            // Height on the wing (y-axis in plane)
            double heightOnWing = info.baseY;

            // Perpendicular spread (z-axis in plane)
            double spreadZ = info.baseZ;

            // Apply wing angle rotation around the attachment point (x-axis rotation)
            // rotZ = how far the particle extends outward from the body due to wing opening
            // rotY = how much the particle rises/falls due to wing rotation
            float rotZ = cosAngle * (float)distAlongWing;
            float rotY = sinAngle * (float)distAlongWing;

            // Transform from wing plane coordinates to world coordinates
            // Start with the attachment point
            double worldX = centerX;
            double worldY = centerY + heightOnWing + rotY;  // Apply height offset + rotation effect
            double worldZ = centerZ;

            // Move outward along the wing (rightX direction is outward for each side)
            // side parameter: 1.0 for left wing, -1.0 for right wing
            worldX += rightX * side * rotZ;
            worldZ += rightZ * side * rotZ;

            // Add perpendicular spread (slightly forward/backward)
            worldX += forwardX * spreadZ;
            worldZ += forwardZ * spreadZ;

            // Spawn particle if it doesn't exist or has expired
            if (info.particle == null || !info.particle.isAlive()) {
                try {
                    if (mc.level != null && mc.particleEngine != null) {
                        info.particle = mc.particleEngine.createParticle(
                            ModParticles.SNOWFLAKE.get(),
                            worldX, worldY, worldZ,
                            0.0, 0.0, 0.0  // No velocity
                        );
                    }
                } catch (Exception e) {
                    // Silently fail
                }
            } else {
                // Update existing particle position - this is the key to smooth animation
                try {
                    info.particle.setPos(worldX, worldY, worldZ);
                } catch (Exception e) {
                    // Particle may have been removed, will respawn next frame
                    info.particle = null;
                }
            }
        }
    }

    /**
     * Calculate wing animation angle based on player movement state.
     */
    private static float calculateWingAngle(Player player, float ageInTicks) {
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
            return 0.0f;
        } else if (isFalling) {
            return 70.0f + (float) Math.sin(ageInTicks * 0.012f) * 10.0f;
        } else if (isJumping) {
            return 60.0f;
        } else if (isSprinting) {
            return 45.0f + (float) Math.sin(ageInTicks * 0.015f) * 20.0f;
        } else if (isMoving) {
            return 30.0f + (float) Math.sin(ageInTicks * 0.008f) * 12.0f;
        } else {
            return 12.0f + (float) Math.sin(ageInTicks * 0.003f) * 5.0f;
        }
    }
}
