package com.kingodogo.buildscape.mixin.neoforge121;

import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FluidInteractionRegistry.class, remap = false)
public abstract class FluidInteractionMixin {
    @Redirect(method = "canInteract", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/neoforge/fluids/FluidInteractionRegistry$HasFluidInteraction;test(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;)Z"))
    private static boolean buildscape$excludeExperienceMixing(FluidInteractionRegistry.HasFluidInteraction predicate,
            Level level, BlockPos pos, BlockPos relativePos, FluidState state) {
        Fluid current = state.getType();
        Fluid neighbor = level.getFluidState(relativePos).getType();
        if ((ModFluids.isExperience(current) && Fluids.LAVA.isSame(neighbor))
                || (ModFluids.isExperience(neighbor) && Fluids.LAVA.isSame(current))) return false;
        return predicate.test(level, pos, relativePos, state);
    }
}
