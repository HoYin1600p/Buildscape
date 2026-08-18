package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeLayerSettings {
   private static final Codec LAYER_RANGE = Codec.doubleRange(0.01D, 50.0D);
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LAYER_RANGE.optionalFieldOf("filling", 1.7D).forGetter((c) -> c.filling), LAYER_RANGE.optionalFieldOf("inner_layer", 2.2D).forGetter((c) -> c.innerLayer), LAYER_RANGE.optionalFieldOf("middle_layer", 3.2D).forGetter((c) -> c.middleLayer), LAYER_RANGE.optionalFieldOf("outer_layer", 4.2D).forGetter((c) -> c.outerLayer)).apply(i, GeodeLayerSettings::new));
   public final double filling;
   public final double innerLayer;
   public final double middleLayer;
   public final double outerLayer;

   public GeodeLayerSettings(final double filling, final double innerLayer, final double middleLayer, final double outerLayer) {
      this.filling = filling;
      this.innerLayer = innerLayer;
      this.middleLayer = middleLayer;
      this.outerLayer = outerLayer;
   }
}
