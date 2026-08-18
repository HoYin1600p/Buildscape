package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;

public interface ValueInput {
   Optional read(String name, Codec codec);

   /** @deprecated */
   @Deprecated
   Optional read(MapCodec codec);

   Optional child(String name);

   ValueInput childOrEmpty(String name);

   Optional childrenList(String name);

   ValueInput.ValueInputList childrenListOrEmpty(String name);

   Optional list(String name, Codec codec);

   ValueInput.TypedInputList listOrEmpty(String name, Codec codec);

   boolean getBooleanOr(String name, boolean defaultValue);

   byte getByteOr(String name, byte defaultValue);

   int getShortOr(String name, short defaultValue);

   Optional getInt(String name);

   int getIntOr(String name, int defaultValue);

   long getLongOr(String name, long defaultValue);

   Optional getLong(String name);

   float getFloatOr(String name, float defaultValue);

   double getDoubleOr(String name, double defaultValue);

   Optional getString(String name);

   String getStringOr(String name, String defaultValue);

   Optional getIntArray(String name);

   /** @deprecated */
   @Deprecated
   HolderLookup.Provider lookup();

   public interface TypedInputList extends Iterable {
      boolean isEmpty();

      Stream stream();
   }

   public interface ValueInputList extends Iterable {
      boolean isEmpty();

      Stream stream();
   }
}
