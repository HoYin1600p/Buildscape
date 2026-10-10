package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.PotentSulfurBlock;
import com.kingodogo.buildscape.block.PotentSulfurBlockEntity;
import com.kingodogo.buildscape.block.PotentSulfurState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;

public class GeyserParticleHandler {

    private static final int VANILLA_TICKER_RANGE = 16;

    public static void tickClient() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        Level level = mc.level;

        if (level.getGameTime() % 20L != 0L) return;

        BlockPos playerPos = mc.player.blockPosition();

        int renderDistanceChunks = 12;
        try {
            Object opt = mc.options.getClass().getMethod("renderDistance").invoke(mc.options);
            renderDistanceChunks = (Integer) opt.getClass().getMethod("get").invoke(opt);
        } catch (Throwable t) {
            try {
                renderDistanceChunks = mc.options.getClass().getField("renderDistance").getInt(mc.options);
            } catch (Throwable ignored) {
            }
        }

        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;

        for (int cx = -renderDistanceChunks; cx <= renderDistanceChunks; cx++) {
            for (int cz = -renderDistanceChunks; cz <= renderDistanceChunks; cz++) {
                int chunkX = playerChunkX + cx;
                int chunkZ = playerChunkZ + cz;

                if (!level.hasChunk(chunkX, chunkZ)) continue;

                LevelChunk chunk = level.getChunk(chunkX, chunkZ);

                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockEntity be = entry.getValue();
                    if (!(be instanceof PotentSulfurBlockEntity sulfurBE)) continue;

                    BlockPos pos = entry.getKey();

                    double distSq = playerPos.distSqr(pos);
                    if (distSq <= VANILLA_TICKER_RANGE * VANILLA_TICKER_RANGE) continue;

                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof PotentSulfurBlock)) continue;

                    PotentSulfurState sulfurState = state.getValue(PotentSulfurBlock.STATE);
                    if (sulfurState == PotentSulfurState.ERUPTING || sulfurState == PotentSulfurState.CONTINUOUS) {
                        PotentSulfurBlockEntity.tickClientPlumeDistant(level, pos, state, sulfurBE);
                    }
                }
            }
        }
    }
}
