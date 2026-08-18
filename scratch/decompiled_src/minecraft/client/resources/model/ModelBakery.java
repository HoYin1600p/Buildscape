package net.minecraft.client.resources.model;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.slf4j.Logger;

public class ModelBakery {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final SpriteId FIRE_0 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_0");
   public static final SpriteId FIRE_1 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_1");
   private static final int DESTROY_STAGE_COUNT = 10;
   private static final List DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((i) -> Identifier.withDefaultNamespace("block/destroy_stage_" + i)).collect(Collectors.toList());
   private static final List DESTROY_STAGES_OIT = (List)IntStream.range(0, 10).mapToObj((i) -> Identifier.withDefaultNamespace("block/destroy_oit_stage_" + i)).collect(Collectors.toList());
   private static final List BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((location) -> location.withPath((UnaryOperator)((path) -> "textures/" + path + ".png"))).collect(Collectors.toList());
   private static final List BREAKING_LOCATIONS_OIT = (List)DESTROY_STAGES_OIT.stream().map((location) -> location.withPath((UnaryOperator)((path) -> "textures/" + path + ".png"))).collect(Collectors.toList());
   public static final List DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderTypes::crumbling).collect(Collectors.toList());
   public static final List DESTROY_TYPES_OIT = (List)BREAKING_LOCATIONS_OIT.stream().map(RenderTypes::crumbling).collect(Collectors.toList());
   private static final Matrix4fc IDENTITY = new Matrix4f();
   private final EntityModelSet entityModelSet;
   private final SpriteGetter sprites;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final Map unbakedBlockStateModels;
   private final Map clientInfos;
   private final Map resolvedModels;
   private final ResolvedModel missingModel;

   public ModelBakery(final EntityModelSet entityModelSet, final SpriteGetter sprites, final PlayerSkinRenderCache playerSkinRenderCache, final Map unbakedBlockStateModels, final Map clientInfos, final Map resolvedModels, final ResolvedModel missingModel) {
      this.entityModelSet = entityModelSet;
      this.sprites = sprites;
      this.playerSkinRenderCache = playerSkinRenderCache;
      this.unbakedBlockStateModels = unbakedBlockStateModels;
      this.clientInfos = clientInfos;
      this.resolvedModels = resolvedModels;
      this.missingModel = missingModel;
   }

   public CompletableFuture bakeModels(final MaterialBaker materials, final Executor taskExecutor) {
      ModelBakery.InternerImpl interner = new ModelBakery.InternerImpl();
      ModelBakery.MissingModels missingModels = ModelBakery.MissingModels.bake(this.missingModel, materials, interner);
      ModelBakery.ModelBakerImpl baker = new ModelBakery.ModelBakerImpl(materials, interner, missingModels);
      CompletableFuture bakedBlockStateModelFuture = ParallelMapTransform.schedule(this.unbakedBlockStateModels, (blockState, model) -> {
         try {
            return model.bake(blockState, baker);
         } catch (Exception var4) {
            LOGGER.warn("Unable to bake model: '{}'", blockState, var4);
            return null;
         }
      }, taskExecutor);
      CompletableFuture bakedItemStackModelFuture = ParallelMapTransform.schedule(this.clientInfos, (location, clientInfo) -> {
         try {
            return clientInfo.model().bake(new ItemModel.BakingContext(baker, this.entityModelSet, this.sprites, this.playerSkinRenderCache, missingModels.item, clientInfo.registrySwapper()), IDENTITY);
         } catch (Exception var6) {
            LOGGER.warn("Unable to bake item model: '{}'", location, var6);
            return null;
         }
      }, taskExecutor);
      Map itemStackModelProperties = new HashMap(this.clientInfos.size());
      this.clientInfos.forEach((id, clientInfo) -> {
         ClientItem.Properties properties = clientInfo.properties();
         if (!properties.equals(ClientItem.Properties.DEFAULT)) {
            itemStackModelProperties.put(id, properties);
         }

      });
      return bakedBlockStateModelFuture.thenCombine(bakedItemStackModelFuture, (bakedBlockStateModels, bakedItemStateModels) -> new ModelBakery.BakingResult(missingModels, bakedBlockStateModels, bakedItemStateModels, itemStackModelProperties));
   }

   public static record BakingResult(ModelBakery.MissingModels missingModels, Map blockStateModels, Map itemStackModels, Map itemProperties) {
      public BlockStateModel getBlockStateModel(final BlockState blockState) {
         return (BlockStateModel)this.blockStateModels.getOrDefault(blockState, this.missingModels.block);
      }
   }

   private static class InternerImpl implements ModelBaker.Interner {
      private final Interner vectors = Interners.newStrongInterner();
      private final Interner materialInfos = Interners.newStrongInterner();

      public Vector3fc vector(final Vector3fc v) {
         return (Vector3fc)this.vectors.intern(v);
      }

      public BakedQuad.MaterialInfo materialInfo(final BakedQuad.MaterialInfo material) {
         return (BakedQuad.MaterialInfo)this.materialInfos.intern(material);
      }
   }

   public static record MissingModels(BlockStateModelPart blockPart, BlockStateModel block, MissingItemModel item, FluidModel fluid) {
      public static ModelBakery.MissingModels bake(final ResolvedModel unbaked, final MaterialBaker materials, final ModelBaker.Interner interner) {
         ModelBaker missingModelBakery = new ModelBaker() {
            public ResolvedModel getModel(final Identifier location) {
               throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(location));
            }

            public BlockStateModelPart missingBlockModelPart() {
               throw new IllegalStateException();
            }

            public Object compute(final ModelBaker.SharedOperationKey key) {
               return key.compute(this);
            }

            public MaterialBaker materials() {
               return materials;
            }

            public ModelBaker.Interner interner() {
               return interner;
            }
         };
         TextureSlots textureSlots = unbaked.getTopTextureSlots();
         boolean hasAmbientOcclusion = unbaked.getTopAmbientOcclusion();
         boolean usesBlockLight = unbaked.getTopGuiLight().lightLikeBlock();
         ItemTransforms transforms = unbaked.getTopTransforms();
         QuadCollection geometry = unbaked.bakeTopGeometry(textureSlots, missingModelBakery, BlockModelRotation.IDENTITY);
         Multimap forbiddenSprites = SimpleModelWrapper.findNonBlockSprites(geometry);
         if (forbiddenSprites != null) {
            throw new IllegalStateException("Missing block contains sprites from outside of block atlas: " + String.valueOf(forbiddenSprites));
         } else {
            Material.Baked particleMaterial = unbaked.resolveParticleMaterial(textureSlots, missingModelBakery);
            SimpleModelWrapper missingModelPart = new SimpleModelWrapper(geometry, hasAmbientOcclusion, particleMaterial);
            BlockStateModel bakedBlockModel = new SingleVariant(missingModelPart);
            MissingItemModel bakedItemModel = new MissingItemModel(geometry.getAll(), new ModelRenderProperties(usesBlockLight, particleMaterial, transforms));
            FluidModel bakedFluidModel = new FluidModel(ChunkSectionLayer.SOLID, particleMaterial, particleMaterial, (Material.Baked)null, (BlockTintSource)null);
            return new ModelBakery.MissingModels(missingModelPart, bakedBlockModel, bakedItemModel, bakedFluidModel);
         }
      }
   }

   private class ModelBakerImpl implements ModelBaker {
      private final MaterialBaker materials;
      private final ModelBaker.Interner interner;
      private final ModelBakery.MissingModels missingModels;
      private final Map operationCache;
      private final Function cacheComputeFunction;

      private ModelBakerImpl(final MaterialBaker materials, final ModelBaker.Interner interner, final ModelBakery.MissingModels missingModels) {
         Objects.requireNonNull(ModelBakery.this);
         super();
         this.operationCache = new ConcurrentHashMap();
         this.cacheComputeFunction = (k) -> k.compute(this);
         this.materials = materials;
         this.interner = interner;
         this.missingModels = missingModels;
      }

      public BlockStateModelPart missingBlockModelPart() {
         return this.missingModels.blockPart;
      }

      public MaterialBaker materials() {
         return this.materials;
      }

      public ModelBaker.Interner interner() {
         return this.interner;
      }

      public ResolvedModel getModel(final Identifier location) {
         ResolvedModel result = (ResolvedModel)ModelBakery.this.resolvedModels.get(location);
         if (result == null) {
            ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", location);
            return ModelBakery.this.missingModel;
         } else {
            return result;
         }
      }

      public Object compute(final ModelBaker.SharedOperationKey key) {
         return this.operationCache.computeIfAbsent(key, this.cacheComputeFunction);
      }
   }
}
