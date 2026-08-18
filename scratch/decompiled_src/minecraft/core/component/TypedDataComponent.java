package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TypedDataComponent(DataComponentType type, Object value) {
   public static final StreamCodec STREAM_CODEC = new StreamCodec() {
      public TypedDataComponent decode(final RegistryFriendlyByteBuf input) {
         DataComponentType type = (DataComponentType)DataComponentType.STREAM_CODEC.decode(input);
         return decodeTyped(input, type);
      }

      private static TypedDataComponent decodeTyped(final RegistryFriendlyByteBuf input, final DataComponentType type) {
         return new TypedDataComponent(type, type.streamCodec().decode(input));
      }

      public void encode(final RegistryFriendlyByteBuf output, final TypedDataComponent value) {
         encodeCap(output, value);
      }

      private static void encodeCap(final RegistryFriendlyByteBuf output, final TypedDataComponent component) {
         DataComponentType.STREAM_CODEC.encode(output, component.type());
         component.type().streamCodec().encode(output, component.value());
      }
   };

   static TypedDataComponent fromEntryUnchecked(final Map.Entry entry) {
      return createUnchecked((DataComponentType)entry.getKey(), entry.getValue());
   }

   public static TypedDataComponent createUnchecked(final DataComponentType type, final Object value) {
      return new TypedDataComponent(type, value);
   }

   public void applyTo(final PatchedDataComponentMap components) {
      components.set(this.type, this.value);
   }

   public DataResult encodeValue(final DynamicOps ops) {
      Codec codec = this.type.codec();
      return codec == null ? DataResult.error(() -> "Component of type " + String.valueOf(this.type) + " is not encodable") : codec.encodeStart(ops, this.value);
   }

   public String toString() {
      return String.valueOf(this.type) + "=>" + String.valueOf(this.value);
   }
}
