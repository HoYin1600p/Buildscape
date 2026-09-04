package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.block.SmokeVentBlock;
import com.kingodogo.buildscape.block.SmokeVentBlockEntity;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.SmokeColorRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(
        modid = com.kingodogo.buildscape.BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class SmokeVentParticleHandler {

    private static final int VANILLA_ANIMATE_TICK_RANGE = 16;

    private static final Random random = new Random();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        Level level = mc.level;
        BlockPos playerPos = mc.player.blockPosition();

        int renderDistanceChunks = mc.options.renderDistance;
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

                    var state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof SmokeVentBlock)) continue;
                    var part = state.getValue(SmokeVentBlock.PART);
                    if (part != com.kingodogo.buildscape.block.PillarPart.TOP &&
                            part != com.kingodogo.buildscape.block.PillarPart.SINGLE) {
                        continue;
                    }

                    if (!ventBE.isActive()) continue;

                    if (random.nextFloat() < 0.95F) continue;

                    double x = pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
                    double y = pos.getY() + random.nextDouble() + random.nextDouble();
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
