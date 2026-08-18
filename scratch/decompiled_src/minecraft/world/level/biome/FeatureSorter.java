package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Graph;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class FeatureSorter {
   public static List buildFeaturesPerStep(final List featureSources, final Function featureGetter, final boolean tryReducingError) {
      Reference2IntMap featureIndex = new Reference2IntOpenHashMap();
      MutableInt nextFeatureIndex = new MutableInt(0);
      Comparator featureDataComparator = Comparator.comparingInt(FeatureData::step).thenComparingInt(FeatureData::featureIndex);
      Map edges = new TreeMap(featureDataComparator);
      int maxStep = 0;

      record FeatureData(int featureIndex, int step, PlacedFeature feature) {
      }

      for(Object featureSource : featureSources) {
         List featureList = Lists.newArrayList();
         List featuresForStep = (List)featureGetter.apply(featureSource);
         maxStep = Math.max(maxStep, featuresForStep.size());

         for(int i = 0; i < featuresForStep.size(); ++i) {
            for(Holder featureSupplier : (HolderSet)featuresForStep.get(i)) {
               PlacedFeature feature = (PlacedFeature)featureSupplier.value();
               featureList.add(new FeatureData(featureIndex.computeIfAbsent(feature, (f) -> nextFeatureIndex.getAndIncrement()), i, feature));
            }
         }

         for(int i = 0; i < featureList.size(); ++i) {
            Set data = (Set)edges.computeIfAbsent((FeatureData)featureList.get(i), (k) -> new TreeSet(featureDataComparator));
            if (i < featureList.size() - 1) {
               data.add((FeatureData)featureList.get(i + 1));
            }
         }
      }

      Set discovered = new TreeSet(featureDataComparator);
      Set currentlyVisiting = new TreeSet(featureDataComparator);
      List sortedFeatures = Lists.newArrayList();

      for(FeatureData feature : edges.keySet()) {
         if (!currentlyVisiting.isEmpty()) {
            throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
         }

         if (!discovered.contains(feature) && Graph.depthFirstSearch(edges, discovered, currentlyVisiting, sortedFeatures::add, feature)) {
            if (!tryReducingError) {
               throw new IllegalStateException("Feature order cycle found");
            }

            List reducedSources = new ArrayList(featureSources);

            int lastSize;
            do {
               lastSize = reducedSources.size();
               ListIterator iterator = reducedSources.listIterator();

               while(iterator.hasNext()) {
                  Object source = (T)iterator.next();
                  iterator.remove();

                  try {
                     buildFeaturesPerStep(reducedSources, featureGetter, false);
                  } catch (IllegalStateException var18) {
                     continue;
                  }

                  iterator.add(source);
               }
            } while(lastSize != reducedSources.size());

            throw new IllegalStateException("Feature order cycle found, involved sources: " + String.valueOf(reducedSources));
         }
      }

      Collections.reverse(sortedFeatures);
      ImmutableList.Builder features = ImmutableList.builder();

      for(int step = 0; step < maxStep; ++step) {
         int finalStep = step;
         List featuresInStep = (List)sortedFeatures.stream().filter((p) -> p.step() == finalStep).map(FeatureData::feature).collect(Collectors.toList());
         features.add(new FeatureSorter.StepFeatureData(featuresInStep));
      }

      return features.build();
   }

   public static record StepFeatureData(List features, ToIntFunction indexMapping) {
      private StepFeatureData(final List features) {
         this(features, Util.createIndexIdentityLookup(features));
      }
   }
}
