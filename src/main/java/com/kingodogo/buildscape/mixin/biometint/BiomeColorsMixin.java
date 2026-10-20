package com.kingodogo.buildscape.mixin.biometint;

import com.kingodogo.buildscape.feature.biome.ClientBiomeTints;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {
    @Inject(method = "getAverageColor", at = @At("HEAD"), cancellable = true)
    private static void buildscape$columnTint(BlockAndTintGetter level, BlockPos pos, ColorResolver resolver,
            CallbackInfoReturnable<Integer> callback) {
        Integer color = ClientBiomeTints.color(level, pos, resolver);
        if (color != null) {
            callback.setReturnValue(color);
        }
    }
}
