package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class BrewedPotionTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return BrewedPotionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final PotionContents potion) {
      this.trigger(player, (t) -> t.matches(potion));
   }

   public static record TriggerInstance(Optional player, Optional potion) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(BrewedPotionTrigger.TriggerInstance::player), PotionsPredicate.CODEC.optionalFieldOf("potion").forGetter(BrewedPotionTrigger.TriggerInstance::potion)).apply(i, BrewedPotionTrigger.TriggerInstance::new));

      public static Criterion brewedPotion() {
         return CriteriaTriggers.BREWED_POTION.createCriterion(new BrewedPotionTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public boolean matches(final PotionContents potion) {
         return !this.potion.isPresent() || ((PotionsPredicate)this.potion.get()).matches(potion);
      }
   }
}
