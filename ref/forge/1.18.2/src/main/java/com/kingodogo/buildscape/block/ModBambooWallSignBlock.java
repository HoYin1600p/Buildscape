package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;

public class ModBambooWallSignBlock extends WallSignBlock {

    public ModBambooWallSignBlock(
            BlockBehaviour.Properties properties,
            WoodType woodType
    ) {
        super(properties, woodType);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BambooSignBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public net.minecraft.world.InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            net.minecraft.world.InteractionHand hand,
            BlockHitResult hit
    ) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        net.minecraft.world.item.ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty()) {
            if (
                    heldItem.getItem() instanceof net.minecraft.world.item.DyeItem ||
                            heldItem.getItem() == net.minecraft.world.item.Items.GLOW_INK_SAC
            ) {
                return super.use(state, level, pos, player, hand, hit);
            }
        }

        if (
                blockEntity instanceof BambooSignBlockEntity &&
                        !player.isCrouching() &&
                        heldItem.isEmpty()
        ) {
            return super.use(state, level, pos, player, hand, hit);
        }

        return net.minecraft.world.InteractionResult.PASS;
    }
}
