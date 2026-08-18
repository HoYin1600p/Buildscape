package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ChangeDimensionTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return ChangeDimensionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ResourceKey from, final ResourceKey to) {
      this.trigger(player, (t) -> t.matches(from, to));
   }

   public static record TriggerInstance(Optional player, Optional from, Optional to) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(ChangeDimensionTrigger.TriggerInstance::player), ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("from").forGetter(ChangeDimensionTrigger.TriggerInstance::from), ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("to").forGetter(ChangeDimensionTrigger.TriggerInstance::to)).apply(i, ChangeDimensionTrigger.TriggerInstance::new));

      public static Criterion changedDimension() {
         return CriteriaTriggers.CHANGED_DIMENSION.createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion changedDimension(final ResourceKey from, final ResourceKey to) {
         return CriteriaTriggers.CHANGED_DIMENSION.createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.of(from), Optional.of(to)));
      }

      public static Criterion changedDimensionTo(final ResourceKey to) {
         return CriteriaTriggers.CHANGED_DIMENSION.createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(to)));
      }

      public static Criterion changedDimensionFrom(final ResourceKey from) {
         return CriteriaTriggers.CHANGED_DIMENSION.createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.of(from), Optional.empty()));
      }

      public boolean matches(final ResourceKey from, final ResourceKey to) {
         if (this.from.isPresent() && this.from.get() != from) {
            return false;
         } else {
            return !this.to.isPresent() || this.to.get() == to;
         }
      }
   }
}
