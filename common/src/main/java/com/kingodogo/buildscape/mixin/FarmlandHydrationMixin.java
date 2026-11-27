package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.CascadeWaterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmlandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandHydrationMixin {
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void buildscape$checkCascadeWater(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        if (level instanceof Level world && CascadeWaterManager.isNearCascadeWater(world, pos)) {
            callback.setReturnValue(true);
        }
    }
}
