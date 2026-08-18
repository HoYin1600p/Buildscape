package net.minecraft.core.registries.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public final class RegistryFileCodec implements Codec {
   private final ResourceKey registryKey;
   private final Codec elementCodec;
   private final boolean allowInline;

   public static Codec create(final ResourceKey registryKey, final Codec elementCodec, final boolean allowInline) {
      return new RegistryFileCodec(registryKey, elementCodec, allowInline);
   }

   private RegistryFileCodec(final ResourceKey registryKey, final Codec elementCodec, final boolean allowInline) {
      this.registryKey = registryKey;
      this.elementCodec = elementCodec;
      this.allowInline = allowInline;
   }

   public DataResult encode(final Holder input, final DynamicOps ops, final Object prefix) {
      if (ops instanceof RegistryOps registryOps) {
         Optional maybeOwner = registryOps.getter(this.registryKey);
         if (maybeOwner.isPresent()) {
            if (!input.canSerializeIn((HolderOwner)maybeOwner.get())) {
               return DataResult.error(() -> "Element " + String.valueOf(input) + " is not valid in current registry set");
            }

            return (DataResult)input.unwrap().map((id) -> Identifier.CODEC.encode(id.identifier(), ops, prefix), (value) -> this.elementCodec.encode(value, ops, prefix));
         }
      }

      return this.elementCodec.encode(input.value(), ops, prefix);
   }

   public DataResult decode(final DynamicOps ops, final Object input) {
      if (ops instanceof RegistryOps registryOps) {
         DataResult decoded = Identifier.CODEC.decode(ops, input);
         if (decoded.result().isEmpty()) {
            return !this.allowInline ? DataResult.error(() -> "Inline definitions not allowed here") : this.elementCodec.decode(ops, input).map((p) -> p.mapFirst(Holder::direct));
         } else {
            Optional maybeLookup = registryOps.getter(this.registryKey);
            if (maybeLookup.isEmpty()) {
               return DataResult.error(() -> "Registry does not exist: " + String.valueOf(this.registryKey.identifier()));
            } else {
               HolderGetter lookup = (HolderGetter)maybeLookup.get();
               Pair pair = (Pair)decoded.result().get();
               ResourceKey elementKey = ResourceKey.create(this.registryKey, (Identifier)pair.getFirst());
               return ((DataResult)lookup.get(elementKey).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Failed to get element " + String.valueOf(elementKey.identifier()) + " from registry " + String.valueOf(elementKey.registry())))).map((h) -> Pair.of(h, pair.getSecond())).setLifecycle(Lifecycle.stable());
            }
         }
      } else {
         return this.elementCodec.decode(ops, input).map((p) -> p.mapFirst(Holder::direct));
      }
   }

   public String toString() {
      return "RegistryFileCodec[" + String.valueOf(this.registryKey) + " " + String.valueOf(this.elementCodec) + "]";
   }
}
