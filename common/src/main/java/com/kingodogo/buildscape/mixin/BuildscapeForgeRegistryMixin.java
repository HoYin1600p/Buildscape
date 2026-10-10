package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.performance.BuildscapeBlockStateCacheCoordinator;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.minecraftforge.registries.ForgeRegistry", remap = false)
public abstract class BuildscapeForgeRegistryMixin {

    @Dynamic
    @Inject(method = "bake", at = @At("HEAD"), require = 0)
    private void buildscape$beginBlockStateCaches(CallbackInfo callback) {
        if (BuildscapeClientConfig.get().isParallelBlockStateCacheEnabled()) {
            BuildscapeBlockStateCacheCoordinator.begin();
        }
    }

    @Dynamic
    @Inject(method = "bake", at = @At("TAIL"), require = 0)
    private void buildscape$finishBlockStateCaches(CallbackInfo callback) {
        if (BuildscapeBlockStateCacheCoordinator.isCollecting()) {
            BuildscapeBlockStateCacheCoordinator.finish();
        }
    }
}
