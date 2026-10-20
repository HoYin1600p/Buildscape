package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.worldgen.ModConfiguredFeatures;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CherryTreeGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random random, boolean hasFlowers) {
        return Holder.direct(ModConfiguredFeatures.CHERRY_TREE.get());
    }
}
