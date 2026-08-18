package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class UpdateActivityFromSchedule {
   public static BehaviorControl create() {
      return BehaviorBuilder.create((i) -> i.point((Trigger)(level, body, timestamp) -> {
            body.getBrain().updateActivityFromSchedule(level.environmentAttributes(), level.getGameTime(), body.position());
            return true;
         }));
   }
}
