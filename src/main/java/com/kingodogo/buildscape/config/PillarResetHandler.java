package com.kingodogo.buildscape.config;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Handles resetting pillar block entities to their default state when
 * pillar IDs are removed from the manager. This ensures that pillars
 * act as if freshly placed (no custom colors/patterns) while keeping
 * the displayed item intact.
 */
public class PillarResetHandler {

    /**
     * Resets a pillar block entity to its default state (freshly placed).
     * Clears all custom particle colors, patterns, and settings from NBT,
     * but keeps the displayed item.
     * 
     * @param dimension The dimension key (e.g., "minecraft:overworld")
     * @param pos The block position of the pillar
     */
    public static void resetPillarToDefault(String dimension, BlockPos pos) {
        if (dimension == null || pos == null) {
            return;
        }

        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isRunning()) {
                return;
            }

            // Execute on server thread to ensure thread safety
            // This is necessary because removal might be called from client-side GUI
            server.execute(() -> {
                try {
                    // Find the level for this pillar's dimension
                    for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                        if (level.dimension().location().toString().equals(dimension)) {
                            // Check if chunk is loaded
                            if (!level.isLoaded(pos)) {
                                return;
                            }

                            // Get the block entity at the bottom of the stack
                            net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
                            if (blockEntity instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomBE) {
                                // Find the bottom of the stack (in case pos is not the bottom)
                                net.minecraft.core.BlockPos bottomPos = bottomBE.findStackBottom();
                                
                                // Reset ALL connected pillar blocks in the stack
                                net.minecraft.core.BlockPos current = bottomPos;
                                int resetCount = 0;
                                
                                while (level.getBlockState(current).getBlock() instanceof com.kingodogo.buildscape.block.PillarBlock) {
                                    net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(current);
                                    if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
                                        // Get old state for logging (only for first block)
                                        if (resetCount == 0) {
                                            String oldId = pillarBE.getPillarId();
                                            java.util.List<String> oldColors = pillarBE.getParticleColors();
                                            System.out.println("BuildScape: Resetting pillar stack starting at " + bottomPos + 
                                                " - Old ID: " + oldId + " (" + (oldColors != null ? oldColors.size() : 0) + " colors)");
                                        }
                                        
                                        // Reset all custom particle settings to default (freshly placed state)
                                        resetPillarBlockEntity(pillarBE);
                                        resetCount++;
                                    }
                                    current = current.above();
                                    
                                    // Safety check to prevent infinite loops
                                    if (resetCount > 256) {
                                        break;
                                    }
                                }
                                
                                System.out.println("BuildScape: Reset complete - reset " + resetCount + " pillar blocks in stack");
                            } else {
                                System.out.println("BuildScape: Warning - No pillar block entity found at " + pos + " in dimension " + dimension);
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Log error but don't crash
                    System.err.println("BuildScape: Error resetting pillar at " + pos + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // Silently handle errors (e.g., world not loaded, chunk not available)
            // This is expected if the pillar is in an unloaded chunk
        }
    }

    /**
     * Resets a pillar block entity's particle-related fields to default values.
     * This method directly modifies the block entity's internal state.
     * 
     * @param pillarBE The pillar block entity to reset
     */
    private static void resetPillarBlockEntity(com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
        if (pillarBE == null) {
            return;
        }

        // Reset all custom particle settings to default (freshly placed state)
        // This clears colors, patterns, and all related settings from NBT
        pillarBE.resetToDefaultAppearance();
    }

    /**
     * Resets a pillar using PillarData from the manager.
     * This is called when a pillar ID is removed from the manager.
     * 
     * @param data The PillarData containing dimension and position info
     */
    public static void resetPillarFromData(PillarIdManager.PillarData data) {
        if (data == null) {
            return;
        }

        BlockPos pos = data.getBlockPos();
        resetPillarToDefault(data.dimension, pos);
    }
}

