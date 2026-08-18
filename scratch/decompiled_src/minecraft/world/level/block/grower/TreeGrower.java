package net.minecraft.world.level.block.grower;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import org.jspecify.annotations.Nullable;

public final class TreeGrower {
   private static final Map GROWERS = new Object2ObjectArrayMap();
   public static final Codec CODEC = Codec.stringResolver((g) -> g.name, GROWERS::get);
   public static final TreeGrower OAK = new TreeGrower("oak", WeightedList.of(new Weighted(TreeFeatures.OAK, 9), new Weighted(TreeFeatures.FANCY_OAK, 1)), WeightedList.of(), WeightedList.of(new Weighted(TreeFeatures.OAK_BEES_005, 9), new Weighted(TreeFeatures.FANCY_OAK_BEES_005, 1)), TreeFeatures.OAK);
   public static final TreeGrower SPRUCE = new TreeGrower("spruce", WeightedList.of((Object)TreeFeatures.SPRUCE), WeightedList.of(new Weighted(TreeFeatures.MEGA_SPRUCE, 1), new Weighted(TreeFeatures.MEGA_PINE, 1)), WeightedList.of(), TreeFeatures.SPRUCE);
   public static final TreeGrower MANGROVE = new TreeGrower("mangrove", WeightedList.of(new Weighted(TreeFeatures.MANGROVE, 15), new Weighted(TreeFeatures.TALL_MANGROVE, 85)), WeightedList.of(), WeightedList.of(), TreeFeatures.MANGROVE);
   public static final TreeGrower AZALEA = new TreeGrower("azalea", WeightedList.of((Object)TreeFeatures.AZALEA_TREE), WeightedList.of(), WeightedList.of(), TreeFeatures.AZALEA_TREE);
   public static final TreeGrower BIRCH = new TreeGrower("birch", WeightedList.of((Object)TreeFeatures.BIRCH), WeightedList.of(), WeightedList.of((Object)TreeFeatures.BIRCH_BEES_005), TreeFeatures.BIRCH);
   public static final TreeGrower JUNGLE = new TreeGrower("jungle", WeightedList.of((Object)TreeFeatures.JUNGLE_TREE_NO_VINE), WeightedList.of((Object)TreeFeatures.MEGA_JUNGLE_TREE), WeightedList.of(), TreeFeatures.JUNGLE_TREE_NO_VINE);
   public static final TreeGrower ACACIA = new TreeGrower("acacia", WeightedList.of((Object)TreeFeatures.ACACIA), WeightedList.of(), WeightedList.of(), TreeFeatures.ACACIA);
   public static final TreeGrower CHERRY = new TreeGrower("cherry", WeightedList.of((Object)TreeFeatures.CHERRY), WeightedList.of(), WeightedList.of((Object)TreeFeatures.CHERRY_BEES_005), TreeFeatures.CHERRY);
   public static final TreeGrower DARK_OAK = new TreeGrower("dark_oak", WeightedList.of(), WeightedList.of((Object)TreeFeatures.DARK_OAK), WeightedList.of(), (ResourceKey)null);
   public static final TreeGrower PALE_OAK = new TreeGrower("pale_oak", WeightedList.of(), WeightedList.of((Object)TreeFeatures.PALE_OAK_BONEMEAL), WeightedList.of(), (ResourceKey)null);
   public static final TreeGrower POPLAR = new TreeGrower("poplar", WeightedList.of(new Weighted(TreeFeatures.RED_POPLAR, 1), new Weighted(TreeFeatures.ORANGE_POPLAR, 1), new Weighted(TreeFeatures.YELLOW_POPLAR, 1)), WeightedList.of(), WeightedList.of(), TreeFeatures.RED_POPLAR);
   private final String name;
   private final WeightedList trees;
   private final WeightedList megaTrees;
   private final WeightedList flowerTrees;
   private final @Nullable ResourceKey shortestTreeType;

   public TreeGrower(final String name, final WeightedList trees, final WeightedList megaTrees, final WeightedList flowerTrees, final @Nullable ResourceKey shortestTreeType) {
      this.name = name;
      this.trees = trees;
      this.megaTrees = megaTrees;
      this.flowerTrees = flowerTrees;
      this.shortestTreeType = shortestTreeType;
      GROWERS.put(name, this);
   }

   private @Nullable ResourceKey getConfiguredFeature(final RandomSource random, final boolean hasFlowers) {
      return hasFlowers && !this.flowerTrees.isEmpty() ? (ResourceKey)this.flowerTrees.getRandom(random).orElse((Object)null) : (ResourceKey)this.trees.getRandom(random).orElse((Object)null);
   }

   private @Nullable ResourceKey getConfiguredMegaFeature(final RandomSource random) {
      return (ResourceKey)this.megaTrees.getRandom(random).orElse((Object)null);
   }

