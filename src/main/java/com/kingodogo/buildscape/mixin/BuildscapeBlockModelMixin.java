package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
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

/**
 * Memoizes material resolution for Buildscape model instances during one
 * resource reload. New model objects naturally invalidate the cache.
 *
 * @author hoyin1600p
 */
@Mixin(BlockModel.class)
public abstract class BuildscapeBlockModelMixin {
    @Shadow
    public String name;

    @Unique
    private volatile Collection<Material> buildscape$materials;

    @Inject(method = "getMaterials", at = @At("HEAD"), cancellable = true)
    private void buildscape$getCachedMaterials(
            Function<ResourceLocation, UnbakedModel> modelGetter,
            Set<Pair<String, String>> missingTextureErrors,
            CallbackInfoReturnable<Collection<Material>> callback
    ) {
        Collection<Material> materials = buildscape$materials;
        if (materials != null && buildscape$canCacheMaterials()) {
            callback.setReturnValue(materials);
        }
    }

    @Inject(method = "getMaterials", at = @At("RETURN"))
    private void buildscape$cacheMaterials(
            Function<ResourceLocation, UnbakedModel> modelGetter,
            Set<Pair<String, String>> missingTextureErrors,
            CallbackInfoReturnable<Collection<Material>> callback
    ) {
        if (buildscape$materials == null && buildscape$canCacheMaterials()) {
            buildscape$materials = List.copyOf(callback.getReturnValue());
        }
    }

    @Unique
    private boolean buildscape$canCacheMaterials() {
        if (name == null || !name.startsWith(BuildScape.MODID + ":")) {
            return false;
        }
        if (!BuildscapeClientConfig.get().isModelMaterialCacheEnabled()) {
            return false;
        }

        BlockModel model = (BlockModel) (Object) this;
        return !model.customData.hasCustomGeometry();
    }
}
