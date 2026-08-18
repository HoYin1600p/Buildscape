package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.Nullable;

public interface ValueOutput {
   void store(String name, Codec codec, Object value);

   void storeNullable(String name, Codec codec, @Nullable Object value);

   /** @deprecated */
   @Deprecated
   void store(MapCodec codec, Object value);

   void putBoolean(String name, boolean value);

   void putByte(String name, byte value);

   void putShort(String name, short value);

   void putInt(String name, int value);

   void putLong(String name, long value);

   void putFloat(String name, float value);

   void putDouble(String name, double value);

   void putString(String name, String value);

   void putIntArray(String name, int[] value);

   ValueOutput child(String name);

   ValueOutput.ValueOutputList childrenList(String name);

   ValueOutput.TypedOutputList list(String name, Codec codec);

   void discard(String name);

   boolean isEmpty();

   public interface TypedOutputList {
      void add(Object value);

      boolean isEmpty();
   }

   public interface ValueOutputList {
      ValueOutput addChild();

      void discardLast();

      boolean isEmpty();
   }
}
