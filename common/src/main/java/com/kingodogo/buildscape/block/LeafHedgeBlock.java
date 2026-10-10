package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public class LeafHedgeBlock extends WallBlock {

    public LeafHedgeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player && Services.PLATFORM.isOnGround(entity) && level.isClientSide()) {
            BlockPos playerBlockPos = entity.blockPosition();
            BlockPos blockBelowPlayer = playerBlockPos.below();
            BlockState blockBelow = level.getBlockState(blockBelowPlayer);
            if (blockBelow.getBlock() == this) {
                Services.PLATFORM.playVegetationStepSound(level, blockBelowPlayer, entity, this.getSoundType(blockBelow));
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    public void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Player && Services.PLATFORM.isOnGround(entity) && level.isClientSide()) {
            BlockPos playerBlockPos = entity.blockPosition();
            boolean isInside = playerBlockPos.getY() == pos.getY();

            BlockPos blockBelowPlayer = playerBlockPos.below();
            BlockState blockBelow = level.getBlockState(blockBelowPlayer);
            boolean isOnTop = blockBelow.getBlock() == this;

            if (!isOnTop) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos checkPos = blockBelowPlayer.offset(dx, 0, dz);
                        BlockState checkState = level.getBlockState(checkPos);
                        if (checkState.getBlock() == this) {
                            isOnTop = true;
                            blockBelowPlayer = checkPos;
                            blockBelow = checkState;
                            break;
                        }
                    }
                    if (isOnTop) break;
                }
            }

            if (isOnTop || isInside) {
                BlockPos soundPos = isOnTop ? blockBelowPlayer : pos;
                BlockState soundState = isOnTop ? blockBelow : state;
                Services.PLATFORM.playVegetationStepSound(level, soundPos, entity, this.getSoundType(soundState));
            }
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel = Services.PLATFORM.getEfficiencyLevel(player.getMainHandItem());
        ItemStack tool = player.getMainHandItem();

        float speedMultiplier = 1.0F;
        if (!tool.isEmpty()) {
            speedMultiplier = tool.getDestroySpeed(state);
        }

        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0
                    ? efficiencyLevel * efficiencyLevel + 1
                    : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state)
                ? 30.0F
                : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.is(this)) {
            return true;
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }
}
