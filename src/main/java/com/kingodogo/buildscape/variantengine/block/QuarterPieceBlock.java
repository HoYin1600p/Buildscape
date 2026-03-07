package com.kingodogo.buildscape.variantengine.block;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class QuarterPieceBlock extends Block implements SimpleWaterloggedBlock, ExtShapeBlockInterface {
    public static final BooleanProperty NORTH_WEST = BooleanProperty.create("north_west");
    public static final BooleanProperty NORTH_EAST = BooleanProperty.create("north_east");
    public static final BooleanProperty SOUTH_WEST = BooleanProperty.create("south_west");
    public static final BooleanProperty SOUTH_EAST = BooleanProperty.create("south_east");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NW_SHAPE = Block.box(0, 0, 0, 8, 16, 8);
    protected static final VoxelShape NE_SHAPE = Block.box(8, 0, 0, 16, 16, 8);
    protected static final VoxelShape SW_SHAPE = Block.box(0, 0, 8, 8, 16, 16);
    protected static final VoxelShape SE_SHAPE = Block.box(8, 0, 8, 16, 16, 16);

    private final Block baseBlock;

    public QuarterPieceBlock(Properties properties, Block baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH_WEST, false)
                .setValue(NORTH_EAST, false)
                .setValue(SOUTH_WEST, false)
                .setValue(SOUTH_EAST, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public Block getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.QUARTER_PIECE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        if (state.getValue(NORTH_WEST)) shape = Shapes.or(shape, NW_SHAPE);
        if (state.getValue(NORTH_EAST)) shape = Shapes.or(shape, NE_SHAPE);
        if (state.getValue(SOUTH_WEST)) shape = Shapes.or(shape, SW_SHAPE);
        if (state.getValue(SOUTH_EAST)) shape = Shapes.or(shape, SE_SHAPE);
        return shape.isEmpty() ? Shapes.block() : shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        BlockState existingState = ctx.getLevel().getBlockState(blockPos);
        FluidState fluidState = ctx.getLevel().getFluidState(blockPos);

        double x = ctx.getClickLocation().x - (double) blockPos.getX();
        double z = ctx.getClickLocation().z - (double) blockPos.getZ();

        BooleanProperty targetPart = getPartFromLocation(x, z);

        if (existingState.is(this)) {
            if (!existingState.getValue(targetPart)) {
                return existingState.setValue(targetPart, true);
            }
        }

        return this.defaultBlockState()
                .setValue(targetPart, true)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack itemstack = context.getItemInHand();
        if (itemstack.is(this.asItem())) {
            BlockPos blockPos = context.getClickedPos();
            double x = context.getClickLocation().x - (double) blockPos.getX();
            double z = context.getClickLocation().z - (double) blockPos.getZ();
            BooleanProperty part = getPartFromLocation(x, z);
            return !state.getValue(part);
        }
        return false;
    }

    private BooleanProperty getPartFromLocation(double x, double z) {
        if (x < 0.5) {
            return z < 0.5 ? NORTH_WEST : SOUTH_WEST;
        } else {
            return z < 0.5 ? NORTH_EAST : SOUTH_EAST;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }
}
