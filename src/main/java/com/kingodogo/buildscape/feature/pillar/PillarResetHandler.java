package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Resets pillar stacks to the default look on the server. It finds the loaded pillar by dimension and position, walks
 * up the stack (at most 256 blocks) and resets each block entity. It does nothing for unloaded positions.
 */
public class PillarResetHandler {

    public static void resetPillarToDefault(String dimension, BlockPos pos) {
        if (dimension == null || pos == null) {
            return;
        }

        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isRunning()) {
                return;
            }

            server.execute(() -> {
                try {
                    resetInDimension(server, dimension, pos);
                } catch (Exception e) {
                    // world and block entity access on the server thread; log and keep the server running
                    BuildScape.LOGGER.warn("Unable to reset the pillar stack at {}", pos, e);
                }
            });
        } catch (Exception e) {
            // scheduling must not break the caller
            BuildScape.LOGGER.warn("Unable to schedule the pillar reset", e);
        }
    }

    /** Resets the stack in the first level with this dimension id; nothing happens if that position is unloaded. */
    private static void resetInDimension(MinecraftServer server, String dimension, BlockPos pos) {
        for (ServerLevel level : server.getAllLevels()) {
            if (!level.dimension().location().toString().equals(dimension)) {
                continue;
            }
            if (!level.isLoaded(pos)) {
                return;
            }
            resetStackAt(level, pos);
            return;
        }
    }

    /** Resets every pillar block entity from the stack bottom upwards, stopping after 256 resets. */
    private static void resetStackAt(ServerLevel level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PillarBlockEntity bottomBE)) {
            return;
        }
        BlockPos current = bottomBE.findStackBottom();
        int resetCount = 0;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE) {
                resetPillarBlockEntity(pillarBE);
                resetCount++;
            }
            current = current.above();

            if (resetCount > 256) {
                break;
            }
        }
    }

    private static void resetPillarBlockEntity(PillarBlockEntity pillarBE) {
        if (pillarBE == null) {
            return;
        }

        pillarBE.resetToDefaultAppearance();
    }

    public static void resetPillarFromData(PillarIdManager.PillarData data) {
        if (data == null) {
            return;
        }

        BlockPos pos = data.getBlockPos();
        resetPillarToDefault(data.dimension, pos);
    }
}
