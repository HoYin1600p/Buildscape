package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.Nullable;

public class SurfaceRules {
   public static SurfaceRules.RuleSource registerAndWrap(final BootstrapContext context, final ResourceKey key, final SurfaceRules.RuleSource rule) {
      return new SurfaceRules.HolderRuleSource(context.register(key, rule));
   }

   public static SurfaceRules.ConditionSource registerAndWrap(final BootstrapContext context, final ResourceKey key, final SurfaceRules.ConditionSource condition) {
      return new SurfaceRules.HolderConditionSource(context.register(key, condition));
   }

   public static SurfaceRules.RuleSource getRule(final HolderGetter rules, final ResourceKey key) {
      return new SurfaceRules.HolderRuleSource(rules.getOrThrow(key));
   }

   public static SurfaceRules.ConditionSource getCondition(final HolderGetter conditions, final ResourceKey key) {
      return new SurfaceRules.HolderConditionSource(conditions.getOrThrow(key));
   }

   public static SurfaceRules.ConditionSource stoneDepthCheck(final int offset, final boolean addSurfaceDepth1, final CaveSurface surfaceType) {
      return new SurfaceRules.StoneDepthCheck(offset, addSurfaceDepth1, 0, surfaceType);
   }

   public static SurfaceRules.ConditionSource stoneDepthCheck(final int offset, final boolean addSurfaceDepth1, final int secondaryDepthRange, final CaveSurface surfaceType) {
      return new SurfaceRules.StoneDepthCheck(offset, addSurfaceDepth1, secondaryDepthRange, surfaceType);
   }

   public static SurfaceRules.ConditionSource not(final SurfaceRules.ConditionSource target) {
      return new SurfaceRules.NotConditionSource(target);
   }

   public static SurfaceRules.ConditionSource yBlockCheck(final VerticalAnchor anchor, final int surfaceDepthMultiplier) {
      return new SurfaceRules.YConditionSource(anchor, surfaceDepthMultiplier, false);
   }

   public static SurfaceRules.ConditionSource yStartCheck(final VerticalAnchor anchor, final int surfaceDepthMultiplier) {
      return new SurfaceRules.YConditionSource(anchor, surfaceDepthMultiplier, true);
   }

   public static SurfaceRules.ConditionSource waterBlockCheck(final int offset, final int surfaceDepthMultiplier) {
      return new SurfaceRules.WaterConditionSource(offset, surfaceDepthMultiplier, false);
   }

   public static SurfaceRules.ConditionSource waterStartCheck(final int offset, final int surfaceDepthMultiplier) {
      return new SurfaceRules.WaterConditionSource(offset, surfaceDepthMultiplier, true);
   }

   @SafeVarargs
   public static SurfaceRules.ConditionSource isBiome(final HolderGetter biomes, final ResourceKey... target) {
      return new SurfaceRules.BiomeConditionSource(HolderSet.direct(biomes::getOrThrow, target));
   }

   public static SurfaceRules.ConditionSource noiseCondition2d(final ResourceKey noise, final double minRange) {
      return noiseCondition2d(noise, minRange, Double.MAX_VALUE);
   }

   public static SurfaceRules.ConditionSource noiseCondition2d(final ResourceKey noise, final double minRange, final double maxRange) {
      return new SurfaceRules.NoiseThresholdConditionSource(noise, minRange, maxRange, false);
   }

   public static SurfaceRules.ConditionSource noiseCondition3d(final ResourceKey noise, final double minRange) {
      return noiseCondition3d(noise, minRange, Double.MAX_VALUE);
   }

   public static SurfaceRules.ConditionSource noiseCondition3d(final ResourceKey noise, final double minRange, final double maxRange) {
      return new SurfaceRules.NoiseThresholdConditionSource(noise, minRange, maxRange, true);
   }

   public static SurfaceRules.ConditionSource verticalGradient(final String randomName, final VerticalAnchor trueAtAndBelow, final VerticalAnchor falseAtAndAbove) {
      return new SurfaceRules.VerticalGradientConditionSource(Identifier.parse(randomName), trueAtAndBelow, falseAtAndAbove);
   }

   public static SurfaceRules.ConditionSource steep() {
      return SurfaceRules.Steep.INSTANCE;
   }

   public static SurfaceRules.ConditionSource hole() {
      return SurfaceRules.Hole.INSTANCE;
   }

