package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Random;

public class ExperienceCauldronBlock extends LayeredCauldronBlock {
    public ExperienceCauldronBlock(BlockBehaviour.Properties properties, Map<Item, CauldronInteraction> interactions) {
        super(properties, (precipitation) -> false, interactions);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        int levelVal = state.getValue(LEVEL);
        if (levelVal > 0 && random.nextInt(25 - levelVal * 5) == 0) {
            double fillHeight = 0.4375D + (double)levelVal * 0.1875D;
            double x = (double) pos.getX() + 0.2D + random.nextDouble() * 0.6D;
            double y = (double) pos.getY() + fillHeight;
            double z = (double) pos.getZ() + 0.2D + random.nextDouble() * 0.6D;
            level.addParticle(ModParticles.XP_PARTICLE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
