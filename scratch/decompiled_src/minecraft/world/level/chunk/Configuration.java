package net.minecraft.world.level.chunk;

import java.util.List;

public interface Configuration {
   boolean alwaysRepack();

   int bitsInMemory();

   int bitsInStorage();

   Palette createPalette(Strategy strategy, List paletteEntries);

   public static record Global(int bitsInMemory, int bitsInStorage) implements Configuration {
      public boolean alwaysRepack() {
         return true;
      }

      public Palette createPalette(final Strategy strategy, final List paletteEntries) {
         return strategy.globalPalette();
      }
   }

   public static record Simple(Palette.Factory factory, int bits) implements Configuration {
      public boolean alwaysRepack() {
         return false;
      }

      public Palette createPalette(final Strategy strategy, final List paletteEntries) {
         return this.factory.create(this.bits, paletteEntries);
      }

      public int bitsInMemory() {
         return this.bits;
      }

      public int bitsInStorage() {
         return this.bits;
      }
   }
}