   public static SurfaceRules.ConditionSource abovePreliminarySurface() {
      return SurfaceRules.AbovePreliminarySurface.INSTANCE;
   }

   public static SurfaceRules.ConditionSource temperature() {
      return SurfaceRules.Temperature.INSTANCE;
   }

   public static SurfaceRules.RuleSource ifTrue(final SurfaceRules.ConditionSource condition, final SurfaceRules.RuleSource next) {
      return new SurfaceRules.TestRuleSource(condition, next);
   }

   public static SurfaceRules.RuleSource sequence(final SurfaceRules.RuleSource... rules) {
      if (rules.length == 0) {
         throw new IllegalArgumentException("Need at least 1 rule for a sequence");
      } else {
         return new SurfaceRules.SequenceRuleSource(List.of(rules));
      }
   }

   public static SurfaceRules.RuleSource state(final BlockState state) {
      return new SurfaceRules.BlockRuleSource(state);
   }

   public static SurfaceRules.RuleSource bandlands() {
      return SurfaceRules.Bandlands.INSTANCE;
   }

   private static MapCodec register(final Registry registry, final String name, final MapCodec codec) {
      return (MapCodec)Registry.register(registry, name, codec);
   }

   private static enum AbovePreliminarySurface implements SurfaceRules.ConditionSource {
      INSTANCE;

      private static final MapCodec CODEC = MapCodec.unit(INSTANCE);

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context context) {
         return context.abovePreliminarySurface;
      }

