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
 * Creates wings in a rectangular outline shape with clear vertical gap in middle.
 */
public class WingRenderer {

    // Per-player wing plane data
    private static final Map<UUID, WingPlane> wingPlanes = new HashMap<>();

    // Wing structure: vertical columns of particles
    private static final int WING_HEIGHT_PARTICLES = 14;  // 14 particles tall (0.14 blocks, ~1.4m)
    private static final int WING_WIDTH_PARTICLES = 10;   // 10 particles wide per wing (0.1 blocks, ~1m)
    private static final double CENTER_GAP = 0.6;         // 0.6 block gap in center (visible separation)

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
     * Creates rectangular wing outlines with a clear center gap.
     */
    private static void initializeWingParticles(WingPlane plane) {
        // Create rectangular wing outline for left wing
        createRectangularWing(plane.leftWingParticles, 1.0);  // Left side

        // Create rectangular wing outline for right wing
        createRectangularWing(plane.rightWingParticles, -1.0); // Right side
    }

    /**
     * Create particles in a rectangular wing outline.
     * Forms a clear shape with particles arranged in columns.
     *
     * @param wingParticles List to add particles to
     * @param side 1.0 for left wing, -1.0 for right wing
     */
    private static void createRectangularWing(List<ParticleInfo> wingParticles, double side) {
        // Wing dimensions
        double wingLength = 1.6;  // How far wing extends outward
        double wingHeight = 1.4;  // How tall the wing is

        // Create a grid of particles forming a rectangle
        // Particles arranged in columns from body outward
        for (int col = 0; col < WING_WIDTH_PARTICLES; col++) {
            // X position: distance outward from body (0 at body, wingLength at tip)
            double x = (col / (double) (WING_WIDTH_PARTICLES - 1)) * wingLength;

            // For each column, create particles from top to bottom
            for (int row = 0; row < WING_HEIGHT_PARTICLES; row++) {
                // Y position: from top to bottom of wing
                double y = (wingHeight / 2.0) - (row / (double) (WING_HEIGHT_PARTICLES - 1)) * wingHeight;

                // Small random offset for slight texture variation
                double z = (Math.random() - 0.5) * 0.04;

                // Add to wing (side parameter controls left/right via offset in updateWing)
                wingParticles.add(new ParticleInfo(x, y, z));
            }
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

        // Update left wing particles (side = 1.0, extends to the left)
        updateWing(mc, plane.leftWingParticles, attachX, attachY, attachZ,
                   rightX, rightZ, forwardX, forwardZ, sinAngle, cosAngle, 1.0f);

        // Update right wing particles (side = -1.0, extends to the right)
        updateWing(mc, plane.rightWingParticles, attachX, attachY, attachZ,
                   rightX, rightZ, forwardX, forwardZ, sinAngle, cosAngle, -1.0f);
    }

    /**
     * Update positions of particles in one wing.
     * Particles move with the plane as it rotates.
     * Maintains clear separation between left and right wings.
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

            // Move outward along the wing with CENTER_GAP separation
            // Each wing is offset by half the gap so they stay separated
            double gapOffset = CENTER_GAP * 0.5 * side; // Half gap on each side
            worldX += rightX * side * (rotZ + gapOffset);
            worldZ += rightZ * side * (rotZ + gapOffset);

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
        // Larger animation ranges ensure gap stays visible
        if (isSneaking) {
            return 8.0f;  // Slightly open even when sneaking
        } else if (isFalling) {
            return 80.0f + (float) Math.sin(ageInTicks * 0.012f) * 12.0f;
        } else if (isJumping) {
            return 70.0f;
        } else if (isSprinting) {
            return 55.0f + (float) Math.sin(ageInTicks * 0.015f) * 22.0f;
        } else if (isMoving) {
            return 40.0f + (float) Math.sin(ageInTicks * 0.008f) * 15.0f;
        } else {
            return 22.0f + (float) Math.sin(ageInTicks * 0.003f) * 7.0f;
        }
    }
}
