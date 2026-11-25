package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.mixinsupport.FireworkContext;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketContextMixin {
    @Shadow public abstract ItemStack getItem();
    @Inject(method = "handleEntityEvent(B)V", at = @At("HEAD"))
    private void buildscape$beginExplosion(byte event, CallbackInfo ci) {
        if (event == 17) FireworkContext.begin(getItem());
    }
    @Inject(method = "handleEntityEvent(B)V", at = @At("RETURN"))
    private void buildscape$endExplosion(byte event, CallbackInfo ci) {
        if (event == 17) FireworkContext.end();
    }
}
