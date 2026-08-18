package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SpearMobsTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return SpearMobsTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final int number) {
      this.trigger(player, (t) -> t.matches(number));
   }

   public static record TriggerInstance(Optional player, Optional count) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(SpearMobsTrigger.TriggerInstance::player), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count").forGetter(SpearMobsTrigger.TriggerInstance::count)).apply(i, SpearMobsTrigger.TriggerInstance::new));

      public static Criterion spearMobs(final int requiredCount) {
         return CriteriaTriggers.SPEAR_MOBS_TRIGGER.createCriterion(new SpearMobsTrigger.TriggerInstance(Optional.empty(), Optional.of(requiredCount)));
      }

      public boolean matches(final int requiredCount) {
         return this.count.isEmpty() || requiredCount >= this.count.get();
      }
   }
}
