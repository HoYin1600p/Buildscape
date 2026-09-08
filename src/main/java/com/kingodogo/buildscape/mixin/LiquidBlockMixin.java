package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {
    @Redirect(method = "shouldSpreadLiquid", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean buildscape$excludeExperienceFromLavaMixing(FluidState state, TagKey<Fluid> tag) {
        return state.is(tag) && !(tag.equals(FluidTags.WATER) && ModFluids.isExperience(state.getType()));
    }
}
