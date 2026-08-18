package net.minecraft.client.renderer.feature;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class FeatureRendererMap implements AutoCloseable {
   private @Nullable FeatureRenderer[] renderers = new FeatureRenderer[0];

   public void put(final FeatureRendererType type, final FeatureRenderer renderer) {
      if (this.renderers.length <= type.id()) {
         this.renderers = (FeatureRenderer[])Arrays.copyOf(this.renderers, Mth.roundToward(type.id() + 1, 16));
      }

      this.renderers[type.id()] = renderer;
   }

   public @Nullable FeatureRenderer get(final FeatureRendererType type) {
      return type.id() >= this.renderers.length ? null : this.renderers[type.id()];
   }

   public FeatureRenderer getOrThrow(final FeatureRendererType type) {
      FeatureRenderer renderer = this.get(type);
      if (renderer == null) {
         throw new IllegalArgumentException("No FeatureRenderer for type " + String.valueOf(type));
      } else {
         return renderer;
      }
   }

   public Iterable values() {
      return Iterables.filter(Arrays.asList(this.renderers), Objects::nonNull);
   }

   public void close() {
      for(FeatureRenderer renderer : this.renderers) {
         if (renderer != null) {
            renderer.close();
         }
      }

   }
}
