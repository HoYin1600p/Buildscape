package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.schedule.Activity;

public class WakeUp {
   public static BehaviorControl create() {
      return BehaviorBuilder.create((i) -> i.point((Trigger)(level, body, timestamp) -> {
            if (!body.getBrain().isActive(Activity.REST) && body.isSleeping()) {
               body.stopSleeping();
               return true;
            } else {
               return false;
            }
         }));
   }
}
