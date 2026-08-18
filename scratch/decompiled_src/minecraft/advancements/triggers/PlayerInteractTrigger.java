package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class PlayerInteractTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return PlayerInteractTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack itemStack, final Entity interactedWith) {
      LootContext context = EntityPredicate.createContext(player, interactedWith);
      this.trigger(player, (t) -> t.matches(itemStack, context));
   }

   public static record TriggerInstance(Optional player, Optional item, Optional entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(PlayerInteractTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PlayerInteractTrigger.TriggerInstance::item), LootItemCondition.CODEC.optionalFieldOf("entity").forGetter(PlayerInteractTrigger.TriggerInstance::entity)).apply(i, PlayerInteractTrigger.TriggerInstance::new));

      public static Criterion itemUsedOnEntity(final Optional player, final ItemPredicate.Builder item, final Optional entity) {
         return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new PlayerInteractTrigger.TriggerInstance(player, Optional.of(item.build()), entity));
      }

      public static Criterion equipmentSheared(final Optional player, final ItemPredicate.Builder item, final Optional entity) {
         return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.createCriterion(new PlayerInteractTrigger.TriggerInstance(player, Optional.of(item.build()), entity));
      }

      public static Criterion equipmentSheared(final ItemPredicate.Builder item, final Optional entity) {
         return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.createCriterion(new PlayerInteractTrigger.TriggerInstance(Optional.empty(), Optional.of(item.build()), entity));
      }

      public static Criterion itemUsedOnEntity(final ItemPredicate.Builder item, final Optional entity) {
         return itemUsedOnEntity(Optional.empty(), item, entity);
      }

      public boolean matches(final ItemStack itemStack, final LootContext interactedWith) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test((ItemInstance)itemStack)) {
            return false;
         } else {
            return this.entity.isEmpty() || ((LootItemCondition)((Holder)this.entity.get()).value()).test(interactedWith);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "entity", this.entity);
      }
   }
}
