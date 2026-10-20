package com.kingodogo.buildscape.client.feature.hazebush;

import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HazeBushParticleHandler {

    private static final Map<Long, Set<BlockPos>> POSITIONS_BY_CHUNK = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final int VANILLA_TICK_RANGE = 16;
    // HAZE does not override the particle limiter; LevelRenderer discards it beyond 32 blocks.
    private static final int HAZE_PARTICLE_RANGE = 32;

    public static void track(BlockPos pos) {
        if (pos == null) {
            return;
        }
        long chunkKey = (new ChunkPos(pos)).toLong();
        POSITIONS_BY_CHUNK.computeIfAbsent(chunkKey, k -> new HashSet<>()).add(pos.immutable());
    }

    public static void untrack(BlockPos pos) {
        if (pos == null) {
            return;
        }
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
        if (event.getWorld() == null || !event.getWorld().isClientSide()) {
            return;
        }
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        long chunkKey = chunk.getPos().toLong();
        Set<BlockPos> chunkPositions = null;

        LevelChunkSection[] sections = chunk.getSections();
        for (LevelChunkSection section : sections) {
            if (section == null || section.hasOnlyAir() ||
                    !section.maybeHas(state -> state.getBlock() instanceof HazeBushBlock
                            || state.getBlock() instanceof PottedHazeBushBlock)) {
                continue;
            }

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.getBlock() instanceof HazeBushBlock
                                || state.getBlock() instanceof PottedHazeBushBlock) {
                            if (chunkPositions == null) {
                                chunkPositions = new HashSet<>();
                            }
                            chunkPositions.add(new BlockPos(
                                    chunk.getPos().getMinBlockX() + x,
                                    section.bottomBlockY() + y,
                                    chunk.getPos().getMinBlockZ() + z).immutable());
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
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }
        if (mc.options.particles == ParticleStatus.MINIMAL) {
            return;
        }
        if (POSITIONS_BY_CHUNK.isEmpty()) {
            return;
        }

        Level level = mc.level;
        BlockPos playerPos = mc.player.blockPosition();

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        double maxDistSq = (double) HAZE_PARTICLE_RANGE * HAZE_PARTICLE_RANGE;
        double minDistSq = (double) VANILLA_TICK_RANGE * VANILLA_TICK_RANGE;
        float chance = (mc.options.particles == ParticleStatus.DECREASED) ? 0.08F : 0.20F;

        for (Iterator<Map.Entry<Long, Set<BlockPos>>> chunkIt = POSITIONS_BY_CHUNK.entrySet().iterator(); chunkIt
                .hasNext();) {
            Map.Entry<Long, Set<BlockPos>> entry = chunkIt.next();
            Set<BlockPos> positions = entry.getValue();

            for (Iterator<BlockPos> it = positions.iterator(); it.hasNext();) {
                BlockPos pos = it.next();
                double distSq = playerPos.distSqr(pos);

                if (cameraPos.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5) > maxDistSq) {
                    continue;
                }
                if (distSq <= minDistSq) {
                    continue;
                }

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

                if (!hasHaze || rgb == null) {
                    continue;
                }

                if (RANDOM.nextFloat() < chance) {
                    Vec3 particlePos = findHazeParticlePos(level, pos, RANDOM);
                    if (particlePos != null) {
                        level.addAlwaysVisibleParticle(ModParticles.HAZE.get(), false, particlePos.x, particlePos.y,
                                particlePos.z, rgb[0], rgb[1], rgb[2]);
                    }
                }
            }
        }
    }

    @Nullable
    public static Vec3 findHazeParticlePos(Level level, BlockPos bushPos, Random random) {
        for (int attempt = 0; attempt < 3; attempt++) {
            double offsetX = (random.nextDouble() - 0.5) * 5.0;
            double offsetZ = (random.nextDouble() - 0.5) * 5.0;
            double px = bushPos.getX() + 0.5 + offsetX;
            double pz = bushPos.getZ() + 0.5 + offsetZ;

            int blockX = Mth.floor(px);
            int blockZ = Mth.floor(pz);

            double floorY = Double.NaN;
            for (int y = bushPos.getY() + 1; y >= bushPos.getY() - 1; y--) {
                BlockPos checkPos = new BlockPos(blockX, y, blockZ);
                BlockState state = level.getBlockState(checkPos);
                VoxelShape shape = state.getCollisionShape(level, checkPos);
                if (!shape.isEmpty()) {
                    floorY = y + shape.max(Direction.Axis.Y);
                    break;
                }
            }

            if (Double.isNaN(floorY)) {
                continue;
            }

            double py = floorY + 0.02 + random.nextDouble() * 0.30;

            BlockPos particleBlockPos = new BlockPos(blockX, Mth.floor(py), blockZ);
            BlockState pState = level.getBlockState(particleBlockPos);
            if (pState.isSolidRender(level, particleBlockPos)) {
                continue;
            }
            VoxelShape pShape = pState.getCollisionShape(level, particleBlockPos);
            if (!pShape.isEmpty() && pShape.bounds().move(particleBlockPos).contains(px, py, pz)) {
                continue;
            }

            Vec3 start = new Vec3(bushPos.getX() + 0.5, bushPos.getY() + 0.4, bushPos.getZ() + 0.5);
            Vec3 end = new Vec3(px, py + 0.05, pz);
            BlockHitResult hit = level
                    .clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if (hit.getType() != HitResult.Type.MISS) {
                if (hit.getBlockPos().getX() != blockX || hit.getBlockPos().getZ() != blockZ) {
                    continue;
                }
            }

            return new Vec3(px, py, pz);
        }
        return null;
    }

    /** Client side of the blocks' animateTick: tracks the bush and spawns its haze particles. */
    public static void animateTick(Level level, BlockPos pos, Random random, DyeColor color) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.particles == ParticleStatus.MINIMAL) {
            return;
        }

        track(pos);

        if (mc.options.particles == ParticleStatus.DECREASED && random.nextFloat() > 0.5F) {
            return;
        }

        float[] rgb = HazeBushBlock.getPastelColor(color);

        int count = (mc.options.particles == ParticleStatus.DECREASED) ? 1 : (1 + random.nextInt(2));
        for (int i = 0; i < count; i++) {
            Vec3 particlePos = findHazeParticlePos(level, pos, random);
            if (particlePos != null) {
                level.addParticle(ModParticles.HAZE.get(), particlePos.x, particlePos.y, particlePos.z, rgb[0], rgb[1],
                        rgb[2]);
            }
        }
    }
}
