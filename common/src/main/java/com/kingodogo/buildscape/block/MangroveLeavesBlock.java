package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
public abstract class MangroveLeavesBlock extends Block {

    public MangroveLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static Block getPropaguleBlock() {
        return Services.PLATFORM.getBlock(CommonId.of("minecraft", "mangrove_propagule"));
    }

    public static boolean canGrowPropagule(BlockGetter level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        Block propagule = getPropaguleBlock();
        if (propagule != null && belowState.is(propagule) && belowState.hasProperty(MangrovePropaguleBlock.HANGING) && belowState.getValue(MangrovePropaguleBlock.HANGING)) {
            int currentAge = belowState.getValue(MangrovePropaguleBlock.AGE);
            return currentAge < 3;
        }

        return belowState.isAir() || belowState.is(Blocks.WATER);
    }

    public static void growPropagule(ServerLevel level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        Block propagule = getPropaguleBlock();

        if (propagule != null && belowState.is(propagule) && belowState.hasProperty(MangrovePropaguleBlock.HANGING) && belowState.getValue(MangrovePropaguleBlock.HANGING)) {
            int currentAge = belowState.getValue(MangrovePropaguleBlock.AGE);
            if (currentAge < 3) {
                level.setBlock(belowPos, belowState.setValue(MangrovePropaguleBlock.AGE, currentAge + 1), 3);
            }
            return;
        }

        if (propagule != null && (belowState.isAir() || belowState.is(Blocks.WATER))) {
            BlockState propaguleState = propagule.defaultBlockState()
                    .setValue(MangrovePropaguleBlock.AGE, 0)
                    .setValue(MangrovePropaguleBlock.HANGING, true);

            if (belowState.is(Blocks.WATER)) {
                propaguleState = propaguleState.setValue(MangrovePropaguleBlock.WATERLOGGED, true);
            }

            level.setBlock(belowPos, propaguleState, 3);
        }
    }
}
