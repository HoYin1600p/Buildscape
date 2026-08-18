package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public interface Palette {
   int idFor(Object value, PaletteResize resizeHandler);

   boolean maybeHas(Predicate predicate);

   Object valueFor(int index);

   void read(FriendlyByteBuf buffer, IdMap globalMap);

   void write(FriendlyByteBuf buffer, IdMap globalMap);

   int getSerializedSize(IdMap globalMap);

   int getSize();

   Palette copy();

   public interface Factory {
      Palette create(int bits, List paletteEntries);
   }
}
