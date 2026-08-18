package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SummonedEntityTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return SummonedEntityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Entity entity) {
      LootContext context = EntityPredicate.createContext(player, entity);
      this.trigger(player, (t) -> t.matches(context));
   }

   public static record TriggerInstance(Optional player, Optional entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(SummonedEntityTrigger.TriggerInstance::player), LootItemCondition.CODEC.optionalFieldOf("entity").forGetter(SummonedEntityTrigger.TriggerInstance::entity)).apply(i, SummonedEntityTrigger.TriggerInstance::new));

      public static Criterion summonedEntity(final EntityPredicate.Builder predicate) {
         return CriteriaTriggers.SUMMONED_ENTITY.createCriterion(new SummonedEntityTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(predicate))));
      }

      public boolean matches(final LootContext entity) {
         return this.entity.isEmpty() || ((LootItemCondition)((Holder)this.entity.get()).value()).test(entity);
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "entity", this.entity);
      }
   }
}
