package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.entity.SeatEntity;
import com.kingodogo.buildscape.platform.Services;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
public class LogSlabBlock extends ModSlabBlock implements ICommonInteractable {

    public LogSlabBlock(Supplier<Block> baseBlock, BlockBehaviour.Properties properties) {
        super(baseBlock != null ? baseBlock.get() : null, properties);
    }

    public LogSlabBlock(Block baseBlock, BlockBehaviour.Properties properties) {
        super(baseBlock, properties);
    }

    public LogSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND && !player.isShiftKeyDown()) {
            if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() instanceof BlockItem) {
                return InteractionResult.PASS;
            }

            if (player.isPassenger()) {
                return InteractionResult.PASS;
            }

            List<Entity> seats = level.getEntitiesOfClass(Entity.class, new AABB(pos), seat -> seat instanceof SeatEntity);
            if (!seats.isEmpty()) {
                return InteractionResult.PASS;
            }

            double yOffset = 0.5D;
            SlabType type = state.getValue(TYPE);
            if (type == SlabType.TOP || type == SlabType.DOUBLE) {
                yOffset = 1.0D;
            }

            if (!level.isClientSide()) {
                SeatEntity.createSeat(level, pos.getX() + 0.5D, pos.getY() + yOffset - 0.2D, pos.getZ() + 0.5D, player);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }
}
