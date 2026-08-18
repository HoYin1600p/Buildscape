package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.DamagePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class PlayerHurtEntityTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return PlayerHurtEntityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Entity victim, final DamageSource source, final float originalDamage, final float actualDamage, final boolean blocked) {
      LootContext victimContext = EntityPredicate.createContext(player, victim);
      this.trigger(player, (t) -> t.matches(player, victimContext, source, originalDamage, actualDamage, blocked));
   }

   public static record TriggerInstance(Optional player, Optional damage, Optional entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(PlayerHurtEntityTrigger.TriggerInstance::player), DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(PlayerHurtEntityTrigger.TriggerInstance::damage), LootItemCondition.CODEC.optionalFieldOf("entity").forGetter(PlayerHurtEntityTrigger.TriggerInstance::entity)).apply(i, PlayerHurtEntityTrigger.TriggerInstance::new));

      public static Criterion playerHurtEntity() {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion playerHurtEntityWithDamage(final Optional damage) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), damage, Optional.empty()));
      }

      public static Criterion playerHurtEntityWithDamage(final DamagePredicate.Builder damage) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.of(damage.build()), Optional.empty()));
      }

      public static Criterion playerHurtEntity(final Optional entity) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.empty(), EntityPredicate.wrap(entity)));
      }

      public static Criterion playerHurtEntity(final Optional damage, final Optional entity) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), damage, EntityPredicate.wrap(entity)));
      }

      public static Criterion playerHurtEntity(final DamagePredicate.Builder damage, final Optional entity) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.of(damage.build()), EntityPredicate.wrap(entity)));
      }

      public boolean matches(final ServerPlayer player, final LootContext victim, final DamageSource source, final float originalDamage, final float actualDamage, final boolean blocked) {
         if (this.damage.isPresent() && !((DamagePredicate)this.damage.get()).matches(player, source, originalDamage, actualDamage, blocked)) {
            return false;
         } else {
            return !this.entity.isPresent() || ((LootItemCondition)((Holder)this.entity.get()).value()).test(victim);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "entity", this.entity);
      }
   }
}
