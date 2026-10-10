package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.util.BeaconScanContext;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public class BigOrnamentBlock extends Block implements SimpleWaterloggedBlock, ICommonInteractable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private final boolean tinted;

    public BigOrnamentBlock(BlockBehaviour.Properties properties) {
        this(properties, false);
    }

    public BigOrnamentBlock(BlockBehaviour.Properties properties, boolean tinted) {
        super(properties);
        this.tinted = tinted;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, true)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(LIT, true)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean isTinted() { return tinted; }

    public int getBeaconColor(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        if (tinted) {
            BeaconScanContext.markBlocking(level, pos, beaconPos);
            return 0xFFFFFF;
        }
        return state.getMapColor(level, pos).col;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        return toggleLight(state, level, pos);
    }

    private InteractionResult toggleLight(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        boolean currentLit = state.getValue(LIT);
        level.setBlock(pos, state.setValue(LIT, !currentLit), 3);
        Services.PLATFORM.playStoneButtonClick(level, pos, !currentLit);
        return InteractionResult.CONSUME;
    }
}
