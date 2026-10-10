package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class SnowyTallGrassBlock extends DoublePlantBlock implements SinksOnFarmland, ICommonShapeUpdate {

    private final Supplier<Block> snowBricksSupplier;

    public SnowyTallGrassBlock(BlockBehaviour.Properties properties) {
        this(properties, null);
    }

    public SnowyTallGrassBlock(BlockBehaviour.Properties properties, Supplier<Block> snowBricksSupplier) {
        super(properties);
        this.snowBricksSupplier = snowBricksSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, ON_FARMLAND);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = super.getShape(state, level, pos, context);
        if (state.getValue(ON_FARMLAND)) {
            return shape.move(0, -0.0625D, 0);
        }
        return shape;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        Block snowBricks = snowBricksSupplier != null ? snowBricksSupplier.get() : null;
        return (
                state.is(BlockTags.DIRT) ||
                state.is(Blocks.FARMLAND) ||
                state.is(Blocks.SNOW_BLOCK) ||
                (snowBricks != null && state.is(snowBricks))
        );
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return super.canSurvive(state, level, pos);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        if (pos.getY() < Services.PLATFORM.getMaxBuildHeight(context.getLevel()) - 1 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) {
            BlockState state = super.getStateForPlacement(context);
            if (state != null) {
                return state.setValue(ON_FARMLAND, shouldSink(context.getLevel(), pos));
            }
        }
        return null;
    }

    @Override
    public BlockState onUpdateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelReader level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        BlockPos basePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        return state.setValue(ON_FARMLAND, shouldSink(level, basePos));
    }

    public List<ItemStack> getReferenceDrops(BlockState state, ItemStack tool) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return Collections.emptyList();
        }
        if (tool != null && !tool.isEmpty()) {
            if (tool.is(Items.SHEARS) || Services.PLATFORM.hasSilkTouch(tool)) {
                return Collections.singletonList(new ItemStack(this));
            }
        }
        return Collections.emptyList();
    }
}
