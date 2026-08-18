package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

public class Cloner {
   private final Codec directCodec;

   private Cloner(final Codec directCodec) {
      this.directCodec = directCodec;
   }

   public Object clone(final Object value, final HolderLookup.Provider from, final HolderLookup.Provider to) {
      DynamicOps sourceOps = from.createSerializationContext(JavaOps.INSTANCE);
      DynamicOps targetOps = to.createSerializationContext(JavaOps.INSTANCE);
      Object serialized = this.directCodec.encodeStart(sourceOps, value).getOrThrow((error) -> new IllegalStateException("Failed to encode: " + error));
      return this.directCodec.parse(targetOps, serialized).getOrThrow((error) -> new IllegalStateException("Failed to decode: " + error));
   }

   public static class Factory {
      private final Map codecs = new HashMap();

      public Cloner.Factory addCodec(final ResourceKey key, final Codec codec) {
         this.codecs.put(key, new Cloner(codec));
         return this;
      }

      public @Nullable Cloner cloner(final ResourceKey key) {
         return (Cloner)this.codecs.get(key);
      }
   }
}
