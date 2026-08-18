package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

public class GoAndGiveItemsToTarget extends Behavior {
   private static final int CLOSE_ENOUGH_DISTANCE_TO_TARGET = 3;
   private final Function targetPositionGetter;
   private final float speedModifier;
   private final GoAndGiveItemsToTarget.ItemThrower itemThrower;
   private final MemoryModuleType cooldownMemory;
   private final int cooldownDuration;
   private final Predicate hasItemPredicate;

   public GoAndGiveItemsToTarget(final Function targetPositionGetter, final float speedModifier, final int timeoutDuration, final GoAndGiveItemsToTarget.ItemThrower itemThrower, final MemoryModuleType cooldownMemory, final int cooldownDuration, final Predicate hasItemPredicate) {
      super(Map.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, cooldownMemory, MemoryStatus.REGISTERED), timeoutDuration);
      this.targetPositionGetter = targetPositionGetter;
      this.speedModifier = speedModifier;
      this.itemThrower = itemThrower;
      this.cooldownMemory = cooldownMemory;
      this.cooldownDuration = cooldownDuration;
      this.hasItemPredicate = hasItemPredicate;
   }

   protected boolean checkExtraStartConditions(final ServerLevel level, final LivingEntity body) {
      return this.canThrowItemToTarget(body);
   }

   protected boolean canStillUse(final ServerLevel level, final LivingEntity body, final long timestamp) {
      return this.canThrowItemToTarget(body);
   }

   protected void start(final ServerLevel level, final LivingEntity body, final long timestamp) {
      ((Optional)this.targetPositionGetter.apply(body)).ifPresent((positionTracker) -> BehaviorUtils.setWalkAndLookTargetMemories(body, positionTracker, this.speedModifier, 3));
   }

   protected void tick(final ServerLevel level, final LivingEntity body, final long timestamp) {
      Optional targetPosition = (Optional)this.targetPositionGetter.apply(body);
      if (!targetPosition.isEmpty()) {
         PositionTracker depositTarget = (PositionTracker)targetPosition.get();
         Vec3 depositPosition = depositTarget.currentPosition();
         double distanceToTarget = depositPosition.distanceTo(body.getEyePosition());
         if (distanceToTarget < 3.0D) {
            this.itemThrower.throwItem(level, body, depositTarget.currentPosition());
            body.getBrain().setMemory(this.cooldownMemory, this.cooldownDuration);
         }

      }
   }

   private boolean canThrowItemToTarget(final LivingEntity body) {
      return this.hasItemPredicate.test(body) && ((Optional)this.targetPositionGetter.apply(body)).isPresent();
   }

   @FunctionalInterface
   public interface ItemThrower {
      void throwItem(ServerLevel level, Object thrower, Vec3 targetPos);
   }
}
