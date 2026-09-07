package com.kingodogo.buildscape.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Random;

public class BoneDiceBlock extends Block {

    public static final IntegerProperty ROLL = IntegerProperty.create("roll", 1, 6);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BoneDiceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROLL, 1)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROLL, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Random random = context.getLevel().getRandom();
        int roll = 1 + random.nextInt(6);
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        return this.defaultBlockState().setValue(ROLL, roll).setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        int roll = state.getValue(ROLL);

        if (!level.isClientSide) {
            if (placer instanceof Player player) {
                player.displayClientMessage(
                        new TranslatableComponent("message.buildscape.bone_dice.rolled", roll)
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        true
                );
            }
            if (level instanceof ServerLevel serverLevel) {
                spawnHighlightParticles(serverLevel, pos);
            }
        }

        level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.8F, 1.2F);
        level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (player.getItemInHand(hand).isEmpty()) {
            if (!level.isClientSide) {
                Random random = level.getRandom();
                int newRoll = 1 + random.nextInt(6);
                Direction newFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockState newState = state.setValue(ROLL, newRoll).setValue(FACING, newFacing);
                level.setBlock(pos, newState, 3);

                player.displayClientMessage(
                        new TranslatableComponent("message.buildscape.bone_dice.rolled", newRoll)
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        true
                );

                if (level instanceof ServerLevel serverLevel) {
                    spawnHighlightParticles(serverLevel, pos);
                }

                level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.8F, 1.2F);
                level.playSound(null, pos, SoundEvents.BONE_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 1.2F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    public static void spawnHighlightParticles(ServerLevel level, BlockPos pos) {
        double y = pos.getY() + 1.02;
        double minX = pos.getX() + 0.15;
        double maxX = pos.getX() + 0.85;
        double minZ = pos.getZ() + 0.15;
        double maxZ = pos.getZ() + 0.85;

        for (int i = 0; i <= 4; i++) {
            double f = i / 4.0;
            level.sendParticles(ParticleTypes.WAX_ON, minX + f * (maxX - minX), y, minZ, 1, 0, 0, 0, 0);
            level.sendParticles(ParticleTypes.WAX_ON, minX + f * (maxX - minX), y, maxZ, 1, 0, 0, 0, 0);
            if (i > 0 && i < 4) {
                level.sendParticles(ParticleTypes.WAX_ON, minX, y, minZ + f * (maxZ - minZ), 1, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.WAX_ON, maxX, y, minZ + f * (maxZ - minZ), 1, 0, 0, 0, 0);
            }
        }

        level.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5, y + 0.05, pos.getZ() + 0.5, 6, 0.15, 0.02, 0.15, 0.02);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(ROLL);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(ROLL);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(ROLL);
    }
}
