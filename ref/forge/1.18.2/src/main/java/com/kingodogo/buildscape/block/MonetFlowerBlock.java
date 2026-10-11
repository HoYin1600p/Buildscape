package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MonetFlowerBlock extends BushBlock implements SinksOnFarmland, BonemealableBlock {

    protected static final VoxelShape SHAPE = Block.box(
            2.0D,
            0.0D,
            2.0D,
            14.0D,
            13.0D,
            14.0D
    );

    public MonetFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ON_FARMLAND);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape shape = SHAPE;
        if (state.getValue(ON_FARMLAND)) {
            return shape.move(0, -0.0625D, 0);
        }
        return shape;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.is(BlockTags.DIRT) || state.is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return this.mayPlaceOn(belowState, level, belowPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return state.setValue(ON_FARMLAND, shouldSink(context.getLevel(), context.getClickedPos()));
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, net.minecraft.core.Direction direction, BlockState adjacentState, net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
        if (direction == net.minecraft.core.Direction.DOWN) {
            return state.setValue(ON_FARMLAND, shouldSink(level, pos));
        }
        return super.updateShape(state, direction, adjacentState, level, pos, adjacentPos);
    }

    @Override
    public boolean isValidBonemealTarget(
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            boolean isClient
    ) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        return true;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        ItemStack flowerStack = new ItemStack(this);
        ItemEntity itemEntity = new ItemEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                flowerStack
        );
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        return Collections.singletonList(new ItemStack(this));
    }
}
