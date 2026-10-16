package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.PillarPart;
import com.kingodogo.buildscape.block.SmokeVentBlock;
import com.kingodogo.buildscape.block.SmokeVentBlockEntity;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.SmokeColorRegistry;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;
import java.util.Random;

public class SmokeVentParticleHandler {

    private static final int VANILLA_ANIMATE_TICK_RANGE = 16;
    private static final Random random = new Random();

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        Level level = mc.level;
        BlockPos playerPos = mc.player.blockPosition();

        int renderDistanceChunks = Services.PLATFORM.getRenderDistanceChunks();
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
                    if (!(be instanceof SmokeVentBlockEntity ventBE)) continue;

                    BlockPos pos = entry.getKey();

                    double distSq = playerPos.distSqr(pos);
                    if (distSq <= VANILLA_ANIMATE_TICK_RANGE * VANILLA_ANIMATE_TICK_RANGE) continue;

                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof SmokeVentBlock)) continue;
                    PillarPart part = state.getValue(SmokeVentBlock.PART);
                    if (part != PillarPart.TOP && part != PillarPart.SINGLE) {
                        continue;
                    }

                    if (!ventBE.isActive()) continue;

                    if (random.nextFloat() < 0.95F) continue;

                    double x = pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
                    double y = pos.getY() + SmokeVentBlock.SMOKE_SPAWN_BASE + random.nextDouble() * (2.0 - SmokeVentBlock.SMOKE_SPAWN_BASE);
                    double z = pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);

                    String smokeColor = ventBE.getSmokeColor();
                    if (smokeColor != null) {
                        SmokeColorRegistry.registerColorForPosition(x, y, z, smokeColor);
                        level.addAlwaysVisibleParticle(ModParticles.COLORED_SMOKE.get(), true, x, y, z, 0.0, 0.07, 0.0);
                    } else {
                        level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x, y, z, 0.0, 0.07, 0.0);
                    }
                }
            }
        }
    }
}
