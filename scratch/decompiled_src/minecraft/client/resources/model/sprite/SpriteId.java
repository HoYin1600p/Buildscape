package net.minecraft.client.resources.model.sprite;

import java.util.function.Function;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public record SpriteId(Identifier atlasLocation, Identifier texture) {
   public RenderType renderType(final Function renderType) {
      return (RenderType)renderType.apply(this.atlasLocation);
   }
}
