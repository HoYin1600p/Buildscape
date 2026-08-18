package net.minecraft.world.level.chunk;

public interface PaletteResize {
   int onResize(int bits, Object lastAddedValue);

   static PaletteResize noResizeExpected() {
      return (bits, lastAddedValue) -> {
         throw new IllegalArgumentException("Unexpected palette resize, bits = " + bits + ", added value = " + String.valueOf(lastAddedValue));
      };
   }
}
