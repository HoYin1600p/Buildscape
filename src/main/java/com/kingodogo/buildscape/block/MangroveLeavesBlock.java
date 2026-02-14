package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

public class MangroveLeavesBlock
        extends LeavesBlock
        implements BonemealableBlock {


    private static final ThreadLocal<Direction> BONEMEAL_DIRECTION = new ThreadLocal<>();

    public MangroveLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(BlockStateProperties.WATERLOGGED, false)
                        .setValue(DISTANCE, 7)
                        .setValue(PERSISTENT, false)
        );
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() instanceof BoneMealItem) {
            Direction clickedDirection = hit.getDirection();
            BONEMEAL_DIRECTION.set(clickedDirection);

            try {
                if (clickedDirection != Direction.DOWN) {
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.PASS;
            } finally {
                BONEMEAL_DIRECTION.remove();
            }
        }

        return InteractionResult.PASS;
    }


    @Override
    public boolean isValidBonemealTarget(
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            boolean isClient
    ) {
        Direction bonemealDirection = BONEMEAL_DIRECTION.get();
        if (bonemealDirection != null && bonemealDirection != Direction.DOWN) {
            return false;
        }

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (
                belowState.is(ModBlocks.MANGROVE_PROPAGULE.get()) &&
                        belowState.getValue(MangrovePropaguleBlock.HANGING)
        ) {
            int currentAge = belowState.getValue(MangrovePropaguleBlock.AGE);
            return currentAge < 3;
        }

        return belowState.isAir() || belowState.is(Blocks.WATER);
    }

    @Override
    public boolean isBonemealSuccess(
            net.minecraft.world.level.Level level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        return true;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (
                belowState.is(ModBlocks.MANGROVE_PROPAGULE.get()) &&
                        belowState.getValue(MangrovePropaguleBlock.HANGING)
        ) {
            int currentAge = belowState.getValue(MangrovePropaguleBlock.AGE);
            if (currentAge < 3) {
                level.setBlock(
                        belowPos,
                        belowState.setValue(MangrovePropaguleBlock.AGE, currentAge + 1),
                        3
                );
            }
            return;
        }

        if (belowState.isAir() || belowState.is(Blocks.WATER)) {
            boolean canPlace = true;
            for (int i = 1; i <= 2; i++) {
                BlockPos checkPos = belowPos.below(i);
                BlockState checkState = level.getBlockState(checkPos);
                if (!checkState.isAir() && !checkState.is(Blocks.WATER)) {
                    canPlace = false;
                    break;
                }
            }

            if (canPlace) {
                BlockState propaguleState = ModBlocks.MANGROVE_PROPAGULE.get()
                        .defaultBlockState()
                        .setValue(MangrovePropaguleBlock.AGE, 0)
                        .setValue(MangrovePropaguleBlock.HANGING, true);

                if (belowState.is(Blocks.WATER)) {
                    propaguleState = propaguleState.setValue(
                            MangrovePropaguleBlock.WATERLOGGED,
                            true
                    );
                }

                level.setBlock(belowPos, propaguleState, 3);
            }
        }
    }



    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        super.randomTick(state, level, pos, random);
    }
}
