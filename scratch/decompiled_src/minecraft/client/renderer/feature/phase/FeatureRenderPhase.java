package net.minecraft.client.renderer.feature.phase;

import java.util.Collection;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.SubmitNode;

public interface FeatureRenderPhase {
   void submit(SubmitNode submit);

   void sortInto(FeatureRenderPhase.Output output);

   boolean isEmpty();

   @FunctionalInterface
   public interface Output {
      void accept(SubmitNode submit, boolean strictlyOrdered);

      default void acceptFeatureGroup(final FeatureRendererType featureType, final Collection submits, final boolean strictlyOrdered) {
         for(SubmitNode submit : submits) {
            if (submit.featureType() != featureType) {
               throw new IllegalArgumentException(String.valueOf(submit) + " was not of feature type " + String.valueOf(featureType));
            }

            this.accept(submit, strictlyOrdered);
         }

      }
   }
}
