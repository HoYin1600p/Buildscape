package com.kingodogo.buildscape.feature.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Random;

public interface BuildscapeWeatheringCopper extends WeatheringCopper {
    @Override
    default WeatherState getAge() {
        return CopperOxidationHandler.getAge((Block) this);
    }

    @Override
    default Optional<BlockState> getNext(BlockState state) {
        return CopperOxidationHandler.getNext(state);
    }

    @Override
    default void applyChangeOverTime(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        int age = this.getAge().ordinal();
        int sameAge = 0;
        int older = 0;
        for (BlockPos neighborPos : BlockPos.withinManhattan(pos, 4, 4, 4)) {
            if (neighborPos.distManhattan(pos) > 4) break;
            if (!neighborPos.equals(pos)) {
                Block block = level.getBlockState(neighborPos).getBlock();
                if (block instanceof ChangeOverTimeBlock<?> changing) {
                    Enum<?> neighborAge = changing.getAge();
                    if (this.getAge().getClass() == neighborAge.getClass()) {
                        int neighborOrdinal = neighborAge.ordinal();
                        if (neighborOrdinal < age) return;
                        if (neighborOrdinal > age) ++older;
                        else ++sameAge;
                    }
                }
            }
        }
        float ratio = (float) (older + 1) / (float) (older + sameAge + 1);
        if (random.nextFloat() < ratio * ratio * this.getChanceModifier()) {
            this.getNext(state).ifPresent(next ->
                    CopperOxidationHandler.setBlockStateOrDoor(level, pos, state, next.getBlock()));
        }
    }
}
