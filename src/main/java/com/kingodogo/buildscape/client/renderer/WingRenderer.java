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
 * Creates recognizable feather-pattern wings similar to angel/elytra wings.
 */
public class WingRenderer {

    // Per-player wing plane data
    private static final Map<UUID, WingPlane> wingPlanes = new HashMap<>();
    private static final int PARTICLES_PER_WING = 180;  // More particles for detailed feather pattern

    // Feather row configuration
    private static final int FEATHER_ROWS = 9;          // 9 rows of feathers from top to bottom
    private static final int PARTICLES_PER_ROW = 20;    // ~20 particles per feather row

    // Wing dimensions (in feather layout)
    private static final double WING_LENGTH = 1.8;      // How far wing extends outward
    private static final double WING_HEIGHT = 1.5;      // Total height of wing
    private static final double ROW_SPACING = 0.18;     // Space between feather rows
    private static final double MIN_WING_GAP = 0.5;     // Minimum gap between left and right wing

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
     * Initialize the particle positions on the wing plane with feather pattern.
     */
    private static void initializeWingParticles(WingPlane plane) {
        // Create feather-pattern particles for both wings
        createFeatherWing(plane.leftWingParticles);
        createFeatherWing(plane.rightWingParticles);
    }

    /**
     * Create particles in a feather pattern for one wing.
     * Particles arranged in overlapping rows from top to bottom.
     */
    private static void createFeatherWing(List<ParticleInfo> wingParticles) {
        Random random = new Random();

        // Create 9 feather rows from top to bottom
        for (int row = 0; row < FEATHER_ROWS; row++) {
            // Y position for this row (top to bottom)
            double rowY = (WING_HEIGHT / 2.0) - (row * ROW_SPACING);

            // Each row has slightly different length (feather taper)
            // Top rows are shorter, middle rows are longest, bottom rows taper again
            double rowLength = WING_LENGTH;
            if (row < 3) {
                // Top rows shorter
                rowLength = WING_LENGTH * (0.6 + (row * 0.13));
            } else if (row > 5) {
                // Bottom rows shorter
                rowLength = WING_LENGTH * (1.0 - ((row - 5) * 0.15));
            }

            // Place particles along this feather row
            int particlesInRow = PARTICLES_PER_ROW;
            for (int i = 0; i < particlesInRow; i++) {
                // Position along the row (0 at body, rowLength at tip)
                double x = (i / (double) particlesInRow) * rowLength;

                // Add slight curve (feathers arc slightly)
                double curvature = Math.sin((i / (double) particlesInRow) * Math.PI) * 0.1;
                double y = rowY + curvature;

                // Perpendicular spread (feathers have thickness in z direction)
                double z = (random.nextDouble() - 0.5) * 0.08; // Very slight spread for feather texture

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
     * Maintains separation between left and right wings.
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

            // Move outward along the wing with minimum gap separation
            // The gap is enforced by the MIN_WING_GAP offset
            double gapOffset = MIN_WING_GAP * 0.5 * side; // Half gap on each side
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
            return 5.0f;  // Slightly open even when sneaking
        } else if (isFalling) {
            return 75.0f + (float) Math.sin(ageInTicks * 0.012f) * 12.0f;
        } else if (isJumping) {
            return 65.0f;
        } else if (isSprinting) {
            return 50.0f + (float) Math.sin(ageInTicks * 0.015f) * 22.0f;
        } else if (isMoving) {
            return 35.0f + (float) Math.sin(ageInTicks * 0.008f) * 15.0f;
        } else {
            return 18.0f + (float) Math.sin(ageInTicks * 0.003f) * 6.0f;
        }
    }
}
