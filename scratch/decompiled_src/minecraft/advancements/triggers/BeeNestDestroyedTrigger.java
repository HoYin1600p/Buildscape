package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class BeeNestDestroyedTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return BeeNestDestroyedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final BlockState state, final ItemStack itemStack, final int numBeesInside) {
      this.trigger(player, (t) -> t.matches(state, itemStack, numBeesInside));
   }

   public static record TriggerInstance(Optional player, Optional block, Optional state, Optional item, MinMaxBounds.Ints beesInside) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(BeeNestDestroyedTrigger.TriggerInstance::player), RegistryCodecs.holderSet(Registries.BLOCK).optionalFieldOf("blocks").forGetter(BeeNestDestroyedTrigger.TriggerInstance::block), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(BeeNestDestroyedTrigger.TriggerInstance::state), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(BeeNestDestroyedTrigger.TriggerInstance::item), MinMaxBounds.Ints.CODEC.optionalFieldOf("num_bees_inside", MinMaxBounds.Ints.ANY).forGetter(BeeNestDestroyedTrigger.TriggerInstance::beesInside)).apply(i, BeeNestDestroyedTrigger.TriggerInstance::new));

      public static Criterion destroyedBeeNest(final HolderGetter blocks, final Block block, final ItemPredicate.Builder itemPredicate, final MinMaxBounds.Ints numBeesInside) {
         return destroyedBeeNest(HolderSet.direct(block.builtInRegistryHolder()), itemPredicate, numBeesInside);
      }

      public static Criterion destroyedBeeNest(final HolderSet block, final ItemPredicate.Builder itemPredicate, final MinMaxBounds.Ints numBeesInside) {
         return CriteriaTriggers.BEE_NEST_DESTROYED.createCriterion(new BeeNestDestroyedTrigger.TriggerInstance(Optional.empty(), Optional.of(block), Optional.empty(), Optional.of(itemPredicate.build()), numBeesInside));
      }

      public boolean matches(final BlockState state, final ItemStack itemStack, final int numBeesInside) {
         if (this.block.isPresent() && !state.is((HolderSet)this.block.get())) {
            return false;
         } else if (this.state.isPresent() && !((StatePropertiesPredicate)this.state.get()).matches(state)) {
            return false;
         } else {
            return this.item.isPresent() && !((ItemPredicate)this.item.get()).test((ItemInstance)itemStack) ? false : this.beesInside.matches(numBeesInside);
         }
      }
   }
}
