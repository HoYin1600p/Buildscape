package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunOne extends GateBehavior {
   public RunOne(final List weightedBehaviors) {
      this(ImmutableMap.of(), weightedBehaviors);
   }

   public RunOne(final Map entryCondition, final List weightedBehaviors) {
      super(entryCondition, ImmutableSet.of(), GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE, weightedBehaviors);
   }
}
