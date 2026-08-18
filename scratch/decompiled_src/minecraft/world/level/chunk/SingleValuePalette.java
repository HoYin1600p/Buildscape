package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

public class SingleValuePalette implements Palette {
   private @Nullable Object value;

   public SingleValuePalette(final List paletteEntries) {
      if (!paletteEntries.isEmpty()) {
         Validate.isTrue(paletteEntries.size() <= 1, "Can't initialize SingleValuePalette with %d values.", (long)paletteEntries.size());
         this.value = paletteEntries.getFirst();
      }

   }

   public static Palette create(final int bits, final List paletteEntries) {
      return new SingleValuePalette(paletteEntries);
   }

   public int idFor(final Object value, final PaletteResize resizeHandler) {
      if (this.value != null && this.value != value) {
         return resizeHandler.onResize(1, value);
      } else {
         this.value = value;
         return 0;
      }
   }

   public boolean maybeHas(final Predicate predicate) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return predicate.test(this.value);
      }
   }

   public Object valueFor(final int index) {
      if (this.value != null && index == 0) {
         return this.value;
      } else {
         throw new IllegalStateException("Missing Palette entry for id " + index + ".");
      }
   }

   public void read(final FriendlyByteBuf buffer, final IdMap globalMap) {
      this.value = globalMap.byIdOrThrow(buffer.readVarInt());
   }

   public void write(final FriendlyByteBuf buffer, final IdMap globalMap) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         buffer.writeVarInt(globalMap.getId(this.value));
      }
   }

   public int getSerializedSize(final IdMap globalMap) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return VarInt.getByteSize(globalMap.getId(this.value));
      }
   }

   public int getSize() {
      return 1;
   }

   public Palette copy() {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return this;
      }
   }
}
