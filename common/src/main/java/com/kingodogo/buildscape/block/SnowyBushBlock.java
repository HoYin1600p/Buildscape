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
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class SnowyBushBlock extends BushBlock implements SinksOnFarmland, ICommonShapeUpdate {

    protected static final VoxelShape SHAPE = Block.box(
            2.0D,
            0.0D,
            2.0D,
            14.0D,
            13.0D,
            14.0D
    );

    private final Supplier<Block> snowBricksSupplier;

    public SnowyBushBlock(BlockBehaviour.Properties properties) {
        this(properties, null);
    }

    public SnowyBushBlock(BlockBehaviour.Properties properties, Supplier<Block> snowBricksSupplier) {
        super(properties);
        this.snowBricksSupplier = snowBricksSupplier;
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
        Block snowBricks = snowBricksSupplier != null ? snowBricksSupplier.get() : null;
        return (
                state.is(BlockTags.DIRT) ||
                state.is(Blocks.FARMLAND) ||
                state.is(Blocks.SNOW_BLOCK) ||
                (snowBricks != null && state.is(snowBricks))
        );
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
    public BlockState onUpdateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelReader level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN) {
            return state.setValue(ON_FARMLAND, shouldSink(level, pos));
        }
        return state;
    }

    public List<ItemStack> getReferenceDrops(ItemStack tool) {
        if (tool != null && !tool.isEmpty()) {
            if (tool.is(Items.SHEARS) || Services.PLATFORM.hasSilkTouch(tool)) {
                return Collections.singletonList(new ItemStack(this));
            }
        }
        return Collections.emptyList();
    }
}
