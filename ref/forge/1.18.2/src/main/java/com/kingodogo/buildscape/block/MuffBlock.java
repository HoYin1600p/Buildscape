package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class MuffBlock extends Block implements EntityBlock {

    public static final IntegerProperty RADIUS = IntegerProperty.create("radius", 1, 32);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public MuffBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(RADIUS, 1)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RADIUS, POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(RADIUS, 1)
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        int radius = state.getValue(RADIUS);
        int nextRadius = radius + 1;
        if (nextRadius > 32) {
            nextRadius = 1;
        }

        state = state.setValue(RADIUS, nextRadius);
        level.setBlock(pos, state, 3);

        int note = (nextRadius - 1) * 24 / 31;
        float pitch = (float) Math.pow(2.0, (double) (note - 12) / 12.0);
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.RECORDS, 3.0F, pitch);

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

        player.displayClientMessage(new TranslatableComponent("message.buildscape.muff_block.radius", nextRadius), true);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
            if (hasSignal && !level.isClientSide) {
                Player nearestPlayer = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32.0, false);
                if (nearestPlayer instanceof ServerPlayer serverPlayer) {
                    serverPlayer.awardStat(com.kingodogo.buildscape.stat.ModStats.MUFF_BLOCKS_ACTIVATED);
                    com.kingodogo.buildscape.event.AdvancementEvents.grant(serverPlayer, "can_you_hear_me_now");
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MuffBlockEntity(pos, state);
    }
}
