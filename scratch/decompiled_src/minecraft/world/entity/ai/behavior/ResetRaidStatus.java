package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ResetRaidStatus {
   public static BehaviorControl create() {
      return BehaviorBuilder.create((i) -> i.point((Trigger)(level, body, timestamp) -> {
            if (level.getRandom().nextInt(20) != 0) {
               return false;
            } else {
               Brain brain = body.getBrain();
               Raid nearbyRaid = level.getRaidAt(body.blockPosition());
               if (nearbyRaid == null || nearbyRaid.isStopped() || nearbyRaid.isLoss()) {
                  brain.setDefaultActivity(Activity.IDLE);
                  brain.updateActivityFromSchedule(level.environmentAttributes(), level.getGameTime(), body.position());
               }

               return true;
            }
         }));
   }
}
