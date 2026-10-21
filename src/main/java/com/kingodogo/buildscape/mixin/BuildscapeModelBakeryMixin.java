package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.performance.BuildscapeStartupWork;
import com.kingodogo.buildscape.client.performance.LaunchFasterInterop;
import com.kingodogo.buildscape.client.performance.VhaModelBakeryIntegration;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.resources.bundle.adapter.BlockStateAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ResourcePackPrecedenceTracker;
import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;

// Above VHA's priority (500) so this getResources redirect replaces VHA's and forwards to it through its model API.
@Mixin(value = ModelBakery.class, priority = 1100)
public abstract class BuildscapeModelBakeryMixin {
    @Unique
    private static final Pattern BUILDSCAPE_CUSTOM_LOADER = Pattern.compile("\"loader\"\\s*:");

    @Unique
    private static final ThreadLocal<ResourceLocation> BUILDSCAPE$CURRENT_LOADING_LOCATION = new ThreadLocal<>();

    @Shadow
    @Final
    protected ResourceManager resourceManager;

    @Shadow
    @Final
    private static Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;

    @Shadow
    @Final
    @Mutable
    private Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache;

    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow
    @Nullable
    public abstract BakedModel bake(ResourceLocation location, ModelState transform);

    @Unique
    private Map<ResourceLocation, BlockModel> buildscape$parsedModels;

    @Unique
    private volatile boolean buildscape$hasCustomGeometry;

    @Inject(method = "processLoading", at = @At("HEAD"), remap = false)
    private void buildscape$parseModelsInParallel(
            ProfilerFiller profiler,
            int maxMipmapLevel,
            CallbackInfo callback) {
        if (!BuildscapeClientConfig.get().isParallelModelLoadingEnabled() ||
                LaunchFasterInterop.isParallelModelLoadingEnabled()) {
            return;
        }

        long startedAt = System.nanoTime();
        List<ResourceLocation> modelFiles = buildscape$listBundledModels();
        if (modelFiles.isEmpty()) {
            return;
        }

        ConcurrentMap<ResourceLocation, BlockModel> parsedModels = new ConcurrentHashMap<>();
        AtomicInteger fallbackCount = new AtomicInteger();
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();

        BuildscapeStartupWork.forEachIndex(modelFiles.size(), index -> {
            ResourceLocation fileLocation = modelFiles.get(index);
            ResourceLocation modelLocation = buildscape$toModelLocation(fileLocation);
            if (registry.isModelBundled(modelLocation.toString())) {
                // Already served through high-performance binary cache; skip redundant loose JSON parse
                return;
            }

            try (
                    Resource resource = resourceManager.getResource(fileLocation);
                    InputStream stream = resource.getInputStream()) {
                String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

                if (BUILDSCAPE_CUSTOM_LOADER.matcher(json).find()) {
                    fallbackCount.incrementAndGet();
                    return;
                }

                BlockModel model = BlockModel.fromString(json);
                model.name = modelLocation.toString();
                parsedModels.put(modelLocation, model);
            } catch (Exception exception) {
                // an unreadable or unparsable model falls back to the normal loader
                fallbackCount.incrementAndGet();
                BuildScape.LOGGER.debug(
                        "Buildscape model preload deferred {} to the normal loader",
                        fileLocation,
                        exception);
            }
        });

        buildscape$parsedModels = parsedModels;
        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        if (!parsedModels.isEmpty()) {
            BuildScape.LOGGER.info(
                    "Buildscape startup parsed {} unbundled model files in parallel ({} ms, {} sequential fallbacks)",
                    parsedModels.size(),
                    elapsedMillis,
                    fallbackCount.get());
        }
    }

    @Inject(method = "loadBlockModel", at = @At("HEAD"), cancellable = true)
    private void buildscape$loadBlockModel(
            ResourceLocation location,
            CallbackInfoReturnable<BlockModel> callback) {
        BlockModel integrated = VhaModelBakeryIntegration.model(this, location);
        if (integrated != null) {
            callback.setReturnValue(integrated);
            return;
        }
        ResourcePackPrecedenceTracker.ensureInitialized(this.resourceManager);
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        String id = location.toString();
        if (registry.hasModel(id)) {
            ModelIR ir = registry.getModel(id);
            if (ir != null) {
                long start = System.nanoTime();
                BlockModel model = ModelAdapter.toBlockModel(ir);
                registry.recordNativeConversionNanos(System.nanoTime() - start);
                if (model != null) {
                    model.name = id;
                    callback.setReturnValue(model);
                    return;
                }
            }
        }

        Map<ResourceLocation, BlockModel> parsedModels = buildscape$parsedModels;
        if (parsedModels != null && BuildScape.MODID.equals(location.getNamespace())) {
            BlockModel model = parsedModels.get(location);
            if (model != null) {
                callback.setReturnValue(model);
            }
        }
    }

