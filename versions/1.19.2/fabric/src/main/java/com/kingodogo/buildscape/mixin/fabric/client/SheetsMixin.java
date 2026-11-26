package com.kingodogo.buildscape.mixin.fabric.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
public abstract class SheetsMixin {
    @Inject(method = "createSignMaterial(Lnet/minecraft/world/level/block/state/properties/WoodType;)Lnet/minecraft/client/resources/model/Material;",
            at = @At("HEAD"), cancellable = true)
    private static void buildscape$namespacedSignMaterial(WoodType woodType, CallbackInfoReturnable<Material> cir) {
        if (woodType.name().indexOf(':') < 0) return;
        ResourceLocation wood = new ResourceLocation(woodType.name());
        cir.setReturnValue(new Material(Sheets.SIGN_SHEET,
                new ResourceLocation(wood.getNamespace(), "entity/signs/" + wood.getPath())));
    }
}
