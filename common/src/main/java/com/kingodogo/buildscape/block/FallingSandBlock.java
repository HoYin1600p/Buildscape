package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;
public class FallingSandBlock extends Block implements ICommonNeighborAware {
    @SuppressWarnings("unused")
    private final Supplier<? extends Item> dropItem;
    public FallingSandBlock(Properties properties, Supplier<? extends Item> dropItem) {
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
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, this.getDelayAfterPlace());
        }
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, this.getDelayAfterPlace());
        }
    }
    protected int getDelayAfterPlace() {
        return 2;
    }
    protected boolean isFree(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return com.kingodogo.buildscape.platform.Services.PLATFORM.isReplaceable(belowState)
                && belowState.getFluidState().isEmpty();
    }

    public void onScheduledTick(BlockState state, ServerLevel level, BlockPos pos) {
        if (isFree(level, pos)) FallingBlockEntity.fall(level, pos, state);
    }
}
