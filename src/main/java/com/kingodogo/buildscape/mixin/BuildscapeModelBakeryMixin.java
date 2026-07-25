package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.performance.BuildscapeStartupWork;
import com.kingodogo.buildscape.client.performance.LaunchFasterInterop;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Accelerates Buildscape-owned model work while leaving every other namespace
 * on the standard Minecraft and Forge model pipeline.
 *
 * @author hoyin1600p
 */
@Mixin(ModelBakery.class)
public abstract class BuildscapeModelBakeryMixin {
    @Unique
    private static final Pattern BUILDSCAPE_CUSTOM_LOADER =
            Pattern.compile("\"loader\"\\s*:");

    @Shadow
    @Final
    protected ResourceManager resourceManager;

    @Shadow
    @Final
    @Mutable
    private Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache;

    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Shadow
    @Nullable
    public abstract BakedModel bake(ResourceLocation location, ModelState transform);

    @Unique
    private Map<ResourceLocation, BlockModel> buildscape$parsedModels;

    @Inject(method = "processLoading", at = @At("HEAD"), remap = false)
    private void buildscape$parseModelsInParallel(
            ProfilerFiller profiler,
            int maxMipmapLevel,
            CallbackInfo callback
    ) {
        if (
                !BuildscapeClientConfig.get().isParallelModelLoadingEnabled() ||
                LaunchFasterInterop.isParallelModelLoadingEnabled()
        ) {
            return;
        }

        long startedAt = System.nanoTime();
        Collection<ResourceLocation> listedResources = resourceManager.listResources(
                "models",
                path -> path.endsWith(".json")
        );
        List<ResourceLocation> modelFiles = listedResources.stream()
                .filter(location -> BuildScape.MODID.equals(location.getNamespace()))
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();

        ConcurrentMap<ResourceLocation, BlockModel> parsedModels = new ConcurrentHashMap<>();
        AtomicInteger fallbackCount = new AtomicInteger();

        BuildscapeStartupWork.forEachIndex(modelFiles.size(), index -> {
            ResourceLocation fileLocation = modelFiles.get(index);
            try (
                    Resource resource = resourceManager.getResource(fileLocation);
                    InputStream stream = resource.getInputStream()
            ) {
                String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

                // Forge geometry loaders may execute mod code while parsing. Keep those
                // models on Forge's normal sequential path.
                if (BUILDSCAPE_CUSTOM_LOADER.matcher(json).find()) {
                    fallbackCount.incrementAndGet();
                    return;
                }

                ResourceLocation modelLocation = buildscape$toModelLocation(fileLocation);
                BlockModel model = BlockModel.fromString(json);
                model.name = modelLocation.toString();
                parsedModels.put(modelLocation, model);
            } catch (Exception exception) {
                fallbackCount.incrementAndGet();
                BuildScape.LOGGER.debug(
                        "Buildscape model preload deferred {} to the normal loader",
                        fileLocation,
                        exception
                );
            }
        });

        buildscape$parsedModels = parsedModels;
        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        BuildScape.LOGGER.info(
                "Buildscape startup parsed {} model files in parallel ({} ms, {} sequential fallbacks)",
                parsedModels.size(),
                elapsedMillis,
                fallbackCount.get()
        );
    }

    @Inject(method = "loadBlockModel", at = @At("HEAD"), cancellable = true)
    private void buildscape$useParsedModel(
            ResourceLocation location,
            CallbackInfoReturnable<BlockModel> callback
    ) {
        Map<ResourceLocation, BlockModel> parsedModels = buildscape$parsedModels;
        if (parsedModels == null || !BuildScape.MODID.equals(location.getNamespace())) {
            return;
        }

        BlockModel model = parsedModels.get(location);
        if (model != null) {
            callback.setReturnValue(model);
        }
    }

    @Inject(method = "processLoading", at = @At("TAIL"), remap = false)
    private void buildscape$releaseParsedModels(
            ProfilerFiller profiler,
            int maxMipmapLevel,
            CallbackInfo callback
    ) {
        buildscape$parsedModels = null;
    }

    @Inject(
            method = "uploadTextures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;" +
                            "popPush(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void buildscape$bakeModelsInParallel(
            TextureManager textureManager,
            ProfilerFiller profiler,
            CallbackInfoReturnable<AtlasSet> callback
    ) {
        if (
                !BuildscapeClientConfig.get().isParallelModelBakingEnabled() ||
                LaunchFasterInterop.isParallelModelBakingEnabled()
        ) {
            return;
        }

        boolean hasCustomGeometry = unbakedCache.values().stream()
                .filter(BlockModel.class::isInstance)
                .map(BlockModel.class::cast)
                .anyMatch(model ->
                        model.name.startsWith(BuildScape.MODID + ":") &&
                                model.customData.hasCustomGeometry()
                );
        if (hasCustomGeometry) {
            BuildScape.LOGGER.info(
                    "Buildscape startup kept model baking sequential because custom geometry was detected"
            );
            return;
        }

        List<ResourceLocation> candidates = topLevelModels.keySet().stream()
                .filter(location -> BuildScape.MODID.equals(location.getNamespace()))
                .toList();
        if (candidates.isEmpty()) {
            return;
        }

        long startedAt = System.nanoTime();
        bakedCache = new ConcurrentHashMap<>(bakedCache);
        java.util.Set<ResourceLocation> completed = ConcurrentHashMap.newKeySet();
        AtomicInteger fallbackCount = new AtomicInteger();

        BuildscapeStartupWork.forEachIndex(candidates.size(), index -> {
            ResourceLocation location = candidates.get(index);
            try {
                BakedModel model = bake(location, BlockModelRotation.X0_Y0);
                if (model != null) {
                    completed.add(location);
                } else {
                    fallbackCount.incrementAndGet();
                }
            } catch (RuntimeException exception) {
                fallbackCount.incrementAndGet();
                BuildScape.LOGGER.debug(
                        "Buildscape model bake deferred {} to the normal loader",
                        location,
                        exception
                );
            }
        });

        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        BuildScape.LOGGER.info(
                "Buildscape startup baked {} top-level models in parallel ({} ms, {} sequential fallbacks)",
                completed.size(),
                elapsedMillis,
                fallbackCount.get()
        );
    }

    @Unique
    private static ResourceLocation buildscape$toModelLocation(ResourceLocation fileLocation) {
        String path = fileLocation.getPath();
        String modelPath = path.substring("models/".length(), path.length() - ".json".length());
        return new ResourceLocation(fileLocation.getNamespace(), modelPath);
    }
}
