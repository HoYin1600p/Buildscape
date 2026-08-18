package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemCondition extends LootContextUser, Predicate {
   Codec DIRECT_CODEC = BuiltInRegistries.LOOT_CONDITION_TYPE.byNameCodec().dispatch(LootItemCondition::codec, (c) -> c);
   Codec CODEC = RegistryCodecs.holder(Registries.PREDICATE, DIRECT_CODEC);
   Codec LIST_CODEC = RegistryCodecs.holderSet(Registries.PREDICATE, DIRECT_CODEC);

   MapCodec codec();

   @FunctionalInterface
   public interface Builder {
      LootItemCondition build();

      default LootItemCondition.Builder invert() {
         return InvertedLootItemCondition.invert(this);
      }

      default AnyOfCondition.Builder or(final LootItemCondition.Builder other) {
         return AnyOfCondition.anyOf(this, other);
      }

      default AllOfCondition.Builder and(final LootItemCondition.Builder other) {
         return AllOfCondition.allOf(this, other);
      }
   }
}
