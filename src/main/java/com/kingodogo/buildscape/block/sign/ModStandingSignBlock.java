package com.kingodogo.buildscape.block.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;

public class ModStandingSignBlock extends StandingSignBlock {

    public ModStandingSignBlock(
            BlockBehaviour.Properties properties,
            WoodType woodType) {
        super(properties, woodType);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MangroveSignBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty()) {
            if (heldItem.getItem() instanceof DyeItem ||
                    heldItem.getItem() == Items.GLOW_INK_SAC ||
                    heldItem.getItem() == Items.INK_SAC) {
                return super.use(state, level, pos, player, hand, hit);
            }
        }

        if (blockEntity instanceof MangroveSignBlockEntity &&
                !player.isCrouching() &&
                heldItem.isEmpty()) {
            return super.use(state, level, pos, player, hand, hit);
        }

        return InteractionResult.PASS;
    }
}
