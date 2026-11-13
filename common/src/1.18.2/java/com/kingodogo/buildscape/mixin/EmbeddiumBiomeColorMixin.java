package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.biome.BiomeTintRendering;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache", remap = false)
abstract class EmbeddiumBiomeColorMixin {
    @Redirect(method = "updateColorBuffers", remap = false, require = 0,
            at = @At(value = "INVOKE", remap = true,
                    target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/level/biome/Biome;DD)I"))
    private int buildscape$biomeTint(ColorResolver resolver, Biome biome, double x, double z) {
        return BiomeTintRendering.resolveColor(resolver, biome, x, z);
    }
}
