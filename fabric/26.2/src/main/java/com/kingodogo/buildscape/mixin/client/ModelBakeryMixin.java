package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.adapter.v26x.client.FoliageColors;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    // Fabric API 0.149.0+26.2 ships no model-loading module, so this mirrors NeoForge's
    // ModelEvent.ModifyBakingResult for the foliage item tints.
    @Inject(method = "bakeModels", at = @At("RETURN"), cancellable = true)
    private void buildscape$tintItems(
            CallbackInfoReturnable<CompletableFuture<ModelBakery.BakingResult>> cir) {
        cir.setReturnValue(cir.getReturnValue().thenApply(result -> {
            result.itemStackModels().replaceAll(FoliageColors::tintItem);
            return result;
        }));
    }
}
