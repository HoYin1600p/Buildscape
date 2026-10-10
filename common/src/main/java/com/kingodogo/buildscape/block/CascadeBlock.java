package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
public class CascadeBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, ICommonInteractable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final CommonId NO_MIST_BLOCK_ID = new CommonId(BuildscapeCommon.MOD_ID, "cascade_block_no_mist");
    private static final CommonId BOTTLE_OF_MIST_ID = new CommonId(BuildscapeCommon.MOD_ID, "bottle_of_mist");

    public CascadeBlock(BlockBehaviour.Properties properties) {
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
        Block noMistBlock = Services.PLATFORM.getBlock(NO_MIST_BLOCK_ID);
        return adjacentState.is(this) || noMistBlock != null && adjacentState.is(noMistBlock)
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return type == ModBlockEntities.CASCADE_TYPE ? (lvl, p, st, be) -> CascadeBlockEntity.clientTick(lvl, p, st, (CascadeBlockEntity) be) : null;
        }
        return null;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide()) {
                Block noMistBlock = resolveNoMistBlock();
                if (noMistBlock != null) {
                    BlockState nextState = noMistBlock.defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                    level.setBlock(pos, nextState, 3);
                }

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                ItemStack mistBottle = new ItemStack(Services.PLATFORM.getItem(BOTTLE_OF_MIST_ID));
                if (!player.getInventory().add(mistBottle)) {
                    player.drop(mistBottle, false);
                }
                Services.PLATFORM.playBottleFill(level, pos);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        if (heldItem.isEmpty() && level.getBlockEntity(pos) instanceof CascadeBlockEntity be) {
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
    private Block resolveNoMistBlock() {
        return Services.PLATFORM.getBlock(NO_MIST_BLOCK_ID);
    }
}
