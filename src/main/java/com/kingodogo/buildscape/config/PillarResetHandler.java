package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
public class PillarResetHandler {
    public static void resetPillarToDefault(MinecraftServer server, String dimension, BlockPos pos) {
        if (server == null || dimension == null || pos == null) {
            return;
        }

        server.execute(() -> {
            for (ServerLevel level : server.getAllLevels()) {
                if (com.kingodogo.buildscape.platform.Services.PLATFORM.getDimensionId(level).toString().equals(dimension)) {
                    if (!level.isLoaded(pos)) {
                        return;
                    }

                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof PillarBlockEntity bottomBE) {
                        BlockPos bottomPos = bottomBE.findStackBottom();
                        BlockPos current = bottomPos;
                        int resetCount = 0;

                        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
                            BlockEntity be = level.getBlockEntity(current);
                            if (be instanceof PillarBlockEntity pillarBE) {
                                pillarBE.resetToDefaultAppearance();
                                resetCount++;
                            }
                            current = current.above();
                            if (resetCount > 256) {
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        });
    }
    public static void resetPillarFromData(MinecraftServer server, PillarIdManager.PillarData data) {
        if (data != null) {
            resetPillarToDefault(server, data.dimension, data.getBlockPos());
        }
    }

    public static void resetPillarFromData(PillarIdManager.PillarData data) {
        resetPillarFromData(PillarIdManager.getCurrentServer(), data);
    }
}
