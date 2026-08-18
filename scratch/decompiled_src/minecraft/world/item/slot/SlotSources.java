package net.minecraft.world.item.slot;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;

public interface SlotSources {
   Codec TYPED_CODEC = BuiltInRegistries.SLOT_SOURCE_TYPE.byNameCodec().dispatch(SlotSource::codec, (c) -> c);
   Codec DIRECT_CODEC = Codec.lazyInitialized(() -> Codec.either(TYPED_CODEC, GroupSlotSource.INLINE_CODEC).xmap((typedOrInline) -> (SlotSource)typedOrInline.map((e) -> e, (e) -> e), (slotSource) -> {
         Either var10000;
         if (slotSource instanceof GroupSlotSource composite) {
            var10000 = Either.right(composite);
         } else {
            var10000 = Either.left(slotSource);
         }

         return var10000;
      }));
   Codec CODEC = RegistryCodecs.holder(Registries.SLOT_SOURCE, DIRECT_CODEC);
   Codec LIST_CODEC = RegistryCodecs.holderSet(Registries.SLOT_SOURCE, DIRECT_CODEC);

   static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "group", GroupSlotSource.MAP_CODEC);
      Registry.register(registry, "filtered", FilteredSlotSource.MAP_CODEC);
      Registry.register(registry, "limit_slots", LimitSlotSource.MAP_CODEC);
      Registry.register(registry, "slot_range", RangeSlotSource.MAP_CODEC);
      Registry.register(registry, "contents", ContentsSlotSource.MAP_CODEC);
      return (MapCodec)Registry.register(registry, "empty", EmptySlotSource.MAP_CODEC);
   }
}
