package net.minecraft.client.model.geom;

import net.minecraft.resources.Identifier;

public record ModelLayerLocation(Identifier model, String layer) {
   public String toString() {
      return String.valueOf(this.model) + "#" + this.layer;
   }
}
