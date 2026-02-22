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
 * Creates wings with curved oval shape, rounded edges, center connector, colorable.
 */
public class WingRenderer {

    // Per-player wing plane data
    private static final Map<UUID, WingPlane> wingPlanes = new HashMap<>();
    private static final Map<UUID, float[]> playerWingColors = new HashMap<>(); // RGB color per player
    private static final int PARTICLES_PER_WING = 160;  // Slimmer wings: reduced from 200
    private static final double WING_GAP_OFFSET = 0.3;  // Gap offset per wing (0.6 total gap)
    private static final int CONNECTOR_PARTICLES = 15;  // Particles in center connector

    /**
     * Represents an animated particle plane for one player's wings.
     */
    private static class WingPlane {
        UUID playerId;
        List<ParticleInfo> leftWingParticles = new ArrayList<>();
        List<ParticleInfo> rightWingParticles = new ArrayList<>();
        List<ParticleInfo> connectorParticles = new ArrayList<>();  // Center connector

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
     * Set wing color for a player (RGB values 0.0-1.0)
     */
    public static void setWingColor(UUID playerId, float r, float g, float b) {
        playerWingColors.put(playerId, new float[]{r, g, b});
    }

    /**
     * Check if player has wings equipped and render them.
     */
    public static void renderWingsForPlayer(AbstractClientPlayer player, float ageInTicks) {
        // Check if player has wings equipped
        if (!hasWingsEquipped(player)) {
            // Clean up if player no longer has wings
            wingPlanes.remove(player.getUUID());
            playerWingColors.remove(player.getUUID());
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
     * Creates curved oval shaped wings with rounded edges, slimmer profile, and center connector.
     */
    private static void initializeWingParticles(WingPlane plane) {
        // Create curved wings for left and right sides (slimmer)
        createSlimWing(plane.leftWingParticles);
        createSlimWing(plane.rightWingParticles);

        // Create center connector plane
        createConnector(plane.connectorParticles);
    }

    /**
     * Create particles in a slimmer curved oval wing shape with rounded edges.
     * Reduced width for slimmer appearance from top view.
     */
    private static void createSlimWing(List<ParticleInfo> wingParticles) {
        Random random = new Random();

        // Slimmer wing dimensions (reduced width from 0.65 to 0.45)
        double wingLength = 1.7;   // How far wing extends outward
        double wingHeight = 1.6;   // How tall the wing is
        double maxWidth = 0.45;    // REDUCED from 0.65 - slimmer appearance

        // Create particles along the wing from body to tip
        for (double lengthProgress = 0.0; lengthProgress <= 1.0; lengthProgress += 0.06) {  // 0.06 instead of 0.05 for fewer particles
            // X position: distance outward
            double x = lengthProgress * wingLength;

            // Width of wing at this length position (slimmer overall)
            double widthFactor = Math.sin(lengthProgress * Math.PI) * 0.85; // Reduced from 0.9

            // Apply smooth rounding at ends
            if (lengthProgress < 0.1) {
                widthFactor *= Math.sin((lengthProgress / 0.1) * (Math.PI / 2.0));
            }
            if (lengthProgress > 0.9) {
                widthFactor *= Math.sin(((1.0 - lengthProgress) / 0.1) * (Math.PI / 2.0));
            }

            double currentWidth = maxWidth * widthFactor;

            // Fewer particles per row for slimmer look
            int particlesAtThisLength = (int) (12 * widthFactor) + 1;  // Reduced from 16

            for (int i = 0; i < particlesAtThisLength; i++) {
                // Y position: distribute vertically
                double heightProgress = i / (double) (particlesAtThisLength + 1);
                double y = (wingHeight / 2.0) - (heightProgress * wingHeight);

                // Z position: circular arc distribution
                double zProgress = Math.abs(heightProgress - 0.5) * 2.0;
                double maxZAtThisY = currentWidth * Math.sqrt(1.0 - zProgress * zProgress);
                double z = (random.nextDouble() - 0.5) * maxZAtThisY * 2.0;

                wingParticles.add(new ParticleInfo(x, y, z));
            }
        }
    }

    /**
     * Create particles for center connector plane between wings.
     * Small vertical plane connecting left and right wings.
     */
    private static void createConnector(List<ParticleInfo> connectorParticles) {
        Random random = new Random();

        double connectorHeight = 1.2;  // Height of connector
        double connectorWidth = 0.25;  // Width of connector (small)

        // Create particles in a small vertical plane
        for (int i = 0; i < CONNECTOR_PARTICLES; i++) {
            // X position: close to body (0.1 to 0.3 from center)
            double x = 0.1 + random.nextDouble() * 0.2;

            // Y position: distributed vertically across connector height
            double heightProgress = i / (double) (CONNECTOR_PARTICLES + 1);
            double y = (connectorHeight / 2.0) - (heightProgress * connectorHeight);

            // Z position: very small perpendicular spread (thin connector)
            double z = (random.nextDouble() - 0.5) * connectorWidth;

            connectorParticles.add(new ParticleInfo(x, y, z));
        }
    }

    /**
     * Update the wing plane position and animation.
     */
    private static void updateWingPlane(Minecraft mc, WingPlane plane, Player player, float ageInTicks) {
        // Calculate wing animation angle
        float wingAngle = calculateWingAngle(player, ageInTicks);

        // Get player position and rotation
        double playerX = player.getX();
        double playerY = player.getY() + 1.2;
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

        // Convert wing angle to radians
        float wingAngleRad = (float) Math.toRadians(wingAngle);
        float sinAngle = (float) Math.sin(wingAngleRad);
        float cosAngle = (float) Math.cos(wingAngleRad);

        // Get player's wing color (default white if not set)
        float[] wingColor = playerWingColors.getOrDefault(player.getUUID(), new float[]{1.0f, 1.0f, 1.0f});

        // Update left wing
        updateWing(mc, plane.leftWingParticles, attachX, attachY, attachZ,
                   rightX, rightZ, forwardX, forwardZ, sinAngle, cosAngle, 1.0f, wingColor);

        // Update right wing
        updateWing(mc, plane.rightWingParticles, attachX, attachY, attachZ,
                   rightX, rightZ, forwardX, forwardZ, sinAngle, cosAngle, -1.0f, wingColor);

        // Update connector (no rotation, just follows body)
        updateConnector(mc, plane.connectorParticles, attachX, attachY, attachZ,
                       rightX, rightZ, forwardX, forwardZ, wingColor);
    }

    /**
     * Update positions of particles in one wing.
     */
    private static void updateWing(Minecraft mc, List<ParticleInfo> particles,
                                   double centerX, double centerY, double centerZ,
                                   double rightX, double rightZ,
                                   double forwardX, double forwardZ,
                                   float sinAngle, float cosAngle, float side,
                                   float[] wingColor) {

        for (ParticleInfo info : particles) {
            // Distance along the wing
            double distAlongWing = info.baseX;
            double heightOnWing = info.baseY;
            double spreadZ = info.baseZ;

            // Apply wing angle rotation
            float rotZ = cosAngle * (float)distAlongWing;
            float rotY = sinAngle * (float)distAlongWing;

            // Transform to world coordinates
            double worldX = centerX;
            double worldY = centerY + heightOnWing + rotY;
            double worldZ = centerZ;

            worldX += rightX * side * rotZ;
            worldZ += rightZ * side * rotZ;

            worldX += rightX * side * spreadZ;
            worldZ += rightZ * side * spreadZ;

            // Add gap offset
            double gapOffset = WING_GAP_OFFSET * side;
            worldX += rightX * gapOffset;
            worldZ += rightZ * gapOffset;

            // Slight depth variation
            worldX += forwardX * (Math.random() - 0.5) * 0.02;
            worldZ += forwardZ * (Math.random() - 0.5) * 0.02;

            // Spawn/update particle with color
            if (info.particle == null || !info.particle.isAlive()) {
                try {
                    if (mc.level != null && mc.particleEngine != null) {
                        info.particle = mc.particleEngine.createParticle(
                            ModParticles.SNOWFLAKE.get(),
                            worldX, worldY, worldZ,
                            0.0, 0.0, 0.0);

                        // Apply color if particle supports it
                        if (info.particle != null) {
                            info.particle.setColor(wingColor[0], wingColor[1], wingColor[2]);
                        }
                    }
                } catch (Exception e) {
                    // Silently fail
                }
            } else {
                try {
                    info.particle.setPos(worldX, worldY, worldZ);
                    info.particle.setColor(wingColor[0], wingColor[1], wingColor[2]);
                } catch (Exception e) {
                    info.particle = null;
                }
            }
        }
    }

    /**
     * Update positions of connector particles (no rotation, just follow body).
     */
    private static void updateConnector(Minecraft mc, List<ParticleInfo> particles,
                                       double centerX, double centerY, double centerZ,
                                       double rightX, double rightZ,
                                       double forwardX, double forwardZ,
                                       float[] wingColor) {

        for (ParticleInfo info : particles) {
            // Connector stays centered, no rotation
            double heightOnConnector = info.baseY;
            double spreadZ = info.baseZ;

            // Transform to world coordinates (no rotation)
            double worldX = centerX;
            double worldY = centerY + heightOnConnector;
            double worldZ = centerZ;

            // Small perpendicular spread
            worldX += rightX * spreadZ * 0.5;
            worldZ += rightZ * spreadZ * 0.5;

            // Spawn/update particle with color
            if (info.particle == null || !info.particle.isAlive()) {
                try {
                    if (mc.level != null && mc.particleEngine != null) {
                        info.particle = mc.particleEngine.createParticle(
                            ModParticles.SNOWFLAKE.get(),
                            worldX, worldY, worldZ,
                            0.0, 0.0, 0.0);

                        if (info.particle != null) {
                            info.particle.setColor(wingColor[0], wingColor[1], wingColor[2]);
                        }
                    }
                } catch (Exception e) {
                    // Silently fail
                }
            } else {
                try {
                    info.particle.setPos(worldX, worldY, worldZ);
                    info.particle.setColor(wingColor[0], wingColor[1], wingColor[2]);
                } catch (Exception e) {
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
