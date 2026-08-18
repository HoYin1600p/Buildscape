package net.minecraft.world.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;

public record ActivityData(Activity activityType, ImmutableList behaviorPriorityPairs, Set conditions, Set memoriesToEraseWhenStopped) {
   public static ActivityData create(final Activity activity, final int priorityOfFirstBehavior, final ImmutableList behaviorList) {
      return create(activity, createPriorityPairs(priorityOfFirstBehavior, behaviorList));
   }

   public static ActivityData create(final Activity activity, final int priorityOfFirstBehavior, final ImmutableList behaviorList, final MemoryModuleType memoryThatMustHaveValueAndWillBeErasedAfter) {
      Set conditions = ImmutableSet.of(Pair.of(memoryThatMustHaveValueAndWillBeErasedAfter, MemoryStatus.VALUE_PRESENT));
      Set memoriesToEraseWhenStopped = ImmutableSet.of(memoryThatMustHaveValueAndWillBeErasedAfter);
      return create(activity, createPriorityPairs(priorityOfFirstBehavior, behaviorList), conditions, memoriesToEraseWhenStopped);
   }

   public static ActivityData create(final Activity activity, final ImmutableList behaviorPriorityPairs) {
      return create(activity, behaviorPriorityPairs, ImmutableSet.of(), Sets.newHashSet());
   }

   public static ActivityData create(final Activity activity, final int priorityOfFirstBehavior, final ImmutableList behaviorList, final Set conditions) {
      return create(activity, createPriorityPairs(priorityOfFirstBehavior, behaviorList), conditions);
   }

   public static ActivityData create(final Activity activity, final ImmutableList behaviorPriorityPairs, final Set conditions) {
      return create(activity, behaviorPriorityPairs, conditions, Sets.newHashSet());
   }

   public static ActivityData create(final Activity activity, final ImmutableList behaviorPriorityPairs, final Set conditions, final Set memoriesToEraseWhenStopped) {
      return new ActivityData(activity, behaviorPriorityPairs, conditions, memoriesToEraseWhenStopped);
   }

   public static ImmutableList createPriorityPairs(final int priorityOfFirstBehavior, final ImmutableList behaviorList) {
      int nextPrio = priorityOfFirstBehavior;
      ImmutableList.Builder listBuilder = ImmutableList.builder();
      UnmodifiableIterator var4 = behaviorList.iterator();

      while(var4.hasNext()) {
         BehaviorControl behavior = (BehaviorControl)var4.next();
         listBuilder.add(Pair.of(nextPrio++, behavior));
      }

      return listBuilder.build();
   }
}
