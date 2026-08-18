package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class CopyMemoryWithExpiry {
   public static BehaviorControl create(final Predicate copyIfTrue, final MemoryModuleType sourceMemory, final MemoryModuleType targetMemory, final UniformInt durationOfCopy) {
      return BehaviorBuilder.create((i) -> i.group(i.present(sourceMemory), i.absent(targetMemory)).apply(i, (source, target) -> (level, body, timestamp) -> {
               if (!copyIfTrue.test(body)) {
                  return false;
               } else {
                  target.setWithExpiry(i.get(source), (long)durationOfCopy.sample(level.getRandom()));
                  return true;
               }
            }));
   }
}
