package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.apache.commons.lang3.Validate;

public class LinearPalette implements Palette {
   private final Object[] values;
   private final int bits;
   private int size;

   private LinearPalette(final int bits, final List paletteEntries) {
      this.values = new Object[1 << bits];
      this.bits = bits;
      Validate.isTrue(paletteEntries.size() <= this.values.length, "Can't initialize LinearPalette of size %d with %d entries", new Object[]{this.values.length, paletteEntries.size()});

      for(int i = 0; i < paletteEntries.size(); ++i) {
         this.values[i] = paletteEntries.get(i);
      }

      this.size = paletteEntries.size();
   }

   private LinearPalette(final Object[] values, final int bits, final int size) {
      this.values = values;
      this.bits = bits;
      this.size = size;
   }

   public static Palette create(final int bits, final List paletteEntries) {
      return new LinearPalette(bits, paletteEntries);
   }

   public int idFor(final Object value, final PaletteResize resizeHandler) {
      for(int i = 0; i < this.size; ++i) {
         if (this.values[i] == value) {
            return i;
         }
      }

      int index = this.size;
      if (index < this.values.length) {
         this.values[index] = value;
         ++this.size;
         return index;
      } else {
         return resizeHandler.onResize(this.bits + 1, value);
      }
   }

   public boolean maybeHas(final Predicate predicate) {
      for(int i = 0; i < this.size; ++i) {
         if (predicate.test(this.values[i])) {
            return true;
         }
      }

      return false;
   }

   public Object valueFor(final int index) {
      if (index >= 0 && index < this.size) {
         return this.values[index];
      } else {
         throw new MissingPaletteEntryException(index);
      }
   }

   public void read(final FriendlyByteBuf buffer, final IdMap globalMap) {
      this.size = buffer.readVarInt();

      for(int i = 0; i < this.size; ++i) {
         this.values[i] = globalMap.byIdOrThrow(buffer.readVarInt());
      }

   }

   public void write(final FriendlyByteBuf buffer, final IdMap globalMap) {
      buffer.writeVarInt(this.size);

      for(int i = 0; i < this.size; ++i) {
         buffer.writeVarInt(globalMap.getId(this.values[i]));
      }

   }

   public int getSerializedSize(final IdMap globalMap) {
      int result = VarInt.getByteSize(this.getSize());

      for(int i = 0; i < this.getSize(); ++i) {
         result += VarInt.getByteSize(globalMap.getId(this.values[i]));
      }

      return result;
   }

   public int getSize() {
      return this.size;
   }

   public Palette copy() {
      return new LinearPalette(this.values.clone(), this.bits, this.size);
   }
}
