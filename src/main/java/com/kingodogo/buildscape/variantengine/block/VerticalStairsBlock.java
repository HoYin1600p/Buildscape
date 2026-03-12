package com.kingodogo.buildscape.variantengine.block;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.util.HorizontalCornerDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import java.util.Random;

import java.util.EnumMap;
import java.util.Map;

public class VerticalStairsBlock extends Block implements SimpleWaterloggedBlock, ExtShapeBlockInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<HorizontalCornerDirection> FACING = EnumProperty.create("facing", HorizontalCornerDirection.class);
    public static final Map<HorizontalCornerDirection, VoxelShape> VOXELS = new EnumMap<>(HorizontalCornerDirection.class);

    static {
        for (HorizontalCornerDirection dir : HorizontalCornerDirection.values()) {
            net.minecraft.core.Vec3i vec = dir.getVector();
            VoxelShape quarterShape = Block.box(
                    Math.min(vec.getX() + 1, 1) * 8, 0,
                    Math.min(vec.getZ() + 1, 1) * 8, Math.max(vec.getX() + 1, 1) * 8, 16,
                    Math.max(vec.getZ() + 1, 1) * 8
            );
            VOXELS.put(dir.getOpposite(), Shapes.join(Shapes.block(), quarterShape, BooleanOp.ONLY_FIRST));
        }
    }

    private final Block baseBlock;

    public VerticalStairsBlock(Properties properties, Block baseBlock) {
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
        return BlockShape.VERTICAL_STAIRS;
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

        if (com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isFallingBlock(this.baseBlock)) {
            context.getLevel().scheduleTick(blockPos, this, 2);
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
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return this.getShape(state, level, pos, CollisionContext.empty());
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
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
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        Block adjBlock = adjacentState.getBlock();
        BlockState adjBaseState = adjacentState;
        
        if (adjBlock instanceof ExtShapeBlockInterface extShape) {
            adjBaseState = extShape.getBaseBlock().defaultBlockState();
        }
        
        boolean baseWouldSkip = this.baseBlock.skipRendering(this.defaultBlockState(), adjBaseState, side);

        // Fallback safety check for tricky glass implementations
        if (!baseWouldSkip && com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isGlass(this.baseBlock)) {
            Block pureAdjBlock = (adjBlock instanceof ExtShapeBlockInterface ext) ? ext.getBaseBlock() : adjBlock;
            if (com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isGlass(pureAdjBlock)) {
                if (this.baseBlock == pureAdjBlock) {
                    baseWouldSkip = true;
                }
            }
        }

        if (baseWouldSkip) {
            if (side == Direction.UP || side == Direction.DOWN) {
                if (adjBlock instanceof VerticalStairsBlock && adjacentState.hasProperty(FACING)) {
                    return state.getValue(FACING) == adjacentState.getValue(FACING);
                } else if (adjBlock instanceof SlabBlock || adjBlock instanceof StairBlock || adjBlock instanceof VerticalSlabBlock) {
                    return false;
                }
                return true; 
            }
            
            if (adjBlock instanceof VerticalStairsBlock && adjacentState.hasProperty(FACING)) {
                HorizontalCornerDirection myFacing = state.getValue(FACING);
                HorizontalCornerDirection adjFacing = adjacentState.getValue(FACING);
                return getFaceProfile(myFacing, side) == getFaceProfile(adjFacing, side.getOpposite()) && getFaceProfile(myFacing, side) != FaceProfile.NONE;
            } else if (adjBlock instanceof SlabBlock || adjBlock instanceof StairBlock || adjBlock instanceof VerticalSlabBlock) {
                return false;
            } else {
                return getFaceProfile(state.getValue(FACING), side) == FaceProfile.FULL;
            }
        }
        
        return super.skipRendering(state, adjacentState, side);
    }
    
    private enum FaceProfile { FULL, NORTH_HALF, SOUTH_HALF, WEST_HALF, EAST_HALF, NONE }

    private FaceProfile getFaceProfile(HorizontalCornerDirection facing, Direction side) {
        return switch (facing) {
            case NORTH_WEST -> switch (side) {
                case NORTH, WEST -> FaceProfile.FULL;
                case SOUTH -> FaceProfile.WEST_HALF;
                case EAST -> FaceProfile.NORTH_HALF;
                default -> FaceProfile.NONE;
            };
            case NORTH_EAST -> switch (side) {
                case NORTH, EAST -> FaceProfile.FULL;
                case SOUTH -> FaceProfile.EAST_HALF;
                case WEST -> FaceProfile.NORTH_HALF;
                default -> FaceProfile.NONE;
            };
            case SOUTH_WEST -> switch (side) {
                case SOUTH, WEST -> FaceProfile.FULL;
                case NORTH -> FaceProfile.WEST_HALF;
                case EAST -> FaceProfile.SOUTH_HALF;
                default -> FaceProfile.NONE;
            };
            case SOUTH_EAST -> switch (side) {
                case SOUTH, EAST -> FaceProfile.FULL;
                case NORTH -> FaceProfile.EAST_HALF;
                case WEST -> FaceProfile.SOUTH_HALF;
                default -> FaceProfile.NONE;
            };
        };
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
        if (com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isFallingBlock(this.baseBlock)) {
            level.scheduleTick(currentPos, this, 2);
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isFallingBlock(this.baseBlock)) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isFallingBlock(this.baseBlock) && (level.isEmptyBlock(pos.below()) || FallingBlock.isFree(level.getBlockState(pos.below()))) && pos.getY() >= level.getMinBuildHeight()) {
            FallingBlockEntity.fall(level, pos, state);
        }
    }
}
