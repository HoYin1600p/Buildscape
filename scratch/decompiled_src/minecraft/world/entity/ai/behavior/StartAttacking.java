package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StartAttacking {
   public static BehaviorControl create(final StartAttacking.TargetFinder targetFinderFunction) {
      return create((level, body) -> true, targetFinderFunction);
   }

   public static BehaviorControl create(final StartAttacking.StartAttackingCondition canAttackPredicate, final StartAttacking.TargetFinder targetFinderFunction) {
      return BehaviorBuilder.create((i) -> i.group(i.absent(MemoryModuleType.ATTACK_TARGET), i.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(i, (attackTarget, cantReachSince) -> (level, body, timestamp) -> {
               if (!canAttackPredicate.test(level, body)) {
                  return false;
               } else {
                  Optional target = targetFinderFunction.get(level, body);
                  if (target.isEmpty()) {
                     return false;
                  } else {
                     LivingEntity targetEntity = (LivingEntity)target.get();
                     if (!body.canAttack(targetEntity)) {
                        return false;
                     } else {
                        attackTarget.set(targetEntity);
                        cantReachSince.erase();
                        return true;
                     }
                  }
               }
            }));
   }

   @FunctionalInterface
   public interface StartAttackingCondition {
      boolean test(ServerLevel level, Object body);
   }

   @FunctionalInterface
   public interface TargetFinder {
      Optional get(ServerLevel level, Object body);
   }
}
