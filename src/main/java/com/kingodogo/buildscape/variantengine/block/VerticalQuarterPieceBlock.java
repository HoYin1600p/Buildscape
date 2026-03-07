package com.kingodogo.buildscape.variantengine.block;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.util.HorizontalCornerDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class VerticalQuarterPieceBlock extends Block implements SimpleWaterloggedBlock, ExtShapeBlockInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<HorizontalCornerDirection> FACING = EnumProperty.create("facing", HorizontalCornerDirection.class);
    public static final Map<HorizontalCornerDirection, VoxelShape> VOXELS = new EnumMap<>(HorizontalCornerDirection.class);

    static {
        for (HorizontalCornerDirection dir : HorizontalCornerDirection.values()) {
            net.minecraft.core.Vec3i vec = dir.getVector();
            VOXELS.put(dir, Block.box(
                    Math.min(vec.getX() + 1, 1) * 8, 0,
                    Math.min(vec.getZ() + 1, 1) * 8, Math.max(vec.getX() + 1, 1) * 8, 16,
                    Math.max(vec.getZ() + 1, 1) * 8
            ));
        }
    }

    private final Block baseBlock;

    public VerticalQuarterPieceBlock(Properties properties, Block baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(FACING, HorizontalCornerDirection.SOUTH_WEST));
    }

    @Override
    public Block getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.VERTICAL_QUARTER_PIECE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(blockPos);
        double x_diff = context.getClickLocation().x - context.getClickedPos().getX();
        double z_diff = context.getClickLocation().z - context.getClickedPos().getZ();

        HorizontalCornerDirection facing;
        if (x_diff < 0.5) {
            facing = z_diff < 0.5 ? HorizontalCornerDirection.NORTH_WEST : HorizontalCornerDirection.SOUTH_WEST;
        } else {
            facing = z_diff < 0.5 ? HorizontalCornerDirection.NORTH_EAST : HorizontalCornerDirection.SOUTH_EAST;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VOXELS.get(state.getValue(FACING));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, state.getValue(FACING).rotate(rotation));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, state.getValue(FACING).mirror(mirror));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
}
