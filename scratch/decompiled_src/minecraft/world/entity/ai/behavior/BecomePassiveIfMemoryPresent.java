package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Function3;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BecomePassiveIfMemoryPresent {
   public static BehaviorControl create(final MemoryModuleType pacifyingMemory, final int pacifyDuration) {
      return BehaviorBuilder.create((i) -> i.group(i.registered(MemoryModuleType.ATTACK_TARGET), i.absent(MemoryModuleType.PACIFIED), i.present(pacifyingMemory)).apply(i, i.point(() -> "[BecomePassive if " + String.valueOf(pacifyingMemory) + " present]", (Function3)(attackTarget, pacified, pacifying) -> (level, body, timestamp) -> {
               pacified.setWithExpiry(true, (long)pacifyDuration);
               attackTarget.erase();
               return true;
            })));
   }
}
