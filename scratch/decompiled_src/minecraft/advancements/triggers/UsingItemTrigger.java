package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class UsingItemTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return UsingItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack item) {
      this.trigger(player, (t) -> t.matches(item));
   }

   public static record TriggerInstance(Optional player, Optional item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(UsingItemTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsingItemTrigger.TriggerInstance::item)).apply(i, UsingItemTrigger.TriggerInstance::new));

      public static Criterion lookingAt(final EntityPredicate.Builder player, final ItemPredicate.Builder with) {
         return CriteriaTriggers.USING_ITEM.createCriterion(new UsingItemTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(player)), Optional.of(with.build())));
      }

      public boolean matches(final ItemStack item) {
         return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test((ItemInstance)item);
      }
   }
}
