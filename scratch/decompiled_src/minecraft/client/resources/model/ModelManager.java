package net.minecraft.client.resources.model;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.BlockModelSet;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.block.LoadedBlockModels;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ModelManager implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
   private Map bakedItemStackModels = Map.of();
   private Map itemProperties = Map.of();
   private final AtlasManager atlasManager;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final BlockColors blockColors;
   private EntityModelSet entityModelSet = EntityModelSet.EMPTY;
   private ModelBakery.MissingModels missingModels;
   private @Nullable BlockStateModelSet blockStateModelSet;
   private @Nullable BlockModelSet blockModelSet;
   private @Nullable FluidStateModelSet fluidStateModelSet;
   private Object2IntMap modelGroups = Object2IntMaps.emptyMap();

   public ModelManager(final BlockColors blockColors, final AtlasManager atlasManager, final PlayerSkinRenderCache playerSkinRenderCache) {
      this.blockColors = blockColors;
      this.atlasManager = atlasManager;
      this.playerSkinRenderCache = playerSkinRenderCache;
   }

   public ItemModel getItemModel(final Identifier id) {
      return (ItemModel)this.bakedItemStackModels.getOrDefault(id, this.missingModels.item());
   }

   public ClientItem.Properties getItemProperties(final Identifier id) {
      return (ClientItem.Properties)this.itemProperties.getOrDefault(id, ClientItem.Properties.DEFAULT);
   }

   public BlockStateModelSet getBlockStateModelSet() {
      return (BlockStateModelSet)Objects.requireNonNull(this.blockStateModelSet, "Block models not yet initialized");
   }

   public BlockModelSet getBlockModelSet() {
      return (BlockModelSet)Objects.requireNonNull(this.blockModelSet, "Block models not yet initialized");
   }

   public FluidStateModelSet getFluidStateModelSet() {
      return (FluidStateModelSet)Objects.requireNonNull(this.fluidStateModelSet, "Fluid models not yet initialized");
   }

   public final CompletableFuture reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      CompletableFuture entityModelSet = CompletableFuture.supplyAsync(EntityModelSet::vanilla, taskExecutor);
      CompletableFuture modelCache = loadBlockModels(manager, taskExecutor);
      CompletableFuture blockStateModels = BlockStateModelLoader.loadBlockStates(manager, taskExecutor);
      CompletableFuture blockModelContents = CompletableFuture.supplyAsync(() -> BuiltInBlockModels.createBlockModels(this.blockColors), taskExecutor);
      CompletableFuture itemStackModels = ClientItemInfoLoader.scheduleLoad(manager, taskExecutor);
      CompletableFuture modelDiscovery = CompletableFuture.allOf(modelCache, blockStateModels, itemStackModels).thenApplyAsync((var3) -> discoverModelDependencies((Map)modelCache.join(), (BlockStateModelLoader.LoadedModels)blockStateModels.join(), (ClientItemInfoLoader.LoadedClientInfos)itemStackModels.join()), taskExecutor);
      CompletableFuture modelGroups = blockStateModels.thenApplyAsync((models) -> buildModelGroups(this.blockColors, models), taskExecutor);
      AtlasManager.PendingStitchResults pendingStitches = (AtlasManager.PendingStitchResults)currentReload.get(AtlasManager.PENDING_STITCH);
      CompletableFuture pendingBlockAtlasSprites = pendingStitches.get(AtlasIds.BLOCKS);
      CompletableFuture pendingItemAtlasSprites = pendingStitches.get(AtlasIds.ITEMS);
      CompletableFuture blockModels = CompletableFuture.allOf(blockModelContents, entityModelSet).thenApply((var3) -> new LoadedBlockModels((Map)blockModelContents.join(), (EntityModelSet)entityModelSet.join(), this.atlasManager, this.playerSkinRenderCache));
      return CompletableFuture.allOf(pendingBlockAtlasSprites, pendingItemAtlasSprites, modelDiscovery, modelGroups, blockStateModels, itemStackModels, entityModelSet, blockModels, modelCache).thenComposeAsync((var11) -> {
         SpriteLoader.Preparations blockAtlasSprites = (SpriteLoader.Preparations)pendingBlockAtlasSprites.join();
         SpriteLoader.Preparations itemAtlasSprites = (SpriteLoader.Preparations)pendingItemAtlasSprites.join();
         ModelManager.ResolvedModels resolvedModels = (ModelManager.ResolvedModels)modelDiscovery.join();
         Object2IntMap groups = (Object2IntMap)modelGroups.join();
         Set unreferencedModels = Sets.difference(((Map)modelCache.join()).keySet(), resolvedModels.models.keySet());
         if (!unreferencedModels.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", unreferencedModels.stream().sorted().map((modelId) -> "\t" + String.valueOf(modelId) + "\n").collect(Collectors.joining()));
         }

         ModelBakery bakery = new ModelBakery((EntityModelSet)entityModelSet.join(), this.atlasManager, this.playerSkinRenderCache, ((BlockStateModelLoader.LoadedModels)blockStateModels.join()).models(), ((ClientItemInfoLoader.LoadedClientInfos)itemStackModels.join()).contents(), resolvedModels.models(), resolvedModels.missing());
         return loadModels(blockAtlasSprites, itemAtlasSprites, bakery, (LoadedBlockModels)blockModels.join(), groups, (EntityModelSet)entityModelSet.join(), taskExecutor);
      }, taskExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(this::apply, reloadExecutor);
   }

   private static CompletableFuture loadBlockModels(final ResourceManager manager, final Executor executor) {
      return CompletableFuture.supplyAsync(() -> MODEL_LISTER.listMatchingResources(manager), executor).thenCompose((resources) -> {
         List result = new ArrayList(resources.size());

         for(Map.Entry resource : resources.entrySet()) {
            result.add(CompletableFuture.supplyAsync(() -> {
               Identifier modelId = MODEL_LISTER.fileToId((Identifier)resource.getKey());

               try {
                  Reader reader = ((Resource)resource.getValue()).openAsReader();

                  Pair t$;
                  try {
                     t$ = Pair.of(modelId, CuboidModel.fromStream(reader));
                  } catch (Throwable var6) {
                     if (reader != null) {
                        try {
                           reader.close();
                        } catch (Throwable var5) {
                           var6.addSuppressed(var5);
                        }
                     }

                     throw var6;
                  }

                  if (reader != null) {
                     reader.close();
                  }

                  return t$;
               } catch (Exception var7) {
                  LOGGER.error("Failed to load model {}", resource.getKey(), var7);
                  return null;
               }
            }, executor));
         }

         return Util.sequence(result).thenApply((pairs) -> (Map)pairs.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
      });
   }

   private static ModelManager.ResolvedModels discoverModelDependencies(final Map allModels, final BlockStateModelLoader.LoadedModels blockStateModels, final ClientItemInfoLoader.LoadedClientInfos itemInfos) {
      try (Zone ignored = Profiler.get().zone("dependencies")) {
         ModelDiscovery result = new ModelDiscovery(allModels, MissingCuboidModel.missingModel());
         result.addSpecialModel(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());
         blockStateModels.models().values().forEach(result::addRoot);
         itemInfos.contents().values().forEach((info) -> result.addRoot(info.model()));
         return new ModelManager.ResolvedModels(result.missingModel(), result.resolve());
      }
   }

   private static CompletableFuture loadModels(final SpriteLoader.Preparations blockAtlas, final SpriteLoader.Preparations itemAtlas, final ModelBakery bakery, final LoadedBlockModels blockModels, final Object2IntMap modelGroups, final EntityModelSet entityModelSet, final Executor taskExecutor) {
      MaterialBaker materialBaker = new MaterialBaker(blockAtlas, itemAtlas);
      CompletableFuture bakedStateResults = bakery.bakeModels(materialBaker, taskExecutor);
      CompletableFuture bakedModelsFuture = bakedStateResults.thenCompose((bakingResult) -> blockModels.bake(bakingResult::getBlockStateModel, bakingResult.missingModels().block(), taskExecutor));
      return bakedStateResults.thenCombine(bakedModelsFuture, (bakingResult, bakedModels) -> {
         Map fluidModels = FluidStateModelSet.bake(materialBaker);
         materialBaker.logMissingTextures();
         Map modelByStateCache = createBlockStateToModelDispatch(bakingResult.blockStateModels(), bakingResult.missingModels().block());
         return new ModelManager.ReloadState(bakingResult, modelGroups, modelByStateCache, bakedModels, fluidModels, entityModelSet);
      });
   }

   private static Map createBlockStateToModelDispatch(final Map bakedModels, final BlockStateModel missingModel) {
      try (Zone ignored = Profiler.get().zone("block state dispatch")) {
         Map modelByStateCache = new IdentityHashMap(bakedModels);

         for(Block block : BuiltInRegistries.BLOCK) {
            block.getStateDefinition().getPossibleStates().forEach((state) -> {
               if (bakedModels.putIfAbsent(state, missingModel) == null) {
                  LOGGER.warn("Missing model for variant: '{}'", state);
               }

            });
         }

         return modelByStateCache;
      }
   }

   private static Object2IntMap buildModelGroups(final BlockColors blockColors, final BlockStateModelLoader.LoadedModels blockStateModels) {
      try (Zone ignored = Profiler.get().zone("block groups")) {
         return ModelGroupCollector.build(blockColors, blockStateModels);
      }
   }

   private void apply(final ModelManager.ReloadState preparations) {
      ModelBakery.BakingResult bakedModels = preparations.bakedModels;
      this.bakedItemStackModels = bakedModels.itemStackModels();
      this.itemProperties = bakedModels.itemProperties();
      this.modelGroups = preparations.modelGroups;
      this.missingModels = bakedModels.missingModels();
      this.blockStateModelSet = new BlockStateModelSet(preparations.blockStateModels, this.missingModels.block());
      this.blockModelSet = new BlockModelSet(this.blockStateModelSet, preparations.blockModels, this.blockColors);
      this.fluidStateModelSet = new FluidStateModelSet(preparations.fluidModels, this.missingModels.fluid());
      this.entityModelSet = preparations.entityModelSet;
   }

   public boolean requiresRender(final BlockState oldState, final BlockState newState) {
      if (oldState == newState) {
         return false;
      } else {
         int oldModelGroup = this.modelGroups.getInt(oldState);
         if (oldModelGroup != -1) {
            int newModelGroup = this.modelGroups.getInt(newState);
            if (oldModelGroup == newModelGroup) {
               FluidState oldFluidState = oldState.getFluidState();
               FluidState newFluidState = newState.getFluidState();
               return oldFluidState != newFluidState;
            }
         }

         return true;
      }
   }

   public Supplier entityModels() {
      return () -> this.entityModelSet;
   }

   private static record ReloadState(ModelBakery.BakingResult bakedModels, Object2IntMap modelGroups, Map blockStateModels, Map blockModels, Map fluidModels, EntityModelSet entityModelSet) {
   }

   private static record ResolvedModels(ResolvedModel missing, Map models) {
   }
}
