package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class LogSlabBlock extends ModSlabBlock {

    public LogSlabBlock(Block baseBlock, BlockBehaviour.Properties properties) {
        super(baseBlock, properties);
    }

    public LogSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND && !player.isShiftKeyDown()) {

            if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.BlockItem) {
                return InteractionResult.PASS;
            }

            if (player.isPassenger()) {
                return InteractionResult.PASS;
            }

            List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
            if (!seats.isEmpty()) {
                return InteractionResult.PASS;
            }

            double yOffset = 0.5;
            SlabType type = state.getValue(TYPE);
            if (type == SlabType.TOP) {
                yOffset = 1.0;
            } else if (type == SlabType.DOUBLE) {
                yOffset = 1.0;
            }

            if (!level.isClientSide) {
                SeatEntity.createSeat(level, pos.getX() + 0.5, pos.getY() + yOffset - 0.2, pos.getZ() + 0.5, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
    }
}
