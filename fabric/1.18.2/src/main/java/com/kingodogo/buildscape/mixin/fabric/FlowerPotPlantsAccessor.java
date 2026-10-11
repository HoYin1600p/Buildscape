package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FlowerPotBlock.class)
public interface FlowerPotPlantsAccessor {
    @Accessor("POTTED_BY_CONTENT")
    static java.util.Map<Block, Block> buildscape$getPottedByContent() { throw new AssertionError(); }
}
