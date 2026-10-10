package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
public abstract class ModBushBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    public ModBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public InteractionResult onInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.BONE_MEAL)) {
            if (!level.isClientSide()) {
                popResource(level, pos, new ItemStack(this.asItem()));
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.levelEvent(1505, pos, 0);
            }
            return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
