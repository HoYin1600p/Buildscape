package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.client.model.TintedLeafHedgeModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Inject(method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("RETURN"), cancellable = true)
    private void buildscape$tintHedges(ResourceLocation id, ModelState state, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel model = cir.getReturnValue();
        if (model == null || model instanceof TintedLeafHedgeModel || !BuildscapeCommon.MOD_ID.equals(id.getNamespace())) return;
        String path = id.getPath();
        for (String wood : java.util.List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "azalea", "flowering_azalea")) {
            String block = "block/" + wood + "_leaf_hedge_";
            if (path.equals("item/" + wood + "_leaf_hedge") || path.equals(block + "post")
                    || path.equals(block + "side") || path.equals(block + "side_tall") || path.equals(block + "inventory")) {
                cir.setReturnValue(new TintedLeafHedgeModel(model));
                return;
            }
        }
    }
}
