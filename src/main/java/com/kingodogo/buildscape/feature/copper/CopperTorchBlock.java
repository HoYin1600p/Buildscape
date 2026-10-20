package com.kingodogo.buildscape.feature.copper;

import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class CopperTorchBlock extends TorchBlock {
    public CopperTorchBlock(BlockBehaviour.Properties properties) {
        super(properties, ParticleTypes.FLAME);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.7;
        double z = (double) pos.getZ() + 0.5;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
        level.addParticle(ModParticles.COPPER_FIRE_FLAME.get(), x, y, z, 0.0, 0.0, 0.0);
    }
}