   public boolean growTree(final ServerLevel level, final ChunkGenerator generator, final BlockPos pos, final BlockState state, final RandomSource random) {
      ResourceKey megaFeatureKey = this.getConfiguredMegaFeature(random);
      if (megaFeatureKey != null) {
         Holder featureHolder = (Holder)level.registryAccess().lookupOrThrow(Registries.FEATURE).get(megaFeatureKey).orElse((Object)null);
         if (featureHolder != null) {
            Optional twoByTwoSaplingPos = findTwoByTwoSaplingPos(level, state, pos);
            if (twoByTwoSaplingPos.isPresent()) {
               int dx = ((TreeGrower.TwoByTwoSaplingPos)twoByTwoSaplingPos.get()).offsetX();
               int dz = ((TreeGrower.TwoByTwoSaplingPos)twoByTwoSaplingPos.get()).offsetZ();
               List groundLevelSurroundingBlocks = ((TreeGrower.TwoByTwoSaplingPos)twoByTwoSaplingPos.get()).groundLevelSurroundingBlocks();
               Feature feature = (Feature)featureHolder.value();
               removeSaplings(level, groundLevelSurroundingBlocks);
               if (feature.place(level, generator, random, pos.offset(dx, 0, dz))) {
                  return true;
               }

               resetSaplings(level, groundLevelSurroundingBlocks);
               return false;
            }
         }
      }

      ResourceKey featureKey = this.getConfiguredFeature(random, this.hasFlowers(level, pos));
      if (featureKey == null) {
         return false;
      } else {
         Holder featureHolder = (Holder)level.registryAccess().lookupOrThrow(Registries.FEATURE).get(featureKey).orElse((Object)null);
         if (featureHolder == null) {
            return false;
         } else {
            Feature feature = (Feature)featureHolder.value();
            removeSapling(level, pos);
            if (feature.place(level, generator, random, pos)) {
               return true;
            } else {
               resetSaplings(level, List.of(Pair.of(state, pos)));
               return false;
            }
         }
      }
   }

   private static List getSurroundingBlockStates(final ServerLevel level, final BlockPos pos, final int dx, final int dz) {
      return List.of(Pair.of(level.getBlockState(pos.offset(dx, 0, dz)), pos.offset(dx, 0, dz)), Pair.of(level.getBlockState(pos.offset(dx + 1, 0, dz)), pos.offset(dx + 1, 0, dz)), Pair.of(level.getBlockState(pos.offset(dx, 0, dz + 1)), pos.offset(dx, 0, dz + 1)), Pair.of(level.getBlockState(pos.offset(dx + 1, 0, dz + 1)), pos.offset(dx + 1, 0, dz + 1)));
   }

   private static void removeSaplings(final ServerLevel level, final List saplingBlocks) {
      for(Pair saplingBlock : saplingBlocks) {
         BlockPos saplingPosition = (BlockPos)saplingBlock.getSecond();
         removeSapling(level, saplingPosition);
      }

   }

   private static void removeSapling(final ServerLevel level, final BlockPos saplingPosition) {
      BlockState emptyBlock = level.getFluidState(saplingPosition).createLegacyBlock();
      level.setBlock(saplingPosition, emptyBlock, 818);
   }

   private static void resetSaplings(final ServerLevel level, final List saplingBlocks) {
      for(Pair saplingBlock : saplingBlocks) {
         level.setBlock((BlockPos)saplingBlock.getSecond(), (BlockState)saplingBlock.getFirst(), 260);
      }

   }

   private static boolean isTwoByTwoSapling(final BlockState state, final List surroundingBlocks) {
      Block block = state.getBlock();

      for(Pair surroundingBlock : surroundingBlocks) {
         BlockState surroundingBlockState = (BlockState)surroundingBlock.getFirst();
         if (!surroundingBlockState.is(block)) {
            return false;
         }
      }

      return true;
   }

   private boolean hasFlowers(final LevelAccessor level, final BlockPos pos) {
      return level.findBlocksIn(pos.offset(-2, -1, -2), pos.offset(2, 1, 2)).filterState((state) -> state.is(BlockTags.FLOWERS)).anyMatched();
   }

   public OptionalInt getMinimumHeight(final ServerLevel level) {
      ResourceKey featureKey = this.shortestTreeType;
      if (featureKey == null) {
         return OptionalInt.empty();
      } else {
         Holder featureHolder = (Holder)level.registryAccess().lookupOrThrow(Registries.FEATURE).get(featureKey).orElse((Object)null);
         if (featureHolder != null) {
            Object var5 = featureHolder.value();
            if (var5 instanceof TreeFeature) {
               TreeFeature treeFeature = (TreeFeature)var5;
               return OptionalInt.of(treeFeature.trunkPlacer().getBaseHeight());
            }
         }

         return OptionalInt.empty();
      }
   }

   public boolean canGrow(final ServerLevel level, final BlockPos pos, final BlockState state) {
      ResourceKey featureKey = this.getConfiguredFeature(level.getRandom(), this.hasFlowers(level, pos));
      ResourceKey megaFeatureKey = this.getConfiguredMegaFeature(level.getRandom());
      return featureKey == null && megaFeatureKey != null ? findTwoByTwoSaplingPos(level, state, pos).isPresent() : true;
   }

   private static Optional findTwoByTwoSaplingPos(final ServerLevel level, final BlockState state, final BlockPos pos) {
      for(int dx = 0; dx >= -1; --dx) {
         for(int dz = 0; dz >= -1; --dz) {
            List groundLevelSurroundingBlocks = getSurroundingBlockStates(level, pos, dx, dz);
            if (isTwoByTwoSapling(state, groundLevelSurroundingBlocks)) {
               return Optional.of(new TreeGrower.TwoByTwoSaplingPos(dx, dz, groundLevelSurroundingBlocks));
            }
         }
      }

      return Optional.empty();
   }

   private static record TwoByTwoSaplingPos(int offsetX, int offsetZ, List groundLevelSurroundingBlocks) {
   }
}
