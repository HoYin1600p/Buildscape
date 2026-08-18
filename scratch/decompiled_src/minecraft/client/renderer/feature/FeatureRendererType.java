package net.minecraft.client.renderer.feature;

import java.util.concurrent.atomic.AtomicInteger;

public record FeatureRendererType(int id, String name) {
   private static final AtomicInteger NEXT_ID = new AtomicInteger();

   public static FeatureRendererType create(final String name) {
      return new FeatureRendererType(NEXT_ID.getAndIncrement(), name);
   }

   public String toString() {
      return this.name;
   }
}
