package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return LootTableTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ResourceKey lootTable) {
      this.trigger(player, (t) -> t.matches(lootTable));
   }

   public static record TriggerInstance(Optional player, HolderSet lootTable) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(LootTableTrigger.TriggerInstance::player), LootTable.LIST_CODEC.fieldOf("loot_tables").forGetter(LootTableTrigger.TriggerInstance::lootTable)).apply(i, LootTableTrigger.TriggerInstance::new));

      public static Criterion lootTableUsed(final Holder lootTable) {
         return lootTableUsed(HolderSet.direct(lootTable));
      }

      public static Criterion lootTableUsed(final HolderSet lootTable) {
         return CriteriaTriggers.GENERATE_LOOT.createCriterion(new LootTableTrigger.TriggerInstance(Optional.empty(), lootTable));
      }

      public boolean matches(final ResourceKey lootTable) {
         return this.lootTable.stream().anyMatch((table) -> table.is(lootTable));
      }
   }
}