      // $FF: synthetic method
      private static SurfaceRules.AbovePreliminarySurface[] $values() {
         return new SurfaceRules.AbovePreliminarySurface[]{INSTANCE};
      }
   }

   private static enum Bandlands implements SurfaceRules.RuleSource {
      INSTANCE;

      private static final MapCodec CODEC = MapCodec.unit(INSTANCE);

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(final SurfaceRules.Context context) {
         return context.system::getBand;
      }

      // $FF: synthetic method
      private static SurfaceRules.Bandlands[] $values() {
         return new SurfaceRules.Bandlands[]{INSTANCE};
      }
   }

   private static record BiomeConditionSource(HolderSet biomes) implements SurfaceRules.ConditionSource {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.holderSet(Registries.BIOME).fieldOf("biome_is").forGetter(SurfaceRules.BiomeConditionSource::biomes)).apply(i, SurfaceRules.BiomeConditionSource::new));

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context ruleContext) {
         if (ruleContext.possibleBiomes != null) {
            if (this.canNeverMatch(ruleContext.possibleBiomes)) {
               return () -> false;
            }

            if (this.willAlwaysMatch(ruleContext.possibleBiomes)) {
               return () -> true;
            }
         }

         class BiomeCondition extends SurfaceRules.LazyYCondition {
            private BiomeCondition() {
               Objects.requireNonNull(BiomeConditionSource.this);
               super(ruleContext);
            }

            protected boolean compute() {
               return BiomeConditionSource.this.biomes.contains(this.context.getBiome());
            }
         }

         return new BiomeCondition();
      }

      private boolean canNeverMatch(final Set possibleBiomes) {
         for(Holder biome : this.biomes) {
            if (possibleBiomes.contains(biome)) {
               return false;
            }
         }

         return true;
      }

      private boolean willAlwaysMatch(final Set possibleBiomes) {
         for(Holder possibleBiome : possibleBiomes) {
            if (!this.biomes.contains(possibleBiome)) {
               return false;
            }
         }

         return true;
      }

      public String toString() {
         return "BiomeConditionSource[biomes=" + String.valueOf(this.biomes) + "]";
      }
   }

   private static record BlockRuleSource(BlockState resultState, SurfaceRules.StateRule rule) implements SurfaceRules.RuleSource {
      private static final MapCodec CODEC = BlockState.CODEC.xmap(SurfaceRules.BlockRuleSource::new, SurfaceRules.BlockRuleSource::resultState).fieldOf("result_state");

      private BlockRuleSource(final BlockState state) {
         this(state, new SurfaceRules.StateRule(state));
      }

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(final SurfaceRules.Context context) {
         return this.rule;
      }
   }

   private interface Condition {
      boolean test();
   }

   public interface ConditionSource extends Function {
      Codec DIRECT_CODEC = BuiltInRegistries.MATERIAL_CONDITION_TYPE.byNameCodec().dispatch(SurfaceRules.ConditionSource::codec, Function.identity());
      Codec CODEC = RegistryCodecs.holder(Registries.MATERIAL_CONDITION, DIRECT_CODEC).xmap((holder) -> {
         Objects.requireNonNull(holder);
         int index$1 = 0;
         Object var10000;
         switch (holder.typeSwitch<invokedynamic>(holder, index$1)) {
            case 0:
               Holder.Direct direct = (Holder.Direct)holder;
               var10000 = (SurfaceRules.ConditionSource)direct.value();
               break;
            case 1:
               Holder.Reference reference = (Holder.Reference)holder;
               var10000 = new SurfaceRules.HolderConditionSource(reference);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return (SurfaceRules.ConditionSource)var10000;
      }, (value) -> {
         Objects.requireNonNull(value);
         int index$2 = 0;
         Holder var8;
         switch (value.typeSwitch<invokedynamic>(value, index$2)) {
            case 0:
               SurfaceRules.HolderConditionSource $b$0 = (SurfaceRules.HolderConditionSource)value;
               SurfaceRules.HolderConditionSource var10000 = $b$0;

               try {
                  var7 = var10000.holder();
               } catch (Throwable var6) {
                  throw new MatchException(var6.toString(), var6);
               }

               Holder patt3$temp = var7;
               var8 = patt3$temp;
               break;
            default:
               var8 = Holder.direct(value);
         }

         return var8;
      });

      static MapCodec bootstrap(final Registry registry) {
         SurfaceRules.register(registry, "biome", SurfaceRules.BiomeConditionSource.CODEC);
         SurfaceRules.register(registry, "noise_threshold", SurfaceRules.NoiseThresholdConditionSource.CODEC);
         SurfaceRules.register(registry, "vertical_gradient", SurfaceRules.VerticalGradientConditionSource.CODEC);
         SurfaceRules.register(registry, "y_above", SurfaceRules.YConditionSource.CODEC);
         SurfaceRules.register(registry, "water", SurfaceRules.WaterConditionSource.CODEC);
         SurfaceRules.register(registry, "temperature", SurfaceRules.Temperature.CODEC);
         SurfaceRules.register(registry, "steep", SurfaceRules.Steep.CODEC);
         SurfaceRules.register(registry, "not", SurfaceRules.NotConditionSource.CODEC);
         SurfaceRules.register(registry, "hole", SurfaceRules.Hole.CODEC);
         SurfaceRules.register(registry, "above_preliminary_surface", SurfaceRules.AbovePreliminarySurface.CODEC);
         return SurfaceRules.register(registry, "stone_depth", SurfaceRules.StoneDepthCheck.CODEC);
      }

      MapCodec codec();
   }

   protected static final class Context {
      private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
      private static final int SURFACE_CELL_BITS = 4;
      private static final int SURFACE_CELL_SIZE = 16;
      private static final int SURFACE_CELL_MASK = 15;
      private final SurfaceSystem system;
      private final SurfaceRules.Condition temperature = new SurfaceRules.Context.TemperatureHelperCondition(this);
      private final SurfaceRules.Condition steep = new SurfaceRules.Context.SteepMaterialCondition(this);
      private final SurfaceRules.Condition hole = new SurfaceRules.Context.HoleCondition(this);
      private final SurfaceRules.Condition abovePreliminarySurface = new SurfaceRules.Context.AbovePreliminarySurfaceCondition();
      private final RandomState randomState;
      private final ChunkAccess chunk;
      private final NoiseChunk noiseChunk;
      private final Function biomeGetter;
      private final WorldGenerationContext context;
      private final @Nullable Set possibleBiomes;
      private long lastPreliminarySurfaceCellOrigin = Long.MAX_VALUE;
      private final int[] preliminarySurfaceCache = new int[4];
      private final Map noiseSamplers2d = new IdentityHashMap();
      private final Map noiseSamplers3d = new IdentityHashMap();
      private long lastUpdateXZ = -9223372036854775807L;
      private int blockX;
      private int blockZ;
      private int surfaceDepth;
      private long lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
      private double surfaceSecondary;
      private long lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
      private int minSurfaceLevel;
      private long lastUpdateY = -9223372036854775807L;
      private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      private @Nullable Holder biome;
      private int blockY;
      private int waterHeight;
      private int stoneDepthBelow;
      private int stoneDepthAbove;

      protected Context(final SurfaceSystem system, final RandomState randomState, final ChunkAccess chunk, final NoiseChunk noiseChunk, final Function biomeGetter, final WorldGenerationContext context, final @Nullable Set possibleBiomes) {
         this.system = system;
         this.randomState = randomState;
         this.chunk = chunk;
         this.noiseChunk = noiseChunk;
         this.biomeGetter = biomeGetter;
         this.context = context;
         this.possibleBiomes = possibleBiomes;
      }

      protected void updateXZ(final int blockX, final int blockZ) {
         ++this.lastUpdateXZ;
         ++this.lastUpdateY;
         this.blockX = blockX;
         this.blockZ = blockZ;
         this.surfaceDepth = this.system.getSurfaceDepth(blockX, blockZ);
      }

      protected void updateY(final int stoneDepthAbove, final int stoneDepthBelow, final int waterHeight, final int blockY) {
         ++this.lastUpdateY;
         this.biome = null;
         this.blockY = blockY;
         this.waterHeight = waterHeight;
         this.stoneDepthBelow = stoneDepthBelow;
         this.stoneDepthAbove = stoneDepthAbove;
      }

      protected double getSurfaceSecondary() {
         if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
            this.lastSurfaceDepth2Update = this.lastUpdateXZ;
            this.surfaceSecondary = this.system.getSurfaceSecondary(this.blockX, this.blockZ);
         }

         return this.surfaceSecondary;
      }

      protected Holder getBiome() {
         if (this.biome == null) {
            this.biome = (Holder)this.biomeGetter.apply(this.pos.set(this.blockX, this.blockY, this.blockZ));
         }

         return this.biome;
      }

      public int getSeaLevel() {
         return this.system.getSeaLevel();
      }

      private static int blockCoordToSurfaceCell(final int blockCoord) {
         return blockCoord >> 4;
      }

      private static int surfaceCellToBlockCoord(final int cellCoord) {
         return cellCoord << 4;
      }

      protected int getMinSurfaceLevel() {
         if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
            int cornerCellX = blockCoordToSurfaceCell(this.blockX);
            int cornerCellZ = blockCoordToSurfaceCell(this.blockZ);
            long preliminarySurfaceCellOrigin = ChunkPos.pack(cornerCellX, cornerCellZ);
            if (this.lastPreliminarySurfaceCellOrigin != preliminarySurfaceCellOrigin) {
               this.lastPreliminarySurfaceCellOrigin = preliminarySurfaceCellOrigin;
               this.preliminarySurfaceCache[0] = this.noiseChunk.computePreliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX), surfaceCellToBlockCoord(cornerCellZ));
               this.preliminarySurfaceCache[1] = this.noiseChunk.computePreliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX + 1), surfaceCellToBlockCoord(cornerCellZ));
               this.preliminarySurfaceCache[2] = this.noiseChunk.computePreliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX), surfaceCellToBlockCoord(cornerCellZ + 1));
               this.preliminarySurfaceCache[3] = this.noiseChunk.computePreliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX + 1), surfaceCellToBlockCoord(cornerCellZ + 1));
            }

            int preliminarySurfaceLevel = Mth.floor(Mth.lerp2((float)(this.blockX & 15) / 16.0F, (float)(this.blockZ & 15) / 16.0F, (float)this.preliminarySurfaceCache[0], (float)this.preliminarySurfaceCache[1], (float)this.preliminarySurfaceCache[2], (float)this.preliminarySurfaceCache[3]));
            this.minSurfaceLevel = preliminarySurfaceLevel + this.surfaceDepth - 8;
         }

         return this.minSurfaceLevel;
      }

      protected DoubleSupplier getNoiseSampler(final ResourceKey noiseId, final boolean is3d) {
         return is3d ? (DoubleSupplier)this.noiseSamplers3d.computeIfAbsent(noiseId, this::createNoiseSampler3d) : (DoubleSupplier)this.noiseSamplers2d.computeIfAbsent(noiseId, this::createNoiseSampler2d);
      }

      private DoubleSupplier createNoiseSampler2d(final ResourceKey noiseId) {
         final Noise noise = this.randomState.getOrCreateNoise(noiseId);
         return new DoubleSupplier() {
            private long lastUpdateXZ;
            private double lastNoise;

            {
               Objects.requireNonNull(Context.this);
               this.lastUpdateXZ = Context.this.lastUpdateXZ - 1L;
            }

            public double getAsDouble() {
               if (this.lastUpdateXZ != Context.this.lastUpdateXZ) {
                  this.lastNoise = (double)noise.get((double)Context.this.blockX, 0.0D, (double)Context.this.blockZ);
                  this.lastUpdateXZ = Context.this.lastUpdateXZ;
               }

               return this.lastNoise;
            }
         };
      }

      private DoubleSupplier createNoiseSampler3d(final ResourceKey noiseId) {
         final Noise noise = this.randomState.getOrCreateNoise(noiseId);
         return new DoubleSupplier() {
            private long lastUpdateY;
            private double lastNoise;

            {
               Objects.requireNonNull(Context.this);
               this.lastUpdateY = Context.this.lastUpdateY - 1L;
            }

            public double getAsDouble() {
               if (this.lastUpdateY != Context.this.lastUpdateY) {
                  this.lastNoise = (double)noise.get((double)Context.this.blockX, (double)Context.this.blockY, (double)Context.this.blockZ);
                  this.lastUpdateY = Context.this.lastUpdateY;
               }

               return this.lastNoise;
            }
         };
      }

      private final class AbovePreliminarySurfaceCondition implements SurfaceRules.Condition {
         private AbovePreliminarySurfaceCondition() {
            Objects.requireNonNull(Context.this);
            super();
         }

         public boolean test() {
            return Context.this.blockY >= Context.this.getMinSurfaceLevel();
         }
      }

      private static final class HoleCondition extends SurfaceRules.LazyXZCondition {
         private HoleCondition(final SurfaceRules.Context context) {
            super(context);
         }

         protected boolean compute() {
            return this.context.surfaceDepth <= 0;
         }
      }

      private static class SteepMaterialCondition extends SurfaceRules.LazyXZCondition {
         private SteepMaterialCondition(final SurfaceRules.Context context) {
            super(context);
         }

         protected boolean compute() {
            int chunkBlockX = this.context.blockX & 15;
            int chunkBlockZ = this.context.blockZ & 15;
            int zNorth = Math.max(chunkBlockZ - 1, 0);
            int zSouth = Math.min(chunkBlockZ + 1, 15);
            ChunkAccess chunk = this.context.chunk;
            int heightNorth = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkBlockX, zNorth);
            int heightSouth = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkBlockX, zSouth);
            if (heightSouth >= heightNorth + 4) {
               return true;
            } else {
               int xWest = Math.max(chunkBlockX - 1, 0);
               int xEast = Math.min(chunkBlockX + 1, 15);
               int heightWest = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, xWest, chunkBlockZ);
               int heightEast = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, xEast, chunkBlockZ);
               return heightWest >= heightEast + 4;
            }
         }
      }

      private static class TemperatureHelperCondition extends SurfaceRules.LazyYCondition {
         private TemperatureHelperCondition(final SurfaceRules.Context context) {
            super(context);
         }

         protected boolean compute() {
            return ((Biome)this.context.getBiome().value()).coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ), this.context.getSeaLevel());
         }
      }
   }

   private static record HolderConditionSource(Holder holder) implements SurfaceRules.ConditionSource {
      public SurfaceRules.Condition apply(final SurfaceRules.Context context) {
         return (SurfaceRules.Condition)((SurfaceRules.ConditionSource)this.holder.value()).apply(context);
      }

      public MapCodec codec() {
         throw new UnsupportedOperationException("HolderConditionSource cannot be serialized");
      }
   }

   private static record HolderRuleSource(Holder holder) implements SurfaceRules.RuleSource {
      public SurfaceRules.SurfaceRule apply(final SurfaceRules.Context context) {
         return (SurfaceRules.SurfaceRule)((SurfaceRules.RuleSource)this.holder.value()).apply(context);
      }

      public MapCodec codec() {
         throw new UnsupportedOperationException("HolderRuleSource cannot be serialized");
      }
   }

   private static enum Hole implements SurfaceRules.ConditionSource {
      INSTANCE;

      private static final MapCodec CODEC = MapCodec.unit(INSTANCE);

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context context) {
         return context.hole;
      }

      // $FF: synthetic method
      private static SurfaceRules.Hole[] $values() {
         return new SurfaceRules.Hole[]{INSTANCE};
      }
   }

   private abstract static class LazyCondition implements SurfaceRules.Condition {
      protected final SurfaceRules.Context context;
      private long lastUpdate;
      private @Nullable Boolean result;

      protected LazyCondition(final SurfaceRules.Context context) {
         this.context = context;
         this.lastUpdate = this.getContextLastUpdate() - 1L;
      }

      public boolean test() {
         long lastContextUpdate = this.getContextLastUpdate();
         if (lastContextUpdate == this.lastUpdate) {
            if (this.result == null) {
               throw new IllegalStateException("Update triggered but the result is null");
            } else {
               return this.result;
            }
         } else {
            this.lastUpdate = lastContextUpdate;
            this.result = this.compute();
            return this.result;
         }
      }

      protected abstract long getContextLastUpdate();

      protected abstract boolean compute();
   }

   private abstract static class LazyXZCondition extends SurfaceRules.LazyCondition {
      protected LazyXZCondition(final SurfaceRules.Context context) {
         super(context);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateXZ;
      }
   }

   private abstract static class LazyYCondition extends SurfaceRules.LazyCondition {
      protected LazyYCondition(final SurfaceRules.Context context) {
         super(context);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateY;
      }
   }

   private static record NoiseThresholdConditionSource(ResourceKey noise, double minThreshold, double maxThreshold, boolean is3d) implements SurfaceRules.ConditionSource {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(SurfaceRules.NoiseThresholdConditionSource::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(SurfaceRules.NoiseThresholdConditionSource::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(SurfaceRules.NoiseThresholdConditionSource::maxThreshold), Codec.BOOL.optionalFieldOf("is_3d", false).forGetter(SurfaceRules.NoiseThresholdConditionSource::is3d)).apply(i, SurfaceRules.NoiseThresholdConditionSource::new));

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context ruleContext) {
         final DoubleSupplier noise = ruleContext.getNoiseSampler(this.noise, this.is3d);

         class NoiseThresholdCondition implements SurfaceRules.Condition {
            NoiseThresholdCondition() {
               Objects.requireNonNull(NoiseThresholdConditionSource.this);
               super();
            }

            public boolean test() {
               double value = noise.getAsDouble();
               return value >= NoiseThresholdConditionSource.this.minThreshold && value <= NoiseThresholdConditionSource.this.maxThreshold;
            }
         }

         return new NoiseThresholdCondition();
      }
   }

   private static record NotCondition(SurfaceRules.Condition target) implements SurfaceRules.Condition {
      public boolean test() {
         return !this.target.test();
      }
   }

   private static record NotConditionSource(SurfaceRules.ConditionSource target) implements SurfaceRules.ConditionSource {
      private static final MapCodec CODEC = SurfaceRules.ConditionSource.CODEC.xmap(SurfaceRules.NotConditionSource::new, SurfaceRules.NotConditionSource::target).fieldOf("invert");

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context context) {
         return new SurfaceRules.NotCondition((SurfaceRules.Condition)this.target.apply(context));
      }
   }

   public interface RuleSource extends Function {
      Codec DIRECT_CODEC = BuiltInRegistries.MATERIAL_RULE_TYPE.byNameCodec().dispatch(SurfaceRules.RuleSource::codec, Function.identity());
      Codec HOLDER_CODEC = RegistryCodecs.holder(Registries.MATERIAL_RULE, DIRECT_CODEC);
      Codec CODEC = HOLDER_CODEC.xmap((holder) -> {
         Objects.requireNonNull(holder);
         int index$1 = 0;
         Object var10000;
         switch (holder.typeSwitch<invokedynamic>(holder, index$1)) {
            case 0:
               Holder.Direct direct = (Holder.Direct)holder;
               var10000 = (SurfaceRules.RuleSource)direct.value();
               break;
            case 1:
               Holder.Reference reference = (Holder.Reference)holder;
               var10000 = new SurfaceRules.HolderRuleSource(reference);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return (SurfaceRules.RuleSource)var10000;
      }, (value) -> {
         Objects.requireNonNull(value);
         int index$2 = 0;
         Holder var8;
         switch (value.typeSwitch<invokedynamic>(value, index$2)) {
            case 0:
               SurfaceRules.HolderRuleSource $b$0 = (SurfaceRules.HolderRuleSource)value;
               SurfaceRules.HolderRuleSource var10000 = $b$0;

               try {
                  var7 = var10000.holder();
               } catch (Throwable var6) {
                  throw new MatchException(var6.toString(), var6);
               }

               Holder patt3$temp = var7;
               var8 = patt3$temp;
               break;
            default:
               var8 = Holder.direct(value);
         }

         return var8;
      });

      static MapCodec bootstrap(final Registry registry) {
         SurfaceRules.register(registry, "bandlands", SurfaceRules.Bandlands.CODEC);
         SurfaceRules.register(registry, "block", SurfaceRules.BlockRuleSource.CODEC);
         SurfaceRules.register(registry, "sequence", SurfaceRules.SequenceRuleSource.CODEC);
         return SurfaceRules.register(registry, "condition", SurfaceRules.TestRuleSource.CODEC);
      }

      MapCodec codec();
   }

   private static record SequenceRule(List rules) implements SurfaceRules.SurfaceRule {
      public @Nullable BlockState tryApply(final int blockX, final int blockY, final int blockZ) {
         for(SurfaceRules.SurfaceRule rule : this.rules) {
            BlockState state = rule.tryApply(blockX, blockY, blockZ);
            if (state != null) {
               return state;
            }
         }

         return null;
      }
   }

   private static record SequenceRuleSource(List sequence) implements SurfaceRules.RuleSource {
      private static final MapCodec CODEC = SurfaceRules.RuleSource.CODEC.listOf().xmap(SurfaceRules.SequenceRuleSource::new, SurfaceRules.SequenceRuleSource::sequence).fieldOf("sequence");

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(final SurfaceRules.Context context) {
         if (this.sequence.size() == 1) {
            return (SurfaceRules.SurfaceRule)((SurfaceRules.RuleSource)this.sequence.getFirst()).apply(context);
         } else {
            ImmutableList.Builder builder = ImmutableList.builder();

            for(SurfaceRules.RuleSource rule : this.sequence) {
               builder.add((SurfaceRules.SurfaceRule)rule.apply(context));
            }

            return new SurfaceRules.SequenceRule(builder.build());
         }
      }
   }

   private static record StateRule(BlockState state) implements SurfaceRules.SurfaceRule {
      public BlockState tryApply(final int blockX, final int blockY, final int blockZ) {
         return this.state;
      }
   }

   private static enum Steep implements SurfaceRules.ConditionSource {
      INSTANCE;

      private static final MapCodec CODEC = MapCodec.unit(INSTANCE);

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context context) {
         return context.steep;
      }

      // $FF: synthetic method
      private static SurfaceRules.Steep[] $values() {
         return new SurfaceRules.Steep[]{INSTANCE};
      }
   }

   private static record StoneDepthCheck(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) implements SurfaceRules.ConditionSource {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.StoneDepthCheck::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(SurfaceRules.StoneDepthCheck::addSurfaceDepth), Codec.INT.fieldOf("secondary_depth_range").forGetter(SurfaceRules.StoneDepthCheck::secondaryDepthRange), CaveSurface.CODEC.fieldOf("surface_type").forGetter(SurfaceRules.StoneDepthCheck::surfaceType)).apply(i, SurfaceRules.StoneDepthCheck::new));

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context ruleContext) {
         final boolean ceiling = this.surfaceType == CaveSurface.CEILING;

         class StoneDepthCondition extends SurfaceRules.LazyYCondition {
            private StoneDepthCondition() {
               Objects.requireNonNull(StoneDepthCheck.this);
               super(ruleContext);
            }

            protected boolean compute() {
               int stoneDepth = ceiling ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
               int surfaceDepth = StoneDepthCheck.this.addSurfaceDepth ? this.context.surfaceDepth : 0;
               int secondarySurfaceDepth = StoneDepthCheck.this.secondaryDepthRange == 0 ? 0 : (int)Mth.map(this.context.getSurfaceSecondary(), -1.0D, 1.0D, 0.0D, (double)StoneDepthCheck.this.secondaryDepthRange);
               return stoneDepth <= 1 + StoneDepthCheck.this.offset + surfaceDepth + secondarySurfaceDepth;
            }
         }

         return new StoneDepthCondition();
      }
   }

   protected interface SurfaceRule {
      @Nullable BlockState tryApply(final int blockX, final int blockY, final int blockZ);
   }

   private static enum Temperature implements SurfaceRules.ConditionSource {
      INSTANCE;

      private static final MapCodec CODEC = MapCodec.unit(INSTANCE);

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context context) {
         return context.temperature;
      }

      // $FF: synthetic method
      private static SurfaceRules.Temperature[] $values() {
         return new SurfaceRules.Temperature[]{INSTANCE};
      }
   }

   private static record TestRule(SurfaceRules.Condition condition, SurfaceRules.SurfaceRule followup) implements SurfaceRules.SurfaceRule {
      public @Nullable BlockState tryApply(final int blockX, final int blockY, final int blockZ) {
         return !this.condition.test() ? null : this.followup.tryApply(blockX, blockY, blockZ);
      }
   }

   private static record TestRuleSource(SurfaceRules.ConditionSource ifTrue, SurfaceRules.RuleSource thenRun) implements SurfaceRules.RuleSource {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SurfaceRules.ConditionSource.CODEC.fieldOf("if_true").forGetter(SurfaceRules.TestRuleSource::ifTrue), SurfaceRules.RuleSource.CODEC.fieldOf("then_run").forGetter(SurfaceRules.TestRuleSource::thenRun)).apply(i, SurfaceRules.TestRuleSource::new));

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(final SurfaceRules.Context context) {
         return new SurfaceRules.TestRule((SurfaceRules.Condition)this.ifTrue.apply(context), (SurfaceRules.SurfaceRule)this.thenRun.apply(context));
      }
   }

   private static record VerticalGradientConditionSource(Identifier randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements SurfaceRules.ConditionSource {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("random_name").forGetter(SurfaceRules.VerticalGradientConditionSource::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(SurfaceRules.VerticalGradientConditionSource::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(SurfaceRules.VerticalGradientConditionSource::falseAtAndAbove)).apply(i, SurfaceRules.VerticalGradientConditionSource::new));

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context ruleContext) {
         final int trueAtAndBelow = this.trueAtAndBelow().resolveY(ruleContext.context);
         final int falseAtAndAbove = this.falseAtAndAbove().resolveY(ruleContext.context);
         final PositionalRandomFactory randomFactory = ruleContext.randomState.getOrCreateRandomFactory(this.randomName());

         class VerticalGradientCondition extends SurfaceRules.LazyYCondition {
            private VerticalGradientCondition(final SurfaceRules.VerticalGradientConditionSource this$0) {
               Objects.requireNonNull(this$0);
               super(ruleContext);
            }

            protected boolean compute() {
               int blockY = this.context.blockY;
               if (blockY <= trueAtAndBelow) {
                  return true;
               } else if (blockY >= falseAtAndAbove) {
                  return false;
               } else {
                  double probability = Mth.map((double)blockY, (double)trueAtAndBelow, (double)falseAtAndAbove, 1.0D, 0.0D);
                  RandomSource random = randomFactory.at(this.context.blockX, blockY, this.context.blockZ);
                  return (double)random.nextFloat() < probability;
               }
            }
         }

         return new VerticalGradientCondition(this);
      }
   }

   private static record WaterConditionSource(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRules.ConditionSource {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.WaterConditionSource::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.WaterConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.WaterConditionSource::addStoneDepth)).apply(i, SurfaceRules.WaterConditionSource::new));

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context ruleContext) {
         class WaterCondition extends SurfaceRules.LazyYCondition {
            private WaterCondition() {
               Objects.requireNonNull(WaterConditionSource.this);
               super(ruleContext);
            }

            protected boolean compute() {
               return this.context.waterHeight == Integer.MIN_VALUE || this.context.blockY + (WaterConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.waterHeight + WaterConditionSource.this.offset + this.context.surfaceDepth * WaterConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new WaterCondition();
      }
   }

   private static record YConditionSource(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRules.ConditionSource {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(SurfaceRules.YConditionSource::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.YConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.YConditionSource::addStoneDepth)).apply(i, SurfaceRules.YConditionSource::new));

      public MapCodec codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context ruleContext) {
         class YCondition extends SurfaceRules.LazyYCondition {
            private YCondition() {
               Objects.requireNonNull(YConditionSource.this);
               super(ruleContext);
            }

            protected boolean compute() {
               return this.context.blockY + (YConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= YConditionSource.this.anchor.resolveY(this.context.context) + this.context.surfaceDepth * YConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new YCondition();
      }
   }
}
