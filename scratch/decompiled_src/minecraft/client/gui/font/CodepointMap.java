package net.minecraft.client.gui.font;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.function.IntFunction;
import org.jspecify.annotations.Nullable;

public class CodepointMap {
   private static final int BLOCK_BITS = 8;
   private static final int BLOCK_SIZE = 256;
   private static final int IN_BLOCK_MASK = 255;
   private static final int MAX_BLOCK = 4351;
   private static final int BLOCK_COUNT = 4352;
   private final Object[] empty;
   private final @Nullable Object[][] blockMap;
   private final IntFunction blockConstructor;

   public CodepointMap(final IntFunction blockConstructor, final IntFunction blockMapConstructor) {
      this.empty = blockConstructor.apply(256);
      this.blockMap = blockMapConstructor.apply(4352);
      Arrays.fill(this.blockMap, this.empty);
      this.blockConstructor = blockConstructor;
   }

   public void clear() {
      Arrays.fill(this.blockMap, this.empty);
   }

   public @Nullable Object get(final int codepoint) {
      int block = codepoint >> 8;
      int offset = codepoint & 255;
      return this.blockMap[block][offset];
   }

   public @Nullable Object put(final int codepoint, final Object value) {
      int block = codepoint >> 8;
      int offset = codepoint & 255;
      Object[] blockData = (T[])this.blockMap[block];
      if (blockData == this.empty) {
         blockData = (T[])((Object[])this.blockConstructor.apply(256));
         this.blockMap[block] = blockData;
         blockData[offset] = value;
         return null;
      } else {
         Object previous = blockData[offset];
         blockData[offset] = value;
         return previous;
      }
   }

   public Object computeIfAbsent(final int codepoint, final IntFunction mapper) {
      int block = codepoint >> 8;
      int offset = codepoint & 255;
      Object[] blockData = (T[])this.blockMap[block];
      Object current = blockData[offset];
      if (current != null) {
         return current;
      } else {
         if (blockData == this.empty) {
            blockData = (T[])((Object[])this.blockConstructor.apply(256));
            this.blockMap[block] = blockData;
         }

         Object result = (T)mapper.apply(codepoint);
         blockData[offset] = result;
         return result;
      }
   }

   public @Nullable Object remove(final int codepoint) {
      int block = codepoint >> 8;
      int offset = codepoint & 255;
      Object[] blockData = (T[])this.blockMap[block];
      if (blockData == this.empty) {
         return null;
      } else {
         Object previous = blockData[offset];
         blockData[offset] = (T)null;
         return previous;
      }
   }

   public void forEach(final CodepointMap.Output output) {
      for(int block = 0; block < this.blockMap.length; ++block) {
         Object[] blockData = (T[])this.blockMap[block];
         if (blockData != this.empty) {
            for(int offset = 0; offset < blockData.length; ++offset) {
               Object value = blockData[offset];
               if (value != null) {
                  int codepoint = block << 8 | offset;
                  output.accept(codepoint, value);
               }
            }
         }
      }

   }

   public IntSet keySet() {
      IntOpenHashSet result = new IntOpenHashSet();
      this.forEach((codepoint, value) -> result.add(codepoint));
      return result;
   }

   @FunctionalInterface
   public interface Output {
      void accept(int codepoint, Object value);
   }
}
