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

/**
 * A block that acts as a slab and allows players to sit on it.
 */
public class LogSlabBlock extends ModSlabBlock {

    public LogSlabBlock(Block baseBlock, BlockBehaviour.Properties properties) {
        super(baseBlock, properties);
    }

    public LogSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Only allow sitting on right-click with empty hand (or specific conditions)
        // If player is holding a block and not sneaking, they should place it (InteractionResult.PASS)
        if (hand == InteractionHand.MAIN_HAND && !player.isShiftKeyDown()) {
            
            // Check if player is holding an item that might be placed
            if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.BlockItem) {
                return InteractionResult.PASS;
            }

            // Check if player is already riding something
            if (player.isPassenger()) {
                return InteractionResult.PASS;
            }

            // Check if there is already someone sitting here
            List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
            if (!seats.isEmpty()) {
                return InteractionResult.PASS;
            }

            // Calculate seat height based on slab type
            double yOffset = 0.5; // Default for bottom slab
            SlabType type = state.getValue(TYPE);
            if (type == SlabType.TOP) {
                yOffset = 1.0;
            } else if (type == SlabType.DOUBLE) {
                yOffset = 1.0;
            }

            // Create seat entity and make player sit
            if (!level.isClientSide) {
                SeatEntity.createSeat(level, pos.getX() + 0.5, pos.getY() + yOffset - 0.2, pos.getZ() + 0.5, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
    }
}
