package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return CuredZombieVillagerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Zombie zombie, final Villager villager) {
      LootContext zombieContext = EntityPredicate.createContext(player, zombie);
      LootContext villagerContext = EntityPredicate.createContext(player, villager);
      this.trigger(player, (t) -> t.matches(zombieContext, villagerContext));
   }

   public static record TriggerInstance(Optional player, Optional zombie, Optional villager) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(CuredZombieVillagerTrigger.TriggerInstance::player), LootItemCondition.CODEC.optionalFieldOf("zombie").forGetter(CuredZombieVillagerTrigger.TriggerInstance::zombie), LootItemCondition.CODEC.optionalFieldOf("villager").forGetter(CuredZombieVillagerTrigger.TriggerInstance::villager)).apply(i, CuredZombieVillagerTrigger.TriggerInstance::new));

      public static Criterion curedZombieVillager() {
         return CriteriaTriggers.CURED_ZOMBIE_VILLAGER.createCriterion(new CuredZombieVillagerTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public boolean matches(final LootContext zombie, final LootContext villager) {
         if (this.zombie.isPresent() && !((LootItemCondition)((Holder)this.zombie.get()).value()).test(zombie)) {
            return false;
         } else {
            return !this.villager.isPresent() || ((LootItemCondition)((Holder)this.villager.get()).value()).test(villager);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "zombie", this.zombie);
         Validatable.validateHolder(validator.entityContext(), "villager", this.villager);
      }
   }
}
