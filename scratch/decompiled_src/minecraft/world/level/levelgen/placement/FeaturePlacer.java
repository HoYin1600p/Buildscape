package net.minecraft.world.level.levelgen.placement;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureCountTracker;

public class FeaturePlacer {
   private final WorldGenLevel level;
   private final ChunkGenerator generator;
   private final List positions = new ArrayList();
   private final IntList modifierIndices = new IntArrayList();
   private final List modifiedPositions = new ArrayList();

   public FeaturePlacer(final WorldGenLevel level, final ChunkGenerator generator) {
      this.level = level;
      this.generator = generator;
   }

   public boolean place(final PlacedFeature placedFeature, final RandomSource random, final BlockPos origin) {
      return this.place(placedFeature, random, origin, false);
   }

   public boolean placeWithBiomeCheck(final PlacedFeature placedFeature, final RandomSource random, final BlockPos origin) {
      return this.place(placedFeature, random, origin, true);
   }

   private boolean place(final PlacedFeature placedFeature, final RandomSource random, final BlockPos origin, final boolean biomeCheck) {
      Optional topFeature = biomeCheck ? Optional.of(placedFeature) : Optional.empty();
      PlacementContext context = new PlacementContext(this.level, this.generator, topFeature);
      Feature feature = (Feature)placedFeature.feature().value();
      List placement = placedFeature.placement();
      if (placement.isEmpty()) {
         if (SharedConstants.DEBUG_FEATURE_COUNT) {
            FeatureCountTracker.featurePlaced(this.level.getLevel(), feature, topFeature);
         }

         return feature.place(this.level, this.generator, random, origin);
      } else {
         boolean placedAny = false;
         this.positions.add(origin);
         this.modifierIndices.add(0);

         for(; !this.positions.isEmpty(); this.modifiedPositions.clear()) {
            BlockPos pos = (BlockPos)this.positions.removeLast();
            int modifierIndex = this.modifierIndices.removeInt(this.modifierIndices.size() - 1);
            PlacementModifier modifier = (PlacementModifier)placement.get(modifierIndex);
            modifier.modify(context, random, pos, this.modifiedPositions::add);
            int nextModifierIndex = modifierIndex + 1;
            if (nextModifierIndex < placement.size()) {
               for(int i = this.modifiedPositions.size() - 1; i >= 0; --i) {
                  this.positions.add((BlockPos)this.modifiedPositions.get(i));
                  this.modifierIndices.add(nextModifierIndex);
               }
            } else {
               for(BlockPos nextPos : this.modifiedPositions) {
                  placedAny |= feature.place(this.level, this.generator, random, nextPos);
                  if (SharedConstants.DEBUG_FEATURE_COUNT) {
                     FeatureCountTracker.featurePlaced(this.level.getLevel(), feature, topFeature);
                  }
               }
            }
         }

         return placedAny;
      }
   }
}
