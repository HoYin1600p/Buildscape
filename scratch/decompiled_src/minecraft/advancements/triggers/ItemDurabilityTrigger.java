package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return ItemDurabilityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack itemStack, final int newDurability) {
      this.trigger(player, (t) -> t.matches(itemStack, newDurability));
   }

   public static record TriggerInstance(Optional player, Optional item, MinMaxBounds.Ints durability, MinMaxBounds.Ints delta) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(ItemDurabilityTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ItemDurabilityTrigger.TriggerInstance::item), MinMaxBounds.Ints.CODEC.optionalFieldOf("durability", MinMaxBounds.Ints.ANY).forGetter(ItemDurabilityTrigger.TriggerInstance::durability), MinMaxBounds.Ints.CODEC.optionalFieldOf("delta", MinMaxBounds.Ints.ANY).forGetter(ItemDurabilityTrigger.TriggerInstance::delta)).apply(i, ItemDurabilityTrigger.TriggerInstance::new));

      public static Criterion changedDurability(final Optional item, final MinMaxBounds.Ints durability) {
         return changedDurability(Optional.empty(), item, durability);
      }

      public static Criterion changedDurability(final Optional player, final Optional item, final MinMaxBounds.Ints durability) {
         return CriteriaTriggers.ITEM_DURABILITY_CHANGED.createCriterion(new ItemDurabilityTrigger.TriggerInstance(player, item, durability, MinMaxBounds.Ints.ANY));
      }

      public boolean matches(final ItemStack itemStack, final int newDurability) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test((ItemInstance)itemStack)) {
            return false;
         } else if (!this.durability.matches(itemStack.getMaxDamage() - newDurability)) {
            return false;
         } else {
            return this.delta.matches(itemStack.getDamageValue() - newDurability);
         }
      }
   }
}
