package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToIntFunction;

/**
 * Vanilla copper weathering for BuildScape copper (ChangeOverTimeBlock): the random-tick chance, and the
 * slowdown, or stop, near less weathered copper within four blocks.
 */
public final class CopperWeathering {
    /** Vanilla chance that a random tick tries to weather the block. */
    public static final float TICK_CHANCE = 0.05688889F;
    private static final int RADIUS = 4;
    private static final List<ToIntFunction<Block>> AGE_SOURCES = new CopyOnWriteArrayList<>();

    private CopperWeathering() {}

    /** Adds a stage table: 0 unaffected to 3 oxidized for blocks that weather naturally, -1 otherwise. */
    public static void addAgeSource(ToIntFunction<Block> source) {
        AGE_SOURCES.add(source);
    }

    /** The age of BuildScape or vanilla copper that weathers on its own, or -1 for any other block. */
    public static int age(BlockState state) {
        Block block = state.getBlock();
        for (ToIntFunction<Block> source : AGE_SOURCES) {
            int age = source.applyAsInt(block);
            if (age >= 0) {
                return age;
            }
        }
        if (!(block instanceof WeatheringCopper)) {
            return -1;
        }
        int age = 0;
        Optional<BlockState> previous = WeatheringCopper.getPrevious(state);
        while (previous.isPresent() && age < 3) {
            age++;
            previous = WeatheringCopper.getPrevious(previous.get());
        }
        return age;
    }

    /** One random tick of a block at this age: true when it should weather one stage now. */
    public static boolean shouldAdvance(Level level, BlockPos pos, int age) {
        if (age < 0 || age >= 3 || level.getRandom().nextFloat() >= TICK_CHANCE) {
            return false;
        }
        int sameAge = 0;
        int older = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    int distance = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (distance == 0 || distance > RADIUS) {
                        continue;
                    }
                    int neighbor = age(level.getBlockState(cursor.setWithOffset(pos, dx, dy, dz)));
                    if (neighbor < 0) {
                        continue;
                    }
                    // Less weathered copper nearby holds this block back, as in vanilla.
                    if (neighbor < age) {
                        return false;
                    }
                    if (neighbor > age) {
                        older++;
                    } else {
                        sameAge++;
                    }
                }
            }
        }
        return level.getRandom().nextFloat() < chance(age, sameAge, older);
    }

    /** Vanilla weathering chance once a tick qualifies; unaffected copper weathers at three quarters speed. */
    static float chance(int age, int sameAge, int older) {
        float ratio = (older + 1.0F) / (older + sameAge + 1.0F);
        return ratio * ratio * (age == 0 ? 0.75F : 1.0F);
    }

    /** Age from a stage-prefixed id, for families named exposed_, weathered_ and oxidized_ plus the base. */
    public static int ageFromPath(String path) {
        if (path.startsWith("waxed_")) {
            return -1;
        }
        if (path.startsWith("oxidized_")) {
            return 3;
        }
        if (path.startsWith("weathered_")) {
            return 2;
        }
        return path.startsWith("exposed_") ? 1 : 0;
    }
}
