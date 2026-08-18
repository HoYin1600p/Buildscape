package net.minecraft.world.level.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.network.FriendlyByteBuf;

public interface PalettedContainerRO {
   Object get(int x, int y, int z);

   void getAll(Consumer consumer);

   void write(FriendlyByteBuf buffer);

   int getSerializedSize();

   @VisibleForTesting
   int bitsPerEntry();

   boolean maybeHas(Predicate predicate);

   void forEachInPalette(Consumer consumer);

   void count(PalettedContainer.CountConsumer output);

   PalettedContainer copy();

   PalettedContainer recreate();

   PalettedContainerRO.PackedData pack(Strategy strategy);

   public static record PackedData(List paletteEntries, Optional storage, int bitsPerEntry) {
      public static final int UNKNOWN_BITS_PER_ENTRY = -1;

      public PackedData(final List paletteEntries, final Optional storage) {
         this(paletteEntries, storage, -1);
      }
   }

   public interface Unpacker {
      DataResult read(Strategy strategy, PalettedContainerRO.PackedData discData);
   }
}
