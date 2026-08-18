package net.minecraft.network.syncher;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface EntityDataSerializer {
   StreamCodec codec();

   default EntityDataAccessor createAccessor(final int id) {
      return new EntityDataAccessor(id, this);
   }

   Object copy(Object value);

   static EntityDataSerializer forValueType(final StreamCodec codec) {
      return () -> codec;
   }

   public interface ForValueType extends EntityDataSerializer {
      default Object copy(final Object value) {
         return value;
      }
   }
}
