package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class HazeBushParticleHandler {
    private static final Set<BlockPos> POSITIONS = new HashSet<>();
    private static final Random RANDOM = new Random();
    private static Level trackedLevel;
    private static int scanTicks;
    private static final int RANGE = 32;
    private HazeBushParticleHandler() {}

    public static void clear() { POSITIONS.clear(); trackedLevel = null; scanTicks = 0; }

    private static boolean isBush(BlockState state) {
        return state.getBlock() instanceof HazeBushBlock || state.getBlock() instanceof PottedHazeBushBlock;
    }

    private static float[] color(BlockState state) {
        return HazeBushBlock.getPastelColor(state.getBlock() instanceof HazeBushBlock bush
                ? bush.getColor() : ((PottedHazeBushBlock) state.getBlock()).getColor());
    }

    public static void animate(BlockState state, Level level, BlockPos pos) {
        var mc = Minecraft.getInstance();
        ParticleStatus status = mc.options.particles().get();
        if (!state.getValue(HazeBushBlock.HAS_HAZE) || status == ParticleStatus.MINIMAL) return;
        if (trackedLevel != level) { clear(); trackedLevel = level; }
        POSITIONS.add(pos.immutable());
        if (status == ParticleStatus.DECREASED && RANDOM.nextFloat() > .5F) return;
        int count = status == ParticleStatus.DECREASED ? 1 : 1 + RANDOM.nextInt(2);
        for (int i = 0; i < count; i++) spawn(level, pos, color(state), false);
    }

    public static void clientTick() {
        var mc = Minecraft.getInstance();
        if (trackedLevel != mc.level) { clear(); trackedLevel = mc.level; }
        if (mc.level == null || mc.player == null || mc.isPaused()) return;
        ParticleStatus status = mc.options.particles().get();
        if (status == ParticleStatus.MINIMAL) return;
        Vec3 camera = mc.gameRenderer.mainCamera().position();
        if (scanTicks++ % 20 == 0) scanVisibleChunks(mc.level, camera);
        float chance = status == ParticleStatus.DECREASED ? .08F : .20F;
        var iterator = POSITIONS.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (!mc.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) { iterator.remove(); continue; }
            if (camera.distanceToSqr(pos.getX() + .5D, pos.getY() + .4D, pos.getZ() + .5D) > RANGE * RANGE) continue;
            if (mc.player.blockPosition().distSqr(pos) <= 16 * 16) continue;
            BlockState state = mc.level.getBlockState(pos);
            if (!isBush(state)) { iterator.remove(); continue; }
            if (state.getValue(HazeBushBlock.HAS_HAZE) && RANDOM.nextFloat() < chance) spawn(mc.level, pos, color(state), true);
        }
    }

    private static void scanVisibleChunks(Level level, Vec3 camera) {
        POSITIONS.clear();
        int minY = Mth.floor(camera.y - RANGE), maxY = Mth.floor(camera.y + RANGE);
        for (int cx = (Mth.floor(camera.x) - RANGE) >> 4; cx <= (Mth.floor(camera.x) + RANGE) >> 4; cx++) {
            for (int cz = (Mth.floor(camera.z) - RANGE) >> 4; cz <= (Mth.floor(camera.z) + RANGE) >> 4; cz++) {
                if (!level.hasChunk(cx, cz)) continue;
                var chunk = level.getChunk(cx, cz);
                var sections = chunk.getSections();
                for (int index = 0; index < sections.length; index++) {
                    var section = sections[index];
                    int bottom = chunk.getSectionYFromSectionIndex(index) << 4;
                    if (bottom > maxY || bottom + 15 < minY || section.hasOnlyAir() || !section.maybeHas(HazeBushParticleHandler::isBush)) continue;
                    for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) for (int x = 0; x < 16; x++) {
                        if (isBush(section.getBlockState(x, y, z))) POSITIONS.add(new BlockPos((cx << 4) + x, bottom + y, (cz << 4) + z));
                    }
                }
            }
        }
    }

    private static void spawn(Level level, BlockPos pos, float[] rgb, boolean distant) {
        Vec3 particle = findHazeParticlePos(level, pos, RANDOM);
        if (particle == null) return;
        if (distant) level.addAlwaysVisibleParticle(ModParticles.HAZE.get(), false, particle.x, particle.y, particle.z, rgb[0], rgb[1], rgb[2]);
        else level.addParticle(ModParticles.HAZE.get(), particle.x, particle.y, particle.z, rgb[0], rgb[1], rgb[2]);
    }

    public static Vec3 findHazeParticlePos(Level level, BlockPos bushPos, Random random) {
        for (int attempt = 0; attempt < 3; attempt++) {
            double px = bushPos.getX() + .5D + (random.nextDouble() - .5D) * 5;
            double pz = bushPos.getZ() + .5D + (random.nextDouble() - .5D) * 5;
            int blockX = Mth.floor(px), blockZ = Mth.floor(pz);
            double floorY = Double.NaN;
            for (int y = bushPos.getY() + 1; y >= bushPos.getY() - 1; y--) {
                var check = new BlockPos(blockX, y, blockZ);
                var shape = level.getBlockState(check).getCollisionShape(level, check);
                if (!shape.isEmpty()) { floorY = y + shape.max(Direction.Axis.Y); break; }
            }
            if (Double.isNaN(floorY)) continue;
            double py = floorY + .02D + random.nextDouble() * .30D;
            var check = new BlockPos(blockX, Mth.floor(py), blockZ);
            var state = level.getBlockState(check);
            if (state.isSolidRender()) continue;
            var shape = state.getCollisionShape(level, check);
            if (!shape.isEmpty() && shape.bounds().move(check).contains(px, py, pz)) continue;
            var start = new Vec3(bushPos.getX() + .5D, bushPos.getY() + .4D, bushPos.getZ() + .5D);
            var hit = level.clip(new ClipContext(start, new Vec3(px, py + .05D, pz), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                    net.minecraft.world.phys.shapes.CollisionContext.empty()));
            if (hit.getType() != HitResult.Type.MISS && (hit.getBlockPos().getX() != blockX || hit.getBlockPos().getZ() != blockZ)) continue;
            return new Vec3(px, py, pz);
        }
        return null;
    }
}
