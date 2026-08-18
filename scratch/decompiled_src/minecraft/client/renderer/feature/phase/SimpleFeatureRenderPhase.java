package net.minecraft.client.renderer.feature.phase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.BatchableSubmit;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;

public class SimpleFeatureRenderPhase implements FeatureRenderPhase {
   private @Nullable SimpleFeatureRenderPhase.FeatureSubmits[] submitsByFeature = new SimpleFeatureRenderPhase.FeatureSubmits[0];

   public void submit(final SubmitNode submit) {
      FeatureRendererType type = submit.featureType();
      if (this.submitsByFeature.length <= type.id()) {
         this.submitsByFeature = (SimpleFeatureRenderPhase.FeatureSubmits[])Arrays.copyOf(this.submitsByFeature, Mth.roundToward(type.id() + 1, 16));
      }

      SimpleFeatureRenderPhase.FeatureSubmits submits = this.submitsByFeature[type.id()];
      if (submits == null) {
         submits = new SimpleFeatureRenderPhase.FeatureSubmits(type);
         this.submitsByFeature[type.id()] = submits;
      }

      submits.addUnchecked(submit);
   }

   public void sortInto(final FeatureRenderPhase.Output output) {
      for(SimpleFeatureRenderPhase.FeatureSubmits submits : (SimpleFeatureRenderPhase.FeatureSubmits[])maybeShuffle(this.submitsByFeature)) {
         if (submits != null) {
            sortFeatureInto(output, submits);
         }
      }

      this.clear();
   }

   private static void sortFeatureInto(final FeatureRenderPhase.Output output, final SimpleFeatureRenderPhase.FeatureSubmits submits) {
      output.acceptFeatureGroup(submits.featureType, maybeShuffle(submits.unbatched), false);

      for(List batch : maybeShuffle(submits.batches.values())) {
         output.acceptFeatureGroup(submits.featureType, maybeShuffle(batch), false);
      }

   }

   private static Collection maybeShuffle(final Collection collection) {
      if (SharedConstants.DEBUG_SHUFFLE_MODELS) {
         List shuffled = new ArrayList(collection);
         Collections.shuffle(shuffled);
         return shuffled;
      } else {
         return collection;
      }
   }

   private static Object[] maybeShuffle(final Object[] array) {
      if (SharedConstants.DEBUG_SHUFFLE_MODELS) {
         Object[] shuffled = (V[])Arrays.copyOf(array, array.length);
         ArrayUtils.shuffle(shuffled);
         return shuffled;
      } else {
         return array;
      }
   }

   public void clear() {
      for(int i = 0; i < this.submitsByFeature.length; ++i) {
         SimpleFeatureRenderPhase.FeatureSubmits submits = this.submitsByFeature[i];
         if (submits != null) {
            if (submits.isEmpty()) {
               this.submitsByFeature[i] = null;
            } else {
               submits.clear();
            }
         }
      }

   }

   public boolean isEmpty() {
      for(SimpleFeatureRenderPhase.FeatureSubmits submits : this.submitsByFeature) {
         if (submits != null && !submits.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private static class FeatureSubmits {
      private final FeatureRendererType featureType;
      private final List unbatched = new ArrayList();
      private final Map batches = new HashMap();

      private FeatureSubmits(final FeatureRendererType featureType) {
         this.featureType = featureType;
      }

      public void addUnchecked(final SubmitNode submit) {
         this.add(submit);
      }

      public void add(final SubmitNode submit) {
         Object key = batchKey(submit);
         if (key == null) {
            this.unbatched.add(submit);
         } else {
            ((List)this.batches.computeIfAbsent(key, (var0) -> new ArrayList())).add(submit);
         }

      }

      private static @Nullable Object batchKey(final SubmitNode submit) {
         if (submit instanceof BatchableSubmit batchable) {
            return batchable.batchKey();
         } else {
            return null;
         }
      }

      public boolean isEmpty() {
         if (!this.unbatched.isEmpty()) {
            return false;
         } else {
            for(List submits : this.batches.values()) {
               if (!submits.isEmpty()) {
                  return false;
               }
            }

            return true;
         }
      }

      public void clear() {
         this.unbatched.clear();
         this.batches.values().removeIf((submits) -> {
            if (submits.isEmpty()) {
               return true;
            } else {
               submits.clear();
               return false;
            }
         });
      }
   }
}
