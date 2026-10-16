package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Mixin(targets = "net.minecraft.client.renderer.block.model.BlockModel")
public abstract class BuildscapeBlockModelMixin {
    @Shadow
    public String name;

    @Unique
    private volatile Collection<?> buildscape$materials;

    @Dynamic
    @Inject(method = "getMaterials", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$getCachedMaterials(
            Function<?, ?> modelGetter,
            Set<Pair<String, String>> missingTextureErrors,
            CallbackInfoReturnable<Collection<?>> callback
    ) {
        Collection<?> materials = buildscape$materials;
        if (materials != null && buildscape$canCacheMaterials()) {
            callback.setReturnValue(materials);
        }
    }

    @Dynamic
    @Inject(method = "getMaterials", at = @At("RETURN"), require = 0)
    private void buildscape$cacheMaterials(
            Function<?, ?> modelGetter,
            Set<Pair<String, String>> missingTextureErrors,
            CallbackInfoReturnable<Collection<?>> callback
    ) {
        if (buildscape$materials == null && buildscape$canCacheMaterials()) {
            if (callback.getReturnValue() != null) {
                buildscape$materials = List.copyOf(callback.getReturnValue());
            }
        }
    }

    @Unique
    private boolean buildscape$canCacheMaterials() {
        if (name == null || !name.startsWith("buildscape:")) {
            return false;
        }
        return BuildscapeClientConfig.get().isModelMaterialCacheEnabled();
    }
}
