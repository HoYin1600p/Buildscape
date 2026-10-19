package com.kingodogo.buildscape.mixin.biometint;

import com.kingodogo.buildscape.feature.biome.ClientBiomeTints;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache", remap = false)
public class EmbeddiumBiomeColorMixin {
    @Redirect(method = "updateColorBuffers", remap = false, require = 0,
            at = @At(value = "INVOKE", remap = true,
                    target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/level/biome/Biome;DD)I"))
    private int buildscape$columnTint(ColorResolver resolver, Biome biome, double x, double z) {
        return ClientBiomeTints.resolveColor(resolver, biome, x, z);
    }
}
