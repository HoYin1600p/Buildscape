package net.minecraft.server.packs.metadata;

import com.mojang.serialization.Codec;
import java.util.Optional;

public record MetadataSectionType(String name, Codec codec) {
   public MetadataSectionType.WithValue withValue(final Object value) {
      return new MetadataSectionType.WithValue(this, value);
   }

   public static record WithValue(MetadataSectionType type, Object value) {
      public Optional unwrapToType(final MetadataSectionType type) {
         return type == this.type ? Optional.of(this.value) : Optional.empty();
      }
   }
}
