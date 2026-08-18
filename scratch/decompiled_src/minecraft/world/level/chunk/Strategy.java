package net.minecraft.world.level.chunk;

import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;

public abstract class Strategy {
   private static final Palette.Factory SINGLE_VALUE_PALETTE_FACTORY = SingleValuePalette::create;
   private static final Palette.Factory LINEAR_PALETTE_FACTORY = LinearPalette::create;
   private static final Palette.Factory HASHMAP_PALETTE_FACTORY = HashMapPalette::create;
   private static final Configuration ZERO_BITS = new Configuration.Simple(SINGLE_VALUE_PALETTE_FACTORY, 0);
   private static final Configuration ONE_BIT_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 1);
   private static final Configuration TWO_BITS_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 2);
   private static final Configuration THREE_BITS_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 3);
   private static final Configuration FOUR_BITS_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 4);
   private static final Configuration FIVE_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 5);
   private static final Configuration SIX_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 6);
   private static final Configuration SEVEN_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 7);
   private static final Configuration EIGHT_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 8);
   private final IdMap globalMap;
   private final GlobalPalette globalPalette;
   protected final int globalPaletteBitsInMemory;
   private final int bitsPerAxis;
   private final int entryCount;

   private Strategy(final IdMap globalMap, final int bitsPerAxis) {
      this.globalMap = globalMap;
      this.globalPalette = new GlobalPalette(globalMap);
      this.globalPaletteBitsInMemory = minimumBitsRequiredForDistinctValues(globalMap.size());
      this.bitsPerAxis = bitsPerAxis;
      this.entryCount = 1 << bitsPerAxis * 3;
   }

   public static Strategy createForBlockStates(final IdMap registry) {
      return new Strategy(registry, 4) {
         public Configuration getConfigurationForBitCount(final int entryBits) {
            Object var10000;
            switch (entryBits) {
               case 0:
                  var10000 = Strategy.ZERO_BITS;
                  break;
               case 1:
               case 2:
               case 3:
               case 4:
                  var10000 = Strategy.FOUR_BITS_LINEAR;
                  break;
               case 5:
                  var10000 = Strategy.FIVE_BITS_HASHMAP;
                  break;
               case 6:
                  var10000 = Strategy.SIX_BITS_HASHMAP;
                  break;
               case 7:
                  var10000 = Strategy.SEVEN_BITS_HASHMAP;
                  break;
               case 8:
                  var10000 = Strategy.EIGHT_BITS_HASHMAP;
                  break;
               default:
                  var10000 = new Configuration.Global(this.globalPaletteBitsInMemory, entryBits);
            }

            return (Configuration)var10000;
         }
      };
   }

   public static Strategy createForBiomes(final IdMap registry) {
      return new Strategy(registry, 2) {
         public Configuration getConfigurationForBitCount(final int entryBits) {
            Object var10000;
            switch (entryBits) {
               case 0:
                  var10000 = Strategy.ZERO_BITS;
                  break;
               case 1:
                  var10000 = Strategy.ONE_BIT_LINEAR;
                  break;
               case 2:
                  var10000 = Strategy.TWO_BITS_LINEAR;
                  break;
               case 3:
                  var10000 = Strategy.THREE_BITS_LINEAR;
                  break;
               default:
                  var10000 = new Configuration.Global(this.globalPaletteBitsInMemory, entryBits);
            }

            return (Configuration)var10000;
         }
      };
   }

   public int entryCount() {
      return this.entryCount;
   }

   public int getIndex(final int x, final int y, final int z) {
      return (y << this.bitsPerAxis | z) << this.bitsPerAxis | x;
   }

   public IdMap globalMap() {
      return this.globalMap;
   }

   public GlobalPalette globalPalette() {
      return this.globalPalette;
   }

   protected abstract Configuration getConfigurationForBitCount(int entryBits);

   protected Configuration getConfigurationForPaletteSize(final int paletteSize) {
      int bits = minimumBitsRequiredForDistinctValues(paletteSize);
      return this.getConfigurationForBitCount(bits);
   }

   private static int minimumBitsRequiredForDistinctValues(final int count) {
      return Mth.ceillog2(count);
   }
}
