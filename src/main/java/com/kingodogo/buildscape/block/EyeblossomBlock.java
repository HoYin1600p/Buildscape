package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class EyeblossomBlock extends FlowerBlock {
    private final boolean isOpen;
    protected static final VoxelShape SHAPE = box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);

    public EyeblossomBlock(boolean isOpen, MobEffect effect, int duration, BlockBehaviour.Properties properties) {
        super(effect, duration, properties);
        this.isOpen = isOpen;
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
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        boolean isNight = level.isNight();
        if (isOpen && !isNight) {
            // Close flower during day
            level.setBlock(pos, ModBlocks.CLOSED_EYEBLOSSOM.get().defaultBlockState(), 3);
            level.playSound(null, pos, SoundEvents.AZALEA_LEAVES_BREAK, SoundSource.BLOCKS, 0.8F, 0.9F);
        } else if (!isOpen && isNight) {
            // Open flower during night
            level.setBlock(pos, ModBlocks.OPEN_EYEBLOSSOM.get().defaultBlockState(), 3);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.2F);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
    }
}
