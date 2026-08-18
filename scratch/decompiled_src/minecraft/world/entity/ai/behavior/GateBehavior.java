package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GateBehavior implements BehaviorControl {
   private final Map entryCondition;
   private final Set exitErasedMemories;
   private final GateBehavior.OrderPolicy orderPolicy;
   private final GateBehavior.RunningPolicy runningPolicy;
   private final ShufflingList behaviors = new ShufflingList();
   private Behavior.Status status = Behavior.Status.STOPPED;

   public GateBehavior(final Map entryCondition, final Set exitErasedMemories, final GateBehavior.OrderPolicy orderPolicy, final GateBehavior.RunningPolicy runningPolicy, final List behaviors) {
      this.entryCondition = entryCondition;
      this.exitErasedMemories = exitErasedMemories;
      this.orderPolicy = orderPolicy;
      this.runningPolicy = runningPolicy;
      behaviors.forEach((entry) -> this.behaviors.add((BehaviorControl)entry.getFirst(), entry.getSecond()));
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   public Set getRequiredMemories() {
      Set memories = new HashSet(this.entryCondition.keySet());

      for(BehaviorControl behavior : this.behaviors) {
         memories.addAll(behavior.getRequiredMemories());
      }

      return memories;
   }

   private boolean hasRequiredMemories(final LivingEntity body) {
      for(Map.Entry entry : this.entryCondition.entrySet()) {
         MemoryModuleType memoryType = (MemoryModuleType)entry.getKey();
         MemoryStatus requiredStatus = (MemoryStatus)entry.getValue();
         if (!body.getBrain().checkMemory(memoryType, requiredStatus)) {
            return false;
         }
      }

      return true;
   }

   public final boolean tryStart(final ServerLevel level, final LivingEntity body, final long timestamp) {
      if (this.hasRequiredMemories(body)) {
         this.status = Behavior.Status.RUNNING;
         this.orderPolicy.apply(this.behaviors);
         this.runningPolicy.apply(this.behaviors.stream(), level, body, timestamp);
         return true;
      } else {
         return false;
      }
   }

   public final void tickOrStop(final ServerLevel level, final LivingEntity body, final long timestamp) {
      this.behaviors.stream().filter((goal) -> goal.getStatus() == Behavior.Status.RUNNING).forEach((goal) -> goal.tickOrStop(level, body, timestamp));
      if (this.behaviors.stream().noneMatch((g) -> g.getStatus() == Behavior.Status.RUNNING)) {
         this.doStop(level, body, timestamp);
      }

   }

   public final void doStop(final ServerLevel level, final LivingEntity body, final long timestamp) {
      this.status = Behavior.Status.STOPPED;
      this.behaviors.stream().filter((goal) -> goal.getStatus() == Behavior.Status.RUNNING).forEach((goal) -> goal.doStop(level, body, timestamp));
      this.exitErasedMemories.forEach(body.getBrain()::eraseMemory);
   }

   public String debugString() {
      Set runningBehaviours = (Set)this.behaviors.stream().filter((goal) -> goal.getStatus() == Behavior.Status.RUNNING).map((b) -> b.getClass().getSimpleName()).collect(Collectors.toSet());
      return this.getClass().getSimpleName() + ": " + String.valueOf(runningBehaviours);
   }

   public static enum OrderPolicy {
      ORDERED((t) -> {
      }),
      SHUFFLED(ShufflingList::shuffle);

      private final Consumer consumer;

      private OrderPolicy(final Consumer consumer) {
         this.consumer = consumer;
      }

      public void apply(final ShufflingList list) {
         this.consumer.accept(list);
      }

      // $FF: synthetic method
      private static GateBehavior.OrderPolicy[] $values() {
         return new GateBehavior.OrderPolicy[]{ORDERED, SHUFFLED};
      }
   }

   public static enum RunningPolicy {
      RUN_ONE {
         public void apply(final Stream behaviors, final ServerLevel level, final LivingEntity body, final long timestamp) {
            behaviors.filter((goal) -> goal.getStatus() == Behavior.Status.STOPPED).filter((goal) -> goal.tryStart(level, body, timestamp)).findFirst();
         }
      },
      TRY_ALL {
         public void apply(final Stream behaviors, final ServerLevel level, final LivingEntity body, final long timestamp) {
            behaviors.filter((goal) -> goal.getStatus() == Behavior.Status.STOPPED).forEach((goal) -> goal.tryStart(level, body, timestamp));
         }
      };

      public abstract void apply(final Stream behaviors, final ServerLevel level, final LivingEntity body, final long timestamp);

      // $FF: synthetic method
      private static GateBehavior.RunningPolicy[] $values() {
         return new GateBehavior.RunningPolicy[]{RUN_ONE, TRY_ALL};
      }
   }
}
