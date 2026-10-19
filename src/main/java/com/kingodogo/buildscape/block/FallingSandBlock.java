package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;

public class FallingSandBlock extends Block {

    private final RegistryObject<Item> dropItem;

    public FallingSandBlock(
            Properties properties,
            RegistryObject<Item> dropItem
    ) {
        super(properties);
        this.dropItem = dropItem;
    }

    public FallingSandBlock(Properties properties) {
        super(properties);
        this.dropItem = null;
    }

    @Override
    public void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean isMoving
    ) {
        if (
                !level.isClientSide &&
                        level instanceof ServerLevel
        ) {
            ((ServerLevel) level).scheduleTick(
                    pos,
                    this,
                    this.getDelayAfterPlace()
            );
        }
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighborBlock,
            BlockPos neighborPos,
            boolean movedByPiston
    ) {
        if (
                !level.isClientSide &&
                        level instanceof ServerLevel
        ) {
            ((ServerLevel) level).scheduleTick(
                    pos,
                    this,
                    this.getDelayAfterPlace()
            );
        }
    }

    @Override
    public void tick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        if (isFree(level, pos)) {
            FallingBlockEntity.fall(level, pos, state);
        }
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    protected boolean isFree(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return (
                belowState.getMaterial().isReplaceable() &&
                        belowState.getFluidState().isEmpty()
        );
    }

}
