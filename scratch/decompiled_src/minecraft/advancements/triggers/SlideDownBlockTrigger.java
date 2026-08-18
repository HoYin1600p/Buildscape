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

public class SlideDownBlockTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return SlideDownBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final BlockState state) {
      this.trigger(player, (t) -> t.matches(state));
   }

   public static record TriggerInstance(Optional player, Optional block, Optional state) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(SlideDownBlockTrigger.TriggerInstance::player), RegistryCodecs.holderSet(Registries.BLOCK).optionalFieldOf("blocks").forGetter(SlideDownBlockTrigger.TriggerInstance::block), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(SlideDownBlockTrigger.TriggerInstance::state)).apply(i, SlideDownBlockTrigger.TriggerInstance::new));

      public static Criterion slidesDownBlock(final HolderGetter blocks, final Block block) {
         return slidesDownBlock(HolderSet.direct(block.builtInRegistryHolder()));
      }

      public static Criterion slidesDownBlock(final HolderSet block) {
         return CriteriaTriggers.HONEY_BLOCK_SLIDE.createCriterion(new SlideDownBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(block), Optional.empty()));
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
