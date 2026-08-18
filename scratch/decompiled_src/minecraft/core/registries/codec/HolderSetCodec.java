package net.minecraft.core.registries.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

public class HolderSetCodec implements Codec {
   private final ResourceKey registryKey;
   private final Codec tagKeyOrValuesCodec;

   private static Codec directCodec(final Codec elementCodec, final boolean alwaysUseList) {
      Codec listCodec = elementCodec.listOf();
      return alwaysUseList ? listCodec : ExtraCodecs.compactListCodec(elementCodec, listCodec);
   }

   public static Codec create(final ResourceKey registryKey, final Codec elementCodec, final boolean alwaysUseList) {
      return new HolderSetCodec(registryKey, elementCodec, alwaysUseList);
   }

   private HolderSetCodec(final ResourceKey registryKey, final Codec elementCodec, final boolean alwaysUseList) {
      this.registryKey = registryKey;
      this.tagKeyOrValuesCodec = Codec.either(TagKey.hashedCodec(registryKey), directCodec(elementCodec, alwaysUseList));
   }

   public DataResult decode(final DynamicOps ops, final Object input) {
      return this.tagKeyOrValuesCodec.decode(ops, input).flatMap((tagKeyOrValues) -> {
         DataResult result = (DataResult)((Either)tagKeyOrValues.getFirst()).map((tagKey) -> {
            if (ops instanceof RegistryOps registryOps) {
               Optional maybeRegistry = registryOps.getter(this.registryKey);
               return maybeRegistry.isPresent() ? lookupTag((HolderGetter)maybeRegistry.get(), tagKey) : DataResult.error(() -> "Registry " + String.valueOf(this.registryKey.identifier()) + " is not available in this context");
            } else {
               return DataResult.error(() -> "Registries are not available in this context");
            }
         }, (values) -> DataResult.success(HolderSet.direct(values)));
         return result.map((holders) -> Pair.of(holders, tagKeyOrValues.getSecond()));
      });
   }

   private static DataResult lookupTag(final HolderGetter registry, final TagKey key) {
      return (DataResult)registry.get(key).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Missing tag: '" + String.valueOf(key.location()) + "' in '" + String.valueOf(key.registry().identifier()) + "'"));
   }

   public DataResult encode(final HolderSet input, final DynamicOps ops, final Object prefix) {
      if (input instanceof HolderSet.Named named) {
         if (ops instanceof RegistryOps registryOps) {
            Optional maybeOwner = registryOps.getter(this.registryKey);
            if (maybeOwner.isPresent()) {
               if (!named.canSerializeIn((HolderOwner)maybeOwner.get())) {
                  return DataResult.error(() -> "HolderSet " + String.valueOf(named) + " is not valid in current registry set");
               }

               return this.tagKeyOrValuesCodec.encode(Either.left(named.key()), ops, prefix);
            }

            return DataResult.error(() -> "Registry " + String.valueOf(this.registryKey.identifier()) + " is not available in this context");
         }
      }

      return this.tagKeyOrValuesCodec.encode(Either.right(input.stream().toList()), ops, prefix);
   }
}
