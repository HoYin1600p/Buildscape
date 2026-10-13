package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {
    @Inject(method = "appendItemLayers", at = @At("RETURN"))
    private void buildscape$rememberStack(ItemStackRenderState state, ItemStack stack, ItemDisplayContext context,
            Level level, ItemOwner owner, int seed, CallbackInfo ci) {
        FestiveSubmission.remember(state, stack);
    }
}
