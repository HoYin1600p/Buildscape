package com.kingodogo.buildscape.config;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

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
                    for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                        if (level.dimension().location().toString().equals(dimension)) {
                            if (!level.isLoaded(pos)) {
                                return;
                            }

                            net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
                            if (blockEntity instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomBE) {
                                net.minecraft.core.BlockPos bottomPos = bottomBE.findStackBottom();

                                net.minecraft.core.BlockPos current = bottomPos;
                                int resetCount = 0;

                                while (level.getBlockState(current).getBlock() instanceof com.kingodogo.buildscape.block.PillarBlock) {
                                    net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(current);
                                    if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
                                        resetPillarBlockEntity(pillarBE);
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
                } catch (Exception e) {
                }
            });
        } catch (Exception e) {
        }
    }

    private static void resetPillarBlockEntity(com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
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

