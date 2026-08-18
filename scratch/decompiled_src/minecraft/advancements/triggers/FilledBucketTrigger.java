package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FilledBucketTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return FilledBucketTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack item) {
      this.trigger(player, (t) -> t.matches(item));
   }

   public static record TriggerInstance(Optional player, Optional item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(FilledBucketTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FilledBucketTrigger.TriggerInstance::item)).apply(i, FilledBucketTrigger.TriggerInstance::new));

      public static Criterion filledBucket(final ItemPredicate.Builder item) {
         return CriteriaTriggers.FILLED_BUCKET.createCriterion(new FilledBucketTrigger.TriggerInstance(Optional.empty(), Optional.of(item.build())));
      }

      public boolean matches(final ItemStack item) {
         return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test((ItemInstance)item);
      }
   }
}
