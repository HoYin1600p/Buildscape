package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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

    @Override
    public float getDestroyProgress(
            BlockState state,
            Player player,
            BlockGetter level,
            BlockPos pos
    ) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        if (speedMultiplier > 1.0F) {
            int efficiencyLevel = com.kingodogo.buildscape.platform.Services.PLATFORM.getEfficiencyLevel(tool);
            speedMultiplier += efficiencyLevel > 0 ? efficiencyLevel * efficiencyLevel + 1 : 0;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state) ? 30.0F : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
