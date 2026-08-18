package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return FishingRodHookedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack rod, final FishingHook hook, final Collection items) {
      LootContext hookedInContext = EntityPredicate.createContext(player, (Entity)(hook.getHookedIn() != null ? hook.getHookedIn() : hook));
      this.trigger(player, (t) -> t.matches(rod, hookedInContext, items));
   }

   public static record TriggerInstance(Optional player, Optional rod, Optional entity, Optional item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(FishingRodHookedTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("rod").forGetter(FishingRodHookedTrigger.TriggerInstance::rod), LootItemCondition.CODEC.optionalFieldOf("entity").forGetter(FishingRodHookedTrigger.TriggerInstance::entity), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FishingRodHookedTrigger.TriggerInstance::item)).apply(i, FishingRodHookedTrigger.TriggerInstance::new));

      public static Criterion fishedItem(final Optional rod, final Optional entity, final Optional item) {
         return CriteriaTriggers.FISHING_ROD_HOOKED.createCriterion(new FishingRodHookedTrigger.TriggerInstance(Optional.empty(), rod, EntityPredicate.wrap(entity), item));
      }

      public boolean matches(final ItemStack rod, final LootContext hookedIn, final Collection items) {
         if (this.rod.isPresent() && !((ItemPredicate)this.rod.get()).test((ItemInstance)rod)) {
            return false;
         } else if (this.entity.isPresent() && !((LootItemCondition)((Holder)this.entity.get()).value()).test(hookedIn)) {
            return false;
         } else {
            if (this.item.isPresent()) {
               boolean matched = false;
               Entity hookedInEntity = (Entity)hookedIn.getOptionalParameter(LootContextParams.THIS_ENTITY);
               if (hookedInEntity instanceof ItemEntity) {
                  ItemEntity item = (ItemEntity)hookedInEntity;
                  if (((ItemPredicate)this.item.get()).test((ItemInstance)item.getItem())) {
                     matched = true;
                  }
               }

               for(ItemStack item : items) {
                  if (((ItemPredicate)this.item.get()).test((ItemInstance)item)) {
                     matched = true;
                     break;
                  }
               }

               if (!matched) {
                  return false;
               }
            }

            return true;
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "entity", this.entity);
      }
   }
}
