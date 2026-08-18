package net.minecraft.advancements.triggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class SimpleCriterionTrigger implements CriterionTrigger {
   protected void trigger(final ServerPlayer player, final Predicate matcher) {
      PlayerAdvancements advancements = player.getAdvancements();
      Map listenersForType = advancements.getTriggerMapForType(this);
      if (listenersForType != null && !listenersForType.isEmpty()) {
         LootContext playerContext = EntityPredicate.createContext(player, player);
         List matchedConditions = null;

         for(Map.Entry entry : listenersForType.entrySet()) {
            SimpleCriterionTrigger.SimpleInstance value = (T)((SimpleCriterionTrigger.SimpleInstance)entry.getValue());
            if (matcher.test(value)) {
               Optional predicate = value.player();
               if (!predicate.isPresent() || ((LootItemCondition)((Holder)predicate.get()).value()).test(playerContext)) {
                  if (matchedConditions == null) {
                     matchedConditions = new ArrayList();
                  }

                  matchedConditions.add((PlayerAdvancements.TriggerInstanceKey)entry.getKey());
               }
            }
         }

         if (matchedConditions != null) {
            for(PlayerAdvancements.TriggerInstanceKey criterion : matchedConditions) {
               advancements.award(criterion.advancement(), criterion.criterion());
            }
         }

      }
   }

   public interface SimpleInstance extends CriterionTriggerInstance {
      default void validate(final ValidationContextSource validator) {
         Validatable.validateHolder(validator.entityContext(), "player", this.player());
      }

      Optional player();
   }
}
