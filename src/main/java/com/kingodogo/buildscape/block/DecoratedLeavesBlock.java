package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class DecoratedLeavesBlock extends LeavesBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public DecoratedLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(DISTANCE, 7)
                        .setValue(PERSISTENT, false)
                        .setValue(LIT, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            net.minecraft.world.level.block.state.StateDefinition.Builder<
                    net.minecraft.world.level.block.Block,
                    BlockState
                    > builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    public int getLightEmission(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.getValue(LIT) ? 7 : 0;
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
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        boolean currentLit = state.getValue(LIT);
        BlockState newState = state.setValue(LIT, !currentLit);
        level.setBlock(pos, newState, 3);

        if (currentLit) {
            level.playSound(
                    null,
                    pos,
                    net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_OFF,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    0.3f,
                    1.0f
            );
        } else {
            level.playSound(
                    null,
                    pos,
                    net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_ON,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    0.3f,
                    1.0f
            );
        }

        return InteractionResult.SUCCESS;
    }
}
