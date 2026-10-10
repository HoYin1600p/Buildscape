package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.world.item.FireworkStarItem")
public abstract class FireworkStarItemMixin {

    @Inject(method = "appendHoverText(Lnet/minecraft/nbt/CompoundTag;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$appendCustomShapeHoverText(@Coerce Object tag, List<Component> tooltip, CallbackInfo ci) {
        if (MixinFactory.appendFireworkCustomHoverText(tag, tooltip)) {
            ci.cancel();
        }
    }
}
