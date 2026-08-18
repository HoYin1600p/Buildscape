package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class TriggerGate {
   public static OneShot triggerOneShuffled(final List weightedTriggers) {
      return triggerGate(weightedTriggers, GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE);
   }

   public static OneShot triggerGate(final List weightedBehaviors, final GateBehavior.OrderPolicy orderPolicy, final GateBehavior.RunningPolicy runningPolicy) {
      ShufflingList behaviors = new ShufflingList();
      weightedBehaviors.forEach((entry) -> behaviors.add((Trigger)entry.getFirst(), entry.getSecond()));
      return BehaviorBuilder.create((i) -> i.point((Trigger)(level, body, timestamp) -> {
            if (orderPolicy == GateBehavior.OrderPolicy.SHUFFLED) {
               behaviors.shuffle();
            }

            for(Trigger behavior : behaviors) {
               if (behavior.trigger(level, body, timestamp) && runningPolicy == GateBehavior.RunningPolicy.RUN_ONE) {
                  break;
               }
            }

            return true;
         }));
   }
}
