package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EnterBlockTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return EnterBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final BlockState state) {
      this.trigger(player, (t) -> t.matches(state));
   }

   public static record TriggerInstance(Optional player, Optional block, Optional state) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(EnterBlockTrigger.TriggerInstance::player), RegistryCodecs.holderSet(Registries.BLOCK).optionalFieldOf("blocks").forGetter(EnterBlockTrigger.TriggerInstance::block), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(EnterBlockTrigger.TriggerInstance::state)).apply(i, EnterBlockTrigger.TriggerInstance::new));

      public static Criterion entersBlock(final HolderGetter blocks, final Block block) {
         return entersBlock(HolderSet.direct(block.builtInRegistryHolder()));
      }

      public static Criterion entersBlock(final HolderSet block) {
         return CriteriaTriggers.ENTER_BLOCK.createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(block), Optional.empty()));
      }

      public boolean matches(final BlockState state) {
         if (this.block.isPresent() && !state.is((HolderSet)this.block.get())) {
            return false;
         } else {
            return !this.state.isPresent() || ((StatePropertiesPredicate)this.state.get()).matches(state);
         }
      }
   }
}
