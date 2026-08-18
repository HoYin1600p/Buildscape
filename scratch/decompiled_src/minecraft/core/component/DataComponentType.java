package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public interface DataComponentType {
   Codec CODEC = Codec.lazyInitialized(() -> BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec());
   StreamCodec STREAM_CODEC = StreamCodec.recursive((c) -> ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE));
   Codec PERSISTENT_CODEC = CODEC.validate((type) -> type.isTransient() ? DataResult.error(() -> "Encountered transient component " + String.valueOf(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type))) : DataResult.success(type));
   Codec VALUE_MAP_CODEC = Codec.dispatchedMap(PERSISTENT_CODEC, DataComponentType::codecOrThrow);

   static DataComponentType.Builder builder() {
      return new DataComponentType.Builder();
   }

   @Nullable Codec codec();

   default Codec codecOrThrow() {
      Codec codec = this.codec();
      if (codec == null) {
         throw new IllegalStateException(String.valueOf(this) + " is not a persistent component");
      } else {
         return codec;
      }
   }

   default boolean isTransient() {
      return this.codec() == null;
   }

   boolean ignoreSwapAnimation();

   StreamCodec streamCodec();

   public static class Builder {
      private @Nullable Codec codec;
      private @Nullable StreamCodec streamCodec;
      private boolean cacheEncoding;
      private boolean ignoreSwapAnimation;

      public DataComponentType.Builder persistent(final Codec codec) {
         this.codec = codec;
         return this;
      }

      public DataComponentType.Builder networkSynchronized(final StreamCodec streamCodec) {
         this.streamCodec = streamCodec;
         return this;
      }

      public DataComponentType.Builder cacheEncoding() {
         this.cacheEncoding = true;
         return this;
      }

      public DataComponentType build() {
         StreamCodec streamCodec = (StreamCodec)Objects.requireNonNullElseGet(this.streamCodec, () -> ByteBufCodecs.fromCodecWithRegistries((Codec)Objects.requireNonNull(this.codec, "Missing Codec for component")));
         Codec cachingCodec = this.cacheEncoding && this.codec != null ? DataComponents.ENCODER_CACHE.wrap(this.codec) : this.codec;
         return new DataComponentType.Builder.SimpleType(cachingCodec, streamCodec, this.ignoreSwapAnimation);
      }

      public DataComponentType.Builder ignoreSwapAnimation() {
         this.ignoreSwapAnimation = true;
         return this;
      }

      private static class SimpleType implements DataComponentType {
         private final @Nullable Codec codec;
         private final StreamCodec streamCodec;
         private final boolean ignoreSwapAnimation;

         private SimpleType(final @Nullable Codec codec, final StreamCodec streamCodec, final boolean ignoreSwapAnimation) {
            this.codec = codec;
            this.streamCodec = streamCodec;
            this.ignoreSwapAnimation = ignoreSwapAnimation;
         }

         public boolean ignoreSwapAnimation() {
            return this.ignoreSwapAnimation;
         }

         public @Nullable Codec codec() {
            return this.codec;
         }

         public StreamCodec streamCodec() {
            return this.streamCodec;
         }

         public String toString() {
            return Util.getRegisteredName(BuiltInRegistries.DATA_COMPONENT_TYPE, this);
         }
      }
   }
}
