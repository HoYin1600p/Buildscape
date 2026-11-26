package com.kingodogo.buildscape.mixin.fabric.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelLayers.class)
public abstract class ModelLayersMixin {
    @Inject(method = "createSignModelName(Lnet/minecraft/world/level/block/state/properties/WoodType;)Lnet/minecraft/client/model/geom/ModelLayerLocation;",
            at = @At("HEAD"), cancellable = true)
    private static void buildscape$namespacedSignModel(WoodType woodType, CallbackInfoReturnable<ModelLayerLocation> cir) {
        if (woodType.name().indexOf(':') < 0) return;
        ResourceLocation wood = new ResourceLocation(woodType.name());
        cir.setReturnValue(new ModelLayerLocation(
                new ResourceLocation(wood.getNamespace(), "sign/" + wood.getPath()), "main"));
    }
}
