package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
public abstract class MuffBlock extends BaseEntityBlock implements ICommonInteractable, ICommonNeighborAware {
    public static final IntegerProperty RADIUS = IntegerProperty.create("radius", 1, 32);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public MuffBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(RADIUS, 1)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RADIUS, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(RADIUS, 1)
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        return cycleRadius(state, level, pos, player);
    }
    private InteractionResult cycleRadius(BlockState state, Level level, BlockPos pos, Player player) {
        int radius = state.getValue(RADIUS);
        int nextRadius = radius + 1;
        if (nextRadius > 32) {
            nextRadius = 1;
        }

        BlockState newState = state.setValue(RADIUS, nextRadius);
        level.setBlock(pos, newState, 3);

        int note = (nextRadius - 1) * 24 / 31;
        float pitch = (float) Math.pow(2.0, (double) (note - 12) / 12.0);
        Services.PLATFORM.playNoteHarp(level, pos, pitch);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.NOTE,
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + 1.2D,
                    (double) pos.getZ() + 0.5D,
                    0,
                    (double) note / 24.0D,
                    0.0D,
                    0.0D,
                    1.0D);
        }

        Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.translatable("message.buildscape.muff_block.radius", nextRadius));
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block block, BlockPos fromPos, boolean isMoving) {
        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
            if (hasSignal && !level.isClientSide()) {
                Player nearestPlayer = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32.0, false);
                if (nearestPlayer instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    Services.PLATFORM.awardStat(serverPlayer, new com.kingodogo.buildscape.util.CommonId("buildscape", "muff_blocks_activated"));
                    com.kingodogo.buildscape.event.AdvancementMilestoneLogic.grant(serverPlayer, "can_you_hear_me_now");
                }
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MuffBlockEntity(pos, state);
    }
}
