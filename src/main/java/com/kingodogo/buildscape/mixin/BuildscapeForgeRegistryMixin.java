package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.performance.BuildscapeBlockStateCacheCoordinator;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class BuildscapeForgeRegistryMixin {
    @Unique
    private static final ResourceLocation BUILDSCAPE_BLOCK_REGISTRY =
            new ResourceLocation("minecraft", "block");

    @Shadow
    private ResourceLocation name;

    @Inject(method = "bake", at = @At("HEAD"))
    private void buildscape$beginBlockStateCaches(CallbackInfo callback) {
        if (
                BUILDSCAPE_BLOCK_REGISTRY.equals(name) &&
                        BuildscapeClientConfig.get().isParallelBlockStateCacheEnabled()
        ) {
            BuildscapeBlockStateCacheCoordinator.begin();
        }
    }

    @Inject(method = "bake", at = @At("TAIL"))
    private void buildscape$finishBlockStateCaches(CallbackInfo callback) {
        if (
                BUILDSCAPE_BLOCK_REGISTRY.equals(name) &&
                        BuildscapeBlockStateCacheCoordinator.isCollecting()
        ) {
            BuildscapeBlockStateCacheCoordinator.finish();
        }
    }
}
