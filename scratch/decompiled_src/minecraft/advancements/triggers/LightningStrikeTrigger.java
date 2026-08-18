package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LightningStrikeTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return LightningStrikeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final LightningBolt lightning, final List entitiesAround) {
      List entitiesAroundContexts = (List)entitiesAround.stream().map((v) -> EntityPredicate.createContext(player, v)).collect(Collectors.toList());
      LootContext lightningContext = EntityPredicate.createContext(player, lightning);
      this.trigger(player, (t) -> t.matches(lightningContext, entitiesAroundContexts));
   }

   public static record TriggerInstance(Optional player, Optional lightning, Optional bystander) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(LightningStrikeTrigger.TriggerInstance::player), LootItemCondition.CODEC.optionalFieldOf("lightning").forGetter(LightningStrikeTrigger.TriggerInstance::lightning), LootItemCondition.CODEC.optionalFieldOf("bystander").forGetter(LightningStrikeTrigger.TriggerInstance::bystander)).apply(i, LightningStrikeTrigger.TriggerInstance::new));

      public static Criterion lightningStrike(final Optional lightning, final Optional bystander) {
         return CriteriaTriggers.LIGHTNING_STRIKE.createCriterion(new LightningStrikeTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(lightning), EntityPredicate.wrap(bystander)));
      }

      public boolean matches(final LootContext bolt, final List entitiesAround) {
         if (this.lightning.isPresent() && !((LootItemCondition)((Holder)this.lightning.get()).value()).test(bolt)) {
            return false;
         } else {
            return !this.bystander.isPresent() || !entitiesAround.stream().noneMatch((Predicate)((Holder)this.bystander.get()).value());
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "lightning", this.lightning);
         Validatable.validateHolder(validator.entityContext(), "bystander", this.bystander);
      }
   }
}
