package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

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
            double fillHeight = 0.4375 + (double) levelVal * 0.1875;
            double x = (double) pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double y = (double) pos.getY() + fillHeight;
            double z = (double) pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            level.addParticle(ModParticles.XP_PARTICLE.get(), x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(Items.CAULDRON);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
            Player player) {
        return new ItemStack(Items.CAULDRON);
    }
}
