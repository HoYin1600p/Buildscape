package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
public class HangingMossBlock extends Block {

    public static final BooleanProperty TIP = BooleanProperty.create("tip");
    public static final BooleanProperty SHEARED = ModBlockProperties.SHEARED;
    protected static final VoxelShape TIP_SHAPE = Block.box(1.0D, 2.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape BODY_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public HangingMossBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TIP, true).setValue(SHEARED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TIP) ? TIP_SHAPE : BODY_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        return aboveState.is(this)
                || aboveState.is(BlockTags.LEAVES)
                || aboveState.getBlock() instanceof LeavesBlock
                || Block.canSupportCenter(level, abovePos, Direction.DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();
        if (this.canSurvive(this.defaultBlockState(), level, pos)) {
            boolean isTip = !level.getBlockState(pos.below()).is(this);
            return this.defaultBlockState().setValue(TIP, isTip).setValue(SHEARED, false);
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIP, SHEARED);
    }

    public static BlockPos findTip(BlockGetter level, BlockPos pos, Block block) {
        BlockPos tipPos = pos;
        while (level.getBlockState(tipPos.below()).is(block)) {
            tipPos = tipPos.below();
        }
        return tipPos;
    }
}
