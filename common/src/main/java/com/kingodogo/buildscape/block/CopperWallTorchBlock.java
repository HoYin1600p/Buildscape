package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
public final class CopperWallTorchBlock {
    private CopperWallTorchBlock() {}

    public static void spawnParticles(BlockState state, Level level, BlockPos pos) {
        Direction direction = state.getValue(WallTorchBlock.FACING);
        Direction opposite = direction.getOpposite();
        double x = (double) pos.getX() + 0.5D + 0.27D * (double) opposite.getStepX();
        double y = (double) pos.getY() + 0.92D;
        double z = (double) pos.getZ() + 0.5D + 0.27D * (double) opposite.getStepZ();
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ModParticles.COPPER_FIRE_FLAME.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
