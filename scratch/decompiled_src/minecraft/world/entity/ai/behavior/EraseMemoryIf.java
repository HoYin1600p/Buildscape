package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class EraseMemoryIf {
   public static BehaviorControl create(final Predicate predicate, final MemoryModuleType memoryType) {
      return BehaviorBuilder.create((i) -> i.group(i.present(memoryType)).apply(i, (memory) -> (level, body, timestamp) -> {
               if (predicate.test(body)) {
                  memory.erase();
                  return true;
               } else {
                  return false;
               }
            }));
   }
}
