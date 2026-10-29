package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v263.LightningCopperCleaning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltV26xMixin {
    // One walk cleans vanilla and BuildScape copper together, so the vanilla walk is replaced rather than run twice.
    @Inject(method = "clearCopperOnLightningStrike", at = @At("HEAD"), cancellable = true)
    private static void buildscape$clearCopper(Level level, BlockPos pos, CallbackInfo ci) {
        LightningCopperCleaning.clear(level, pos);
        ci.cancel();
    }
}
