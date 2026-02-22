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
 * Creates wings with curved oval shape, rounded edges, clear center gap.
 */
public class WingRenderer {

    // Per-player wing plane data
    private static final Map<UUID, WingPlane> wingPlanes = new HashMap<>();
    private static final int PARTICLES_PER_WING = 200;  // ~200 particles for detailed curved shape
    private static final double WING_GAP_OFFSET = 0.3;  // Gap offset per wing (0.6 total gap)

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
     * Creates curved oval shaped wings with rounded edges and clear center gap.
     */
    private static void initializeWingParticles(WingPlane plane) {
        // Create curved wing for left side
        createCurvedWing(plane.leftWingParticles);

        // Create curved wing for right side
        createCurvedWing(plane.rightWingParticles);
    }

    /**
     * Create particles in a curved oval wing shape with rounded edges and clear center gap.
     * Uses mathematical functions to create smooth curves rather than boxes.
     *
     * Wing shape characteristics:
     * - Wider in the middle
     * - Narrower at top and bottom
     * - Rounded edges instead of sharp tapers
     * - Clear center gap with no particles crossing middle line
     */
    private static void createCurvedWing(List<ParticleInfo> wingParticles) {
        Random random = new Random();

        // Wing dimensions
        double wingLength = 1.7;  // How far wing extends outward
        double wingHeight = 1.6;  // How tall the wing is
        double maxWidth = 0.65;   // Widest part of wing (middle)

        // Create particles using polar-like coordinates
        // Generate particles along the wing from body to tip
        for (double lengthProgress = 0.0; lengthProgress <= 1.0; lengthProgress += 0.05) {
            // X position: distance outward (0 at body, wingLength at tip)
            double x = lengthProgress * wingLength;

            // Width of wing at this length position
            // Using sine curve with rounded edges (no sharp taper)
            double widthFactor = Math.sin(lengthProgress * Math.PI) * 0.9; // 0 to 0.9

            // Apply smoother rounding instead of sharp taper
            if (lengthProgress < 0.1) {
                // Gentle rounding at body, not sharp taper
                widthFactor *= Math.sin((lengthProgress / 0.1) * (Math.PI / 2.0)); // Smooth entrance
            }
            if (lengthProgress > 0.9) {
                // Gentle rounding at tip, not sharp taper
                widthFactor *= Math.sin(((1.0 - lengthProgress) / 0.1) * (Math.PI / 2.0)); // Smooth exit
            }

            double currentWidth = maxWidth * widthFactor;

            // For each x position, create a vertical arc of particles
            int particlesAtThisLength = (int) (16 * widthFactor) + 2; // More particles where wing is wider

            for (int i = 0; i < particlesAtThisLength; i++) {
                // Y position: distribute vertically across wing height
                double heightProgress = i / (double) (particlesAtThisLength + 1);
                double y = (wingHeight / 2.0) - (heightProgress * wingHeight);

                // Z position: spread perpendicular, using circular arc
                double zProgress = Math.abs(heightProgress - 0.5) * 2.0; // 0 at middle, 1 at edges
                double maxZAtThisY = currentWidth * Math.sqrt(1.0 - zProgress * zProgress); // Circular arc

                // Distribute particles across the full wing width
                double z = (random.nextDouble() - 0.5) * maxZAtThisY * 2.0; // Random within bounds

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

        // Wing attachment point on player's back (centered on spine)
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
     * Particles move with the plane as it rotates and animates.
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

            // Perpendicular spread (z-axis in plane) - creates the curved width
            double spreadZ = info.baseZ;

            // Apply wing angle rotation around the attachment point (x-axis rotation)
            // rotZ = how far the particle extends outward from the body due to wing opening
            // rotY = how much the particle rises/falls due to wing rotation
            float rotZ = cosAngle * (float)distAlongWing;
            float rotY = sinAngle * (float)distAlongWing;

            // Transform from wing plane coordinates to world coordinates
            double worldX = centerX;
            double worldY = centerY + heightOnWing + rotY;  // Apply height offset + rotation effect
            double worldZ = centerZ;

            // Move outward along the wing
            // Left wing (side=1.0) extends to left, right wing (side=-1.0) extends to right
            worldX += rightX * side * rotZ;
            worldZ += rightZ * side * rotZ;

            // Add perpendicular spread (the curved width of the wing)
            // This creates the oval shape
            worldX += rightX * side * spreadZ;  // Spread left-right maintains curve
            worldZ += rightZ * side * spreadZ;

            // Add gap offset between wings at the plane level
            // Left wing offset: +0.3, Right wing offset: -0.3 (creates 0.6 block gap)
            double gapOffset = WING_GAP_OFFSET * side;
            worldX += rightX * gapOffset;
            worldZ += rightZ * gapOffset;

            // Slight forward/backward variation for depth
            worldX += forwardX * (Math.random() - 0.5) * 0.02;
            worldZ += forwardZ * (Math.random() - 0.5) * 0.02;

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
                // Update existing particle position - smooth animation
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
            return 10.0f;
        } else if (isFalling) {
            return 82.0f + (float) Math.sin(ageInTicks * 0.012f) * 12.0f;
        } else if (isJumping) {
            return 72.0f;
        } else if (isSprinting) {
            return 58.0f + (float) Math.sin(ageInTicks * 0.015f) * 22.0f;
        } else if (isMoving) {
            return 42.0f + (float) Math.sin(ageInTicks * 0.008f) * 15.0f;
        } else {
            return 25.0f + (float) Math.sin(ageInTicks * 0.003f) * 8.0f;
        }
    }
}
