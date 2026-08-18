package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetEntityLookTarget {
   public static BehaviorControl create(final MobCategory category, final float maxDist) {
      return create((mob) -> category.equals(mob.getType().getCategory()), maxDist);
   }

   public static OneShot create(final EntityType type, final float maxDist) {
      return create((mob) -> mob.is(type), maxDist);
   }

   public static OneShot create(final float maxDist) {
      return create((mob) -> true, maxDist);
   }

   public static OneShot create(final Predicate predicate, final float maxDist) {
      float maxDistSqr = maxDist * maxDist;
      return BehaviorBuilder.create((i) -> i.group(i.absent(MemoryModuleType.LOOK_TARGET), i.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(i, (lookTarget, nearestEntities) -> (level, body, timestamp) -> {
               Optional target = ((NearestVisibleLivingEntities)i.get(nearestEntities)).findClosest(predicate.and((mob) -> mob.distanceToSqr(body) <= (double)maxDistSqr && !body.hasPassenger(mob)));
               if (target.isEmpty()) {
                  return false;
               } else {
                  lookTarget.set(new EntityTracker((Entity)target.get(), true));
                  return true;
               }
            }));
   }
}
