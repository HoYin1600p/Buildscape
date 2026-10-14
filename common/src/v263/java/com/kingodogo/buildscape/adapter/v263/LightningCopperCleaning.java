package com.kingodogo.buildscape.adapter.v263;

import com.kingodogo.buildscape.block.CopperOxidationHandler;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Set;

/** The reference lightning walk, including the custom copper families BuildScape keeps on 26.x (rods, lanterns, bars, chains and chests are vanilla there). */
public final class LightningCopperCleaning {
    private static final Set<String> FAMILIES = Set.of("copper_mesh", "copper_bolts", "cut_copper_vertical_slab",
            "copper_button", "copper_pressure_plate", "large_copper_chain",
            "slit_copper", "slit_copper_stairs", "slit_copper_slab", "slit_copper_vertical_slab");
    private static final String[] STAGES = {"", "exposed_", "weathered_", "oxidized_"};

    private LightningCopperCleaning() {}

    public static void clear(Level level, BlockPos strikePos) {
        BlockState state = level.getBlockState(strikePos);
        BlockPos basePos = strikePos;
        if (state.getBlock() instanceof LightningRodBlock) {
            basePos = strikePos.relative(state.getValue(LightningRodBlock.FACING).getOpposite());
        }
        BlockState base = level.getBlockState(basePos);
        if (!isWeathering(base)) return;
        if (base.getBlock() instanceof WeatheringCopper) {
            level.setBlockAndUpdate(basePos, WeatheringCopper.getFirst(base));
        } else {
            Block first = customStage(base, true);
            if (first != null) replace(level, basePos, base, first);
        }

        BlockPos.MutableBlockPos mutable = strikePos.mutable();
        int walks = level.getRandom().nextInt(3) + 3;
        for (int i = 0; i < walks; i++) {
            int steps = level.getRandom().nextInt(8) + 1;
            mutable.set(basePos);
            for (int j = 0; j < steps; j++) {
                BlockPos next = cleanStep(level, mutable);
                if (next == null) break;
                mutable.set(next);
            }
        }
    }

    private static BlockPos cleanStep(Level level, BlockPos pos) {
        for (BlockPos candidate : BlockPos.randomInCube(level.getRandom(), 10, pos, 1)) {
            BlockState state = level.getBlockState(candidate);
            if (!isWeathering(state)) continue;
            if (state.getBlock() instanceof WeatheringCopper) {
                var previous = WeatheringCopper.getPrevious(state);
                if (previous.isPresent()) level.setBlockAndUpdate(candidate, previous.get());
            } else {
                Block previous = customStage(state, false);
                if (previous != null) replace(level, candidate, state, previous);
            }
            level.levelEvent(3002, candidate, -1);
            return candidate.immutable();
        }
        return null;
    }

    private static boolean isWeathering(BlockState state) {
        return state.getBlock() instanceof WeatheringCopper || customStage(state, true) != null;
    }

    private static Block customStage(BlockState state, boolean first) {
        CommonId id = Services.PLATFORM.getBlockId(state.getBlock());
        if (id == null || !id.getNamespace().equals("buildscape")) return null;
        String path = id.getPath();
        boolean large = path.startsWith("large_");
        String name = large ? path.substring(6) : path;
        int stage = 0;
        for (int i = 1; i < STAGES.length; i++) {
            if (name.startsWith(STAGES[i])) {
                stage = i;
                name = name.substring(STAGES[i].length());
                break;
            }
        }
        String family = (large ? "large_" : "") + name;
        if (!FAMILIES.contains(family) || (!first && stage == 0)) return null;
        int targetStage = first ? 0 : stage - 1;
        Block target = Services.PLATFORM.getBlock(new CommonId("buildscape",
                (large ? "large_" : "") + STAGES[targetStage] + name));
        return target == null || target == Blocks.AIR ? null : target;
    }

    private static void replace(Level level, BlockPos pos, BlockState state, Block target) {
        BlockState next = CopperOxidationHandler.copyStateProperties(state, target.defaultBlockState());
        if (state.hasProperty(DoorBlock.HALF)) {
            BlockPos lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upper = lower.above();
            level.setBlock(lower, CopperOxidationHandler.copyStateProperties(level.getBlockState(lower),
                    target.defaultBlockState()).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 18);
            level.setBlock(upper, CopperOxidationHandler.copyStateProperties(level.getBlockState(upper),
                    target.defaultBlockState()).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
        } else {
            if (state.getBlock() != target) {
                if (target instanceof ButtonBlock && next.getValue(ButtonBlock.POWERED)) {
                    level.scheduleTick(pos, target, 20);
                } else if (target instanceof WeightedPressurePlateBlock && next.getValue(WeightedPressurePlateBlock.POWER) > 0) {
                    level.scheduleTick(pos, target, 10);
                }
            }
            level.setBlock(pos, next, 3);
        }
    }
}
