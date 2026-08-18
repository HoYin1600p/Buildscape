package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class UsedTotemTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return UsedTotemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack itemStack) {
      this.trigger(player, (t) -> t.matches(itemStack));
   }

   public static record TriggerInstance(Optional player, Optional item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(UsedTotemTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsedTotemTrigger.TriggerInstance::item)).apply(i, UsedTotemTrigger.TriggerInstance::new));

      public static Criterion usedTotem(final ItemPredicate item) {
         return CriteriaTriggers.USED_TOTEM.createCriterion(new UsedTotemTrigger.TriggerInstance(Optional.empty(), Optional.of(item)));
      }

      public static Criterion usedTotem(final HolderGetter items, final ItemLike itemlike) {
         return CriteriaTriggers.USED_TOTEM.createCriterion(new UsedTotemTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(items, itemlike).build())));
      }

      public boolean matches(final ItemStack itemStack) {
         return this.item.isEmpty() || ((ItemPredicate)this.item.get()).test((ItemInstance)itemStack);
      }
   }
}
