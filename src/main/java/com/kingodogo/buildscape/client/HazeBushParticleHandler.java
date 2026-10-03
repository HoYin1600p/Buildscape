package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class HazeBushParticleHandler {

    private static final Map<Long, Set<BlockPos>> POSITIONS_BY_CHUNK = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final int VANILLA_TICK_RANGE = 16;

    public static void track(BlockPos pos) {
        if (pos == null) return;
        long chunkKey = (new ChunkPos(pos)).toLong();
        POSITIONS_BY_CHUNK.computeIfAbsent(chunkKey, k -> new HashSet<>()).add(pos.immutable());
    }

    public static void untrack(BlockPos pos) {
        if (pos == null) return;
        long chunkKey = (new ChunkPos(pos)).toLong();
        Set<BlockPos> set = POSITIONS_BY_CHUNK.get(chunkKey);
        if (set != null) {
            set.remove(pos);
            if (set.isEmpty()) {
                POSITIONS_BY_CHUNK.remove(chunkKey);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld() == null || !event.getWorld().isClientSide()) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        long chunkKey = chunk.getPos().toLong();
        Set<BlockPos> chunkPositions = null;

        LevelChunkSection[] sections = chunk.getSections();
        for (LevelChunkSection section : sections) {
            if (section == null || section.hasOnlyAir() ||
                !section.maybeHas(state -> state.getBlock() instanceof HazeBushBlock || state.getBlock() instanceof PottedHazeBushBlock)) {
                continue;
            }

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.getBlock() instanceof HazeBushBlock || state.getBlock() instanceof PottedHazeBushBlock) {
                            if (chunkPositions == null) {
                                chunkPositions = new HashSet<>();
                            }
                            chunkPositions.add(new BlockPos(
                                    chunk.getPos().getMinBlockX() + x,
                                    section.bottomBlockY() + y,
                                    chunk.getPos().getMinBlockZ() + z
                            ).immutable());
                        }
                    }
                }
            }
        }

        if (chunkPositions != null) {
            POSITIONS_BY_CHUNK.put(chunkKey, chunkPositions);
        } else {
            POSITIONS_BY_CHUNK.remove(chunkKey);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld() != null && event.getWorld().isClientSide()) {
            POSITIONS_BY_CHUNK.remove(event.getChunk().getPos().toLong());
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() != null && event.getWorld().isClientSide()) {
            POSITIONS_BY_CHUNK.clear();
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;
        if (POSITIONS_BY_CHUNK.isEmpty()) return;

        Level level = mc.level;
        BlockPos playerPos = mc.player.blockPosition();

        int renderDistBlocks = mc.options.renderDistance * 16;
        double maxDistSq = (double) renderDistBlocks * renderDistBlocks;
        double minDistSq = (double) VANILLA_TICK_RANGE * VANILLA_TICK_RANGE;

        for (Iterator<Map.Entry<Long, Set<BlockPos>>> chunkIt = POSITIONS_BY_CHUNK.entrySet().iterator(); chunkIt.hasNext();) {
            Map.Entry<Long, Set<BlockPos>> entry = chunkIt.next();
            Set<BlockPos> positions = entry.getValue();

            for (Iterator<BlockPos> it = positions.iterator(); it.hasNext();) {
                BlockPos pos = it.next();
                double distSq = playerPos.distSqr(pos);

                if (distSq > maxDistSq) continue;
                if (distSq <= minDistSq) continue;

                BlockState state = level.getBlockState(pos);
                boolean hasHaze = false;
                float[] rgb = null;

                if (state.getBlock() instanceof HazeBushBlock bush) {
                    hasHaze = state.getValue(HazeBushBlock.HAS_HAZE);
                    rgb = HazeBushBlock.getPastelColor(bush.getColor());
                } else if (state.getBlock() instanceof PottedHazeBushBlock potted) {
                    hasHaze = state.getValue(PottedHazeBushBlock.HAS_HAZE);
                    rgb = HazeBushBlock.getPastelColor(potted.getColor());
                } else {
                    it.remove();
                    continue;
                }

                if (!hasHaze || rgb == null) continue;

                if (RANDOM.nextFloat() < 0.20F) {
                    double offsetX = (RANDOM.nextDouble() - 0.5D) * 5.0D;
                    double offsetZ = (RANDOM.nextDouble() - 0.5D) * 5.0D;
                    double offsetY = 0.02D + RANDOM.nextDouble() * 0.30D;

                    double px = pos.getX() + 0.5D + offsetX;
                    double py = pos.getY() + offsetY;
                    double pz = pos.getZ() + 0.5D + offsetZ;

                    level.addAlwaysVisibleParticle(ModParticles.HAZE.get(), true, px, py, pz, rgb[0], rgb[1], rgb[2]);
                }
            }
        }
    }
}
