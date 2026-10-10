package com.kingodogo.buildscape.mixin;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.resources.model.ModelBakery")
public abstract class BuildscapeModelBakeryMixin {

    @Dynamic
    @Inject(method = "processLoading", at = @At("HEAD"), remap = false, require = 0)
    private void buildscape$parseModelsInParallel(Object profiler, int maxMipmapLevel, CallbackInfo callback) {
        Object rm = net.minecraft.client.Minecraft.getInstance() != null ? net.minecraft.client.Minecraft.getInstance().getResourceManager() : null;
        MixinFactory.onModelBakeryPreload(this, rm);
    }

    @Dynamic
    @Inject(method = "loadBlockModel", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$useParsedModel(@Coerce Object location, CallbackInfoReturnable<Object> callback) {
        Object model = MixinFactory.onModelBakeryLoadModel(location);
        if (model != null) {
            callback.setReturnValue(model);
        }
    }

    @Dynamic
    @Inject(method = "loadBlockModel", at = @At("RETURN"), require = 0)
    private void buildscape$detectCustomGeometry(@Coerce Object location, CallbackInfoReturnable<?> callback) {
        MixinFactory.onModelBakeryDetectCustomGeometry(location, callback.getReturnValue());
    }

    @Dynamic
    @Inject(method = "processLoading", at = @At("TAIL"), remap = false, require = 0)
    private void buildscape$releaseParsedModels(Object profiler, int maxMipmapLevel, CallbackInfo callback) {
        MixinFactory.onModelBakeryRelease();
    }

    @Dynamic
    @Inject(
            method = "uploadTextures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            ),
            require = 0
    )
    private void buildscape$bakeModelsInParallel(Object textureManager, Object profiler, CallbackInfoReturnable<?> callback) {
        MixinFactory.onModelBakeryBakeParallel(this);
    }
}
