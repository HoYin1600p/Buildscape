package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InteractWith {
   public static BehaviorControl of(final EntityType type, final int interactionRange, final MemoryModuleType interactionTarget, final float speedModifier, final int stopDistance) {
      return of(type, interactionRange, (mob) -> true, (mob) -> true, interactionTarget, speedModifier, stopDistance);
   }

   public static BehaviorControl of(final EntityType type, final int interactionRange, final Predicate selfFilter, final Predicate targetFilter, final MemoryModuleType interactionTarget, final float speedModifier, final int stopDistance) {
      int interactionRangeSqr = interactionRange * interactionRange;
      Predicate isTargetValid = (mob) -> mob.is(type) && targetFilter.test(mob);
      return BehaviorBuilder.create((i) -> i.group(i.registered(interactionTarget), i.registered(MemoryModuleType.LOOK_TARGET), i.absent(MemoryModuleType.WALK_TARGET), i.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(i, (target, lookTarget, walkTarget, nearestEntities) -> (level, body, timestamp) -> {
               NearestVisibleLivingEntities entities = (NearestVisibleLivingEntities)i.get(nearestEntities);
               if (selfFilter.test(body) && entities.contains(isTargetValid)) {
                  Optional closest = entities.findClosest((mob) -> mob.distanceToSqr(body) <= (double)interactionRangeSqr && isTargetValid.test(mob));
                  closest.ifPresent((mob) -> {
                     target.set(mob);
                     lookTarget.set(new EntityTracker(mob, true));
                     walkTarget.set(new WalkTarget(new EntityTracker(mob, false), speedModifier, stopDistance));
                  });
                  return true;
               } else {
                  return false;
               }
            }));
   }
}
