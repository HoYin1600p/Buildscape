package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.resources.bundle.adapter.ResourcePackPrecedenceTracker;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/client/resources/model/ModelBakery;", at = @At("HEAD"))
    private void buildscape$initPrecedence(
            ResourceManager resourceManager,
            ProfilerFiller profiler,
            CallbackInfoReturnable<ModelBakery> cir) {
        BundledResourceRegistry.getInstance().recordReloadStart();
        ResourcePackPrecedenceTracker.updateOverrides(resourceManager);
    }

    @Inject(method = "apply(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void buildscape$onReloadComplete(
            ModelBakery bakery,
            ResourceManager resourceManager,
            ProfilerFiller profiler,
            CallbackInfo ci) {
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        registry.recordModelBakingComplete();
        registry.logReloadSummary();
        registry.prepareNextReload();
    }
}
