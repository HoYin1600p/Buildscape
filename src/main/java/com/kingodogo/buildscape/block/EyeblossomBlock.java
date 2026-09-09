package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BonemealableBlock;

import java.util.Random;

public class EyeblossomBlock extends FlowerBlock implements BonemealableBlock {
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");
    private final boolean isOpen;
    protected static final VoxelShape SHAPE = box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);

    public EyeblossomBlock(boolean isOpen, MobEffect effect, int duration, BlockBehaviour.Properties properties) {
        super(effect, duration, properties);
        this.isOpen = isOpen;
        this.registerDefaultState(this.stateDefinition.any().setValue(WAXED, false));
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return false;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        EyeblossomTransitionHandler.track(level, pos);
        synchronizeWithTime(level, pos, state, false);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level instanceof ServerLevel serverLevel) {
            EyeblossomTransitionHandler.track(serverLevel, pos);
            serverLevel.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel && !(newState.getBlock() instanceof EyeblossomBlock)) {
            EyeblossomTransitionHandler.untrack(serverLevel, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    void synchronizeWithTime(ServerLevel level, BlockPos pos, BlockState state, boolean playSound) {
        boolean night = level.isNight();
        if (!EyeblossomTransitionHandler.shouldTransition(isOpen, state.getValue(WAXED), night)) {
            return;
        }

        BlockState replacement = (night
                ? ModBlocks.OPEN_EYEBLOSSOM.get()
                : ModBlocks.CLOSED_EYEBLOSSOM.get())
                .defaultBlockState()
                .setValue(WAXED, false);
        level.setBlock(pos, replacement, 3);

        if (playSound) {
            level.playSound(
                    null,
                    pos,
                    night ? SoundEvents.AMETHYST_BLOCK_CHIME : SoundEvents.AZALEA_LEAVES_BREAK,
                    SoundSource.BLOCKS,
                    night ? 1.0F : 0.8F,
                    night ? 1.2F : 0.9F
            );
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WAXED);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        ItemStack flowerStack = new ItemStack(this);
        ItemEntity itemEntity = new ItemEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                flowerStack
        );
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}
