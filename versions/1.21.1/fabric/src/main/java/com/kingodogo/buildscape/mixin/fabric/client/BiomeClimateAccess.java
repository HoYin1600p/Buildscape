package com.kingodogo.buildscape.mixin.fabric.client;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface BiomeClimateAccess {
    @Accessor("climateSettings")
    Biome.ClimateSettings buildscape$climateSettings();
}
