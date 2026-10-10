package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
public class StrawBedBlock extends BedBlock implements ICommonInteractable {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);

    public StrawBedBlock(BlockBehaviour.Properties properties) {
        super(DyeColor.YELLOW, properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (com.kingodogo.buildscape.platform.Services.PLATFORM.doesBedExplode(level)) {
            if (!level.isClientSide()) {
                BlockPos headPos = state.getValue(PART) == BedPart.HEAD ? pos : pos.relative(state.getValue(FACING));
                BlockPos footPos = state.getValue(PART) == BedPart.HEAD ? pos.relative(state.getValue(FACING).getOpposite()) : pos;

                level.destroyBlock(headPos, true);

                if (level.getBlockState(footPos).is(this)) {
                    level.removeBlock(footPos, false);
                }
            }
            return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }
}
