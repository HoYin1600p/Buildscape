package com.kingodogo.buildscape.block;

import com.google.common.collect.ImmutableSet;
import com.kingodogo.buildscape.BuildScape;
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
            ModBlocks.COPPER_ROD,
            ModBlocks.WAXED_COPPER_ROD,
            ModBlocks.EXPOSED_COPPER_ROD,
            ModBlocks.WAXED_EXPOSED_COPPER_ROD,
            ModBlocks.WEATHERED_COPPER_ROD,
            ModBlocks.WAXED_WEATHERED_COPPER_ROD,
            ModBlocks.OXIDIZED_COPPER_ROD,
            ModBlocks.WAXED_OXIDIZED_COPPER_ROD
    );

    public static void registerPoi() {
        try {
            Map<BlockState, PoiType> poiMap = net.minecraftforge.registries.GameData.getBlockStatePointOfInterestTypeMap();
            Set<BlockState> newStates = new HashSet<>(PoiType.LIGHTNING_ROD.getBlockStates());

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
            BuildScape.LOGGER.error("Failed to register Copper Rods into PoiType.LIGHTNING_ROD", e);
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
        if (!CopperOxidationHandler.isWeatheringCopper(baseState.getBlock())) {
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
        Block cleanRod = ModBlocks.COPPER_ROD.get();
        if (block == ModBlocks.EXPOSED_COPPER_ROD.get()
                || block == ModBlocks.WEATHERED_COPPER_ROD.get()
                || block == ModBlocks.OXIDIZED_COPPER_ROD.get()) {
            CopperOxidationHandler.setBlockStateOrDoor(level, pos, state, cleanRod);
        }
    }

    private static void deoxidizeBuildscapeCopperFirst(Level level, BlockPos pos, BlockState state) {
        Block firstStage = CopperOxidationHandler.getFirstStage(state.getBlock());
        if (firstStage != null) {
            CopperOxidationHandler.setBlockStateOrDoor(level, pos, state, firstStage);
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
            if (!CopperOxidationHandler.isWeatheringCopper(testState.getBlock())) {
                continue;
            }
            if (testState.getBlock() instanceof WeatheringCopper) {
                WeatheringCopper.getPrevious(testState).ifPresent(s -> level.setBlockAndUpdate(testPos, s));
            } else {
                Block prev = CopperOxidationHandler.getPrevStage(testState.getBlock());
                if (prev != null) {
                    CopperOxidationHandler.setBlockStateOrDoor(level, testPos, testState, prev);
                }
            }
            level.levelEvent(3002, testPos, -1);
            return Optional.of(testPos);
        }
        return Optional.empty();
    }
}