    @Inject(method = "loadBlockModel", at = @At("RETURN"))
    private void buildscape$detectCustomGeometry(
            ResourceLocation location,
            CallbackInfoReturnable<BlockModel> callback) {
        if (BuildScape.MODID.equals(location.getNamespace()) &&
                callback.getReturnValue() != null &&
                callback.getReturnValue().customData.hasCustomGeometry()) {
            buildscape$hasCustomGeometry = true;
        }
    }

    @Inject(method = "loadModel", at = @At("HEAD"))
    private void buildscape$beforeLoadModel(ResourceLocation location, CallbackInfo ci) {
        BUILDSCAPE$CURRENT_LOADING_LOCATION.set(location);
    }

    @Inject(method = "loadModel", at = @At("RETURN"))
    private void buildscape$afterLoadModel(ResourceLocation location, CallbackInfo ci) {
        BUILDSCAPE$CURRENT_LOADING_LOCATION.remove();
    }

    @Redirect(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceManager;getResources(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/List;"))
    private List<Resource> buildscape$suppressBlockStateNotFound(ResourceManager manager, ResourceLocation fileLoc)
            throws IOException {
        List<Resource> integrated = VhaModelBakeryIntegration.resources(this, manager, fileLoc);
        if (integrated != null) {
            return integrated;
        }
        String path = fileLoc.getPath();
        if (path.startsWith("blockstates/") && path.endsWith(".json")) {
            String stateId = fileLoc.getNamespace() + ":"
                    + path.substring("blockstates/".length(), path.length() - ".json".length());
            BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
            if (registry.hasBlockState(stateId)) {
                if (!ResourcePackPrecedenceTracker.isResourceOverridden(manager, fileLoc)) {
                    return Collections.emptyList();
                }
            }
        }
        try {
            return manager.getResources(fileLoc);
        } catch (IOException e) {
            if (path.startsWith("blockstates/") && path.endsWith(".json")) {
                String stateId = fileLoc.getNamespace() + ":"
                        + path.substring("blockstates/".length(), path.length() - ".json".length());
                if (BundledResourceRegistry.getInstance().isBlockStateBundled(stateId)) {
                    return Collections.emptyList();
                }
            }
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    @Redirect(method = "loadModel", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;", remap = false))
    private Object buildscape$insertBundledBlockModelDefinition(
            Stream<?> stream,
            Collector<?, ?, ?> collector) {
        ResourcePackPrecedenceTracker.ensureInitialized(this.resourceManager);
        List<Pair<String, BlockModelDefinition>> list = new ArrayList<>(
                (List<Pair<String, BlockModelDefinition>>) ((Stream<Pair<String, BlockModelDefinition>>) stream)
                        .collect(
                                (Collector<Pair<String, BlockModelDefinition>, ?, List<Pair<String, BlockModelDefinition>>>) collector));

        ResourceLocation location = BUILDSCAPE$CURRENT_LOADING_LOCATION.get();
        if (location != null && VhaModelBakeryIntegration.hasBlockState(this, location)) {
            return list;
        }
        if (location != null) {
            String namespace = location.getNamespace();
            String path = location.getPath();
            String blockStateId = namespace + ":" + path;

            BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
            if (registry.hasBlockState(blockStateId)) {
                BlockStateIR ir = registry.getBlockState(blockStateId);
                if (ir != null) {
                    long start = System.nanoTime();
                    ResourceLocation blockLoc = new ResourceLocation(namespace, path);
                    StateDefinition<Block, BlockState> stateDefinition = STATIC_DEFINITIONS != null
                            ? STATIC_DEFINITIONS.get(blockLoc)
                            : null;
                    if (stateDefinition == null) {
                        Block block = Registry.BLOCK.get(blockLoc);
                        if (block != null) {
                            stateDefinition = block.getStateDefinition();
                        }
                    }

                    BlockModelDefinition bundledDef = BlockStateAdapter.toBlockModelDefinition(ir, stateDefinition);
                    registry.recordNativeConversionNanos(System.nanoTime() - start);

                    if (bundledDef != null) {
                        List<PackResources> packs = this.resourceManager.listPacks().toList();
                        int bundlePackIndex = ResourcePackPrecedenceTracker.findClientBundlePackIndex(packs);
                        if (bundlePackIndex >= 0) {
                            String packName = packs.get(bundlePackIndex).getName();
                            int insertIdx = 0;
                            for (Pair<String, BlockModelDefinition> p : list) {
                                int pIdx = ResourcePackPrecedenceTracker.findPackIndexByName(packs, p.getFirst());
                                if (pIdx < bundlePackIndex) {
                                    insertIdx++;
                                } else {
                                    break;
                                }
                            }
                            list.add(insertIdx, Pair.of(packName, bundledDef));
                        }
                    }
                }
            }
        }

        return list;
    }

    @Inject(method = "processLoading", at = @At("TAIL"), remap = false)
    private void buildscape$releaseParsedModels(
            ProfilerFiller profiler,
            int maxMipmapLevel,
            CallbackInfo callback) {
        buildscape$parsedModels = null;
    }

    @Inject(method = "uploadTextures", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;"
            +
            "popPush(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void buildscape$bakeModelsInParallel(
            TextureManager textureManager,
            ProfilerFiller profiler,
            CallbackInfoReturnable<AtlasSet> callback) {
        if (!BuildscapeClientConfig.get().isParallelModelBakingEnabled() ||
                LaunchFasterInterop.isParallelModelBakingEnabled()) {
            return;
        }

        if (buildscape$hasCustomGeometry) {
            BuildScape.LOGGER.info(
                    "Buildscape startup kept model baking sequential because custom geometry was detected");
            return;
        }

        List<ResourceLocation> candidates = topLevelModels.keySet().stream()
                .filter(location -> BuildScape.MODID.equals(location.getNamespace()))
                .toList();
        if (candidates.isEmpty()) {
            return;
        }

        Set<ResourceLocation> checked = new HashSet<>();
        for (ResourceLocation candidate : candidates) {
            if (!buildscape$hasCachedDependencies(candidate, checked)) {
                BuildScape.LOGGER.info(
                        "Buildscape startup kept model baking sequential because model dependencies were not safely cached");
                return;
            }
        }

        long startedAt = System.nanoTime();
        bakedCache = new ConcurrentHashMap<>(bakedCache);
        Set<ResourceLocation> completed = ConcurrentHashMap.newKeySet();
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
                        exception);
            }
        });

        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        BuildScape.LOGGER.info(
                "Buildscape startup baked {} top-level models in parallel ({} ms, {} sequential fallbacks)",
                completed.size(),
                elapsedMillis,
                fallbackCount.get());
    }

    @Unique
    private boolean buildscape$hasCachedDependencies(ResourceLocation location, Set<ResourceLocation> checked) {
        if (!checked.add(location)) {
            return true;
        }
        UnbakedModel model = unbakedCache.get(location);
        if (!(model instanceof BlockModel blockModel) || blockModel.customData.hasCustomGeometry()) {
            return false;
        }
        for (ResourceLocation dependency : model.getDependencies()) {
            if (!buildscape$hasCachedDependencies(dependency, checked)) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static ResourceLocation buildscape$toModelLocation(ResourceLocation fileLocation) {
        String path = fileLocation.getPath();
        String modelPath = path.substring("models/".length(), path.length() - ".json".length());
        return new ResourceLocation(fileLocation.getNamespace(), modelPath);
    }

    @Unique
    private static List<ResourceLocation> buildscape$listBundledModels() {
        Path modelRoot = ModList.get()
                .getModFileById(BuildScape.MODID)
                .getFile()
                .findResource("assets", BuildScape.MODID, "models");
        if (!Files.isDirectory(modelRoot)) {
            return List.of();
        }

        try (var paths = Files.walk(modelRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(modelRoot::relativize)
                    .map(Path::toString)
                    .filter(path -> path.endsWith(".json"))
                    .map(path -> new ResourceLocation(
                            BuildScape.MODID,
                            "models/" + path.replace('\\', '/')))
                    .sorted(Comparator.comparing(ResourceLocation::toString))
                    .toList();
        } catch (Exception exception) {
            // directory walk or id parsing failed; fall back to the normal loader
            BuildScape.LOGGER.warn(
                    "Buildscape model discovery fell back to the normal loader",
                    exception);
            return List.of();
        }
    }
}
