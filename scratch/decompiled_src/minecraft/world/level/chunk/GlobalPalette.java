package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette implements Palette {
   private final IdMap registry;

   public GlobalPalette(final IdMap registry) {
      this.registry = registry;
   }

   public int idFor(final Object value, final PaletteResize resizeHandler) {
      int id = this.registry.getId(value);
      return id == -1 ? 0 : id;
   }

   public boolean maybeHas(final Predicate predicate) {
      return true;
   }

   public Object valueFor(final int index) {
      Object value = (T)this.registry.byId(index);
      if (value == null) {
         throw new MissingPaletteEntryException(index);
      } else {
         return value;
      }
   }

   public void read(final FriendlyByteBuf buffer, final IdMap globalMap) {
   }

   public void write(final FriendlyByteBuf buffer, final IdMap globalMap) {
   }

   public int getSerializedSize(final IdMap globalMap) {
      return 0;
   }

   public int getSize() {
      return this.registry.size();
   }

   public Palette copy() {
      return this;
   }
}
