package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;

import com.google.common.collect.ImmutableSet;
import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.mixin.PoiTypeAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class CopperRodHandler {

    private static final List<Supplier<Block>> ROD_SUPPLIERS = List.of(
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "copper_rod")),
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "waxed_copper_rod")),
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "exposed_copper_rod")),
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "waxed_exposed_copper_rod")),
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "weathered_copper_rod")),
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "waxed_weathered_copper_rod")),
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "oxidized_copper_rod")),
            () -> Services.PLATFORM.getBlock(new CommonId("buildscape", "waxed_oxidized_copper_rod"))
    );

    // The Forge loader supplies GameData's POI map during common setup.
    public static void registerPoi(Map<BlockState, PoiType> poiMap) {
        try {

            Set<BlockState> newStates = new HashSet<>(((PoiTypeAccessor) (Object) PoiType.LIGHTNING_ROD).getMatchingStates());

            for (Supplier<Block> rodSupplier : ROD_SUPPLIERS) {
                Block rod = rodSupplier.get();
                for (BlockState state : rod.getStateDefinition().getPossibleStates()) {
                    poiMap.put(state, PoiType.LIGHTNING_ROD);
                    newStates.add(state);
                }
            }

            ImmutableSet<BlockState> immutableStates = ImmutableSet.copyOf(newStates);
            ((PoiTypeAccessor) (Object) PoiType.LIGHTNING_ROD).setMatchingStates(immutableStates);
            PoiTypeAccessor.getAllStates().addAll(immutableStates);
        } catch (Exception e) {
            BuildscapeCommon.LOGGER.error("Failed to register Copper Rods into PoiType.LIGHTNING_ROD", e);
        }
    }

    public static void onLightningClearCopper(Level level, BlockPos strikePos) {
        BlockState state = level.getBlockState(strikePos);
        BlockPos basePos;
        if (state.getBlock() instanceof LightningRodBlock) {
            deoxidizeRod(level, strikePos, state);
            basePos = strikePos.relative(state.getValue(LightningRodBlock.FACING).getOpposite());
        } else {
            basePos = strikePos;
        }

        BlockState baseState = level.getBlockState(basePos);
        if (!com.kingodogo.buildscape.adapter.v118x.RestoredCopperOxidation.isWeatheringCopper(baseState.getBlock())) {
            return;
        }
        if (baseState.getBlock() instanceof WeatheringCopper) {
            level.setBlockAndUpdate(basePos, WeatheringCopper.getFirst(baseState));
        } else {
            deoxidizeBuildscapeCopperFirst(level, basePos, baseState);
        }

        BlockPos.MutableBlockPos mutable = strikePos.mutable();
        int steps = level.random.nextInt(3) + 3;
        for (int i = 0; i < steps; ++i) {
            int dist = level.random.nextInt(8) + 1;
            randomWalkCleanCopper(level, basePos, mutable, dist);
        }
    }

    private static void deoxidizeRod(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        Block cleanRod = Services.PLATFORM.getBlock(new CommonId("buildscape", "copper_rod"));
        if (block == Services.PLATFORM.getBlock(new CommonId("buildscape", "exposed_copper_rod"))
                || block == Services.PLATFORM.getBlock(new CommonId("buildscape", "weathered_copper_rod"))
                || block == Services.PLATFORM.getBlock(new CommonId("buildscape", "oxidized_copper_rod"))) {
            com.kingodogo.buildscape.adapter.v118x.RestoredCopperOxidation.setBlockStateOrDoor(level, pos, state, cleanRod);
        }
    }

    private static void deoxidizeBuildscapeCopperFirst(Level level, BlockPos pos, BlockState state) {
        Block firstStage = com.kingodogo.buildscape.adapter.v118x.RestoredCopperOxidation.getFirstStage(state.getBlock());
        if (firstStage != null) {
            com.kingodogo.buildscape.adapter.v118x.RestoredCopperOxidation.setBlockStateOrDoor(level, pos, state, firstStage);
        }
    }

    private static void randomWalkCleanCopper(Level level, BlockPos center, BlockPos.MutableBlockPos mutable, int maxSteps) {
        mutable.set(center);
        for (int i = 0; i < maxSteps; ++i) {
            Optional<BlockPos> opt = randomStepCleanCopper(level, mutable);
            if (opt.isEmpty()) {
                break;
            }
            mutable.set(opt.get());
        }
    }

    private static Optional<BlockPos> randomStepCleanCopper(Level level, BlockPos pos) {
        for (BlockPos testPos : BlockPos.randomInCube(level.random, 10, pos, 1)) {
            BlockState testState = level.getBlockState(testPos);
            if (!com.kingodogo.buildscape.adapter.v118x.RestoredCopperOxidation.isWeatheringCopper(testState.getBlock())) {
                continue;
            }
            if (testState.getBlock() instanceof WeatheringCopper) {
                WeatheringCopper.getPrevious(testState).ifPresent(s -> level.setBlockAndUpdate(testPos, s));
            } else {
                Block prev = com.kingodogo.buildscape.adapter.v118x.RestoredCopperOxidation.getPrevStage(testState.getBlock());
                if (prev != null) {
                    com.kingodogo.buildscape.adapter.v118x.RestoredCopperOxidation.setBlockStateOrDoor(level, testPos, testState, prev);
                }
            }
            level.levelEvent(3002, testPos, -1);
            return Optional.of(testPos);
        }
        return Optional.empty();
    }
}
