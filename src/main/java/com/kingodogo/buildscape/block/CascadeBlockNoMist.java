package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.kingodogo.buildscape.util.CommonId;
public class CascadeBlockNoMist extends Block implements EntityBlock, SimpleWaterloggedBlock, ICommonInteractable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final CommonId CASCADE_BLOCK_ID = CommonId.of("buildscape", "cascade_block");

    public CascadeBlockNoMist(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this)
                || adjacentState.is(Services.PLATFORM.getBlock(CASCADE_BLOCK_ID))
                || super.skipRendering(state, adjacentState, direction);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CascadeBlockEntity(pos, state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (player.getItemInHand(hand).isEmpty() && level.getBlockEntity(pos) instanceof CascadeBlockEntity be) {
            if (!level.isClientSide()) {
                int newLevel = be.cycleParticleLevel();
                int percentage = newLevel * 20;
                Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.cascade_particles", percentage + "%"));
                float pitch = 0.6f + (newLevel * 0.16f);
                Services.PLATFORM.playLeverClick(level, pos, pitch);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
