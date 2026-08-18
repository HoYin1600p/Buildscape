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
import org.jspecify.annotations.Nullable;

public class PickedUpItemTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return PickedUpItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack itemStack, final @Nullable Entity entity) {
      LootContext context = EntityPredicate.createContext(player, entity);
      this.trigger(player, (t) -> t.matches(player, itemStack, context));
   }

   public static record TriggerInstance(Optional player, Optional item, Optional entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(PickedUpItemTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PickedUpItemTrigger.TriggerInstance::item), LootItemCondition.CODEC.optionalFieldOf("entity").forGetter(PickedUpItemTrigger.TriggerInstance::entity)).apply(i, PickedUpItemTrigger.TriggerInstance::new));

      public static Criterion thrownItemPickedUpByEntity(final Optional player, final Optional item, final Optional entity) {
         return CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.createCriterion(new PickedUpItemTrigger.TriggerInstance(player, item, entity));
      }

      public static Criterion thrownItemPickedUpByPlayer(final Optional player, final Optional item, final Optional entity) {
         return CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.createCriterion(new PickedUpItemTrigger.TriggerInstance(player, item, entity));
      }

      public boolean matches(final ServerPlayer player, final ItemStack itemStack, final LootContext pickedUpBy) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test((ItemInstance)itemStack)) {
            return false;
         } else {
            return !this.entity.isPresent() || ((LootItemCondition)((Holder)this.entity.get()).value()).test(pickedUpBy);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "entity", this.entity);
      }
   }
}
