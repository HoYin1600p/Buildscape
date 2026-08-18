package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Locale;

public class StitcherException extends RuntimeException {
   private final Collection allSprites;

   public StitcherException(final Stitcher.Entry sprite, final Collection allSprites) {
      super(String.format(Locale.ROOT, "Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", sprite.name(), sprite.width(), sprite.height()));
      this.allSprites = allSprites;
   }

   public Collection getAllSprites() {
      return this.allSprites;
   }
}
