package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public abstract class MangrovePropaguleBlock extends BushBlock implements SinksOnFarmland, SimpleWaterloggedBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 1);
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    private static final VoxelShape HANGING_SHAPE_0 = Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape HANGING_SHAPE_1 = Block.box(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape HANGING_SHAPE_2 = Block.box(7.0D, 7.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape HANGING_SHAPE_3 = Block.box(7.0D, 3.0D, 7.0D, 9.0D, 16.0D, 9.0D);

    public MangrovePropaguleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(STAGE, 0)
                .setValue(HANGING, false)
                .setValue(WATERLOGGED, false)
                .setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, STAGE, HANGING, WATERLOGGED, ON_FARMLAND);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(HANGING)) {
            int age = state.getValue(AGE);
            return switch (age) {
                case 0 -> HANGING_SHAPE_0;
                case 1 -> HANGING_SHAPE_1;
                case 2 -> HANGING_SHAPE_2;
                case 3 -> HANGING_SHAPE_3;
                default -> HANGING_SHAPE_0;
            };
        }
        VoxelShape shape = SHAPE;
        if (state.getValue(ON_FARMLAND)) {
            return shape.move(0, -0.0625D, 0);
        }
        return shape;
    }

    private boolean isLeavesAbove(BlockState aboveState) {
        if (aboveState.is(BlockTags.LEAVES)) return true;
        Block leaves = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlock(com.kingodogo.buildscape.util.CommonId.of("minecraft", "mangrove_leaves"));
        return leaves != null && aboveState.is(leaves);
    }

    private boolean isMudBelow(BlockState belowState) {
        Block mud = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlock(com.kingodogo.buildscape.util.CommonId.of("buildscape", "mud"));
        return mud != null && belowState.is(mud);
    }

    private boolean isMuddyRootsBelow(BlockState belowState) {
        Block roots = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlock(com.kingodogo.buildscape.util.CommonId.of("minecraft", "muddy_mangrove_roots"));
        return roots != null && belowState.is(roots);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState aboveState = level.getBlockState(pos.above());
        FluidState fluidstate = level.getFluidState(pos);

        boolean hanging = isLeavesAbove(aboveState);

        return this.defaultBlockState()
                .setValue(HANGING, hanging)
                .setValue(AGE, 0)
                .setValue(STAGE, 0)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(ON_FARMLAND, !hanging && shouldSink(level, pos));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HANGING)) {
            BlockState aboveState = level.getBlockState(pos.above());
            return isLeavesAbove(aboveState);
        } else {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            return (belowState.is(BlockTags.DIRT) && !belowState.is(Blocks.DIRT_PATH))
                    || belowState.is(Blocks.FARMLAND)
                    || belowState.is(Blocks.MOSS_BLOCK)
                    || isMudBelow(belowState)
                    || belowState.is(Blocks.CLAY)
                    || isMuddyRootsBelow(belowState);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean canBonemeal(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.getValue(HANGING)) {
            return state.getValue(AGE) < 3;
        }
        return true;
    }

    public void performBonemealEffect(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(HANGING)) {
            int currentAge = state.getValue(AGE);
            if (currentAge < 3) {
                level.setBlock(pos, state.setValue(AGE, currentAge + 1), 4);
            }
            return;
        }
        if (state.getValue(STAGE) < 1) {
            level.setBlock(pos, state.setValue(STAGE, state.getValue(STAGE) + 1), 4);
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, side);
    }
}
