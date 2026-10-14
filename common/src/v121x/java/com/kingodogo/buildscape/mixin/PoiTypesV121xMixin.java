package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.CopperRodPoi;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PoiTypes.class)
public class PoiTypesV121xMixin {
    // Mod blocks do not exist when the vanilla POI types are built, so the rods join the lightning rod type on the first lookup.
    @Inject(method = "forState", at = @At("HEAD"))
    private static void buildscape$registerRodsOnForState(net.minecraft.world.level.block.state.BlockState state, CallbackInfoReturnable<?> cir) {
        CopperRodPoi.ensureRegistered();
    }

    @Inject(method = "hasPoi", at = @At("HEAD"))
    private static void buildscape$registerRodsOnHasPoi(net.minecraft.world.level.block.state.BlockState state, CallbackInfoReturnable<Boolean> cir) {
        CopperRodPoi.ensureRegistered();
    }
}
