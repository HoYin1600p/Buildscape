package net.minecraft.world.entity.ai.sensing;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class MobSensor extends Sensor {
   private final BiPredicate mobTest;
   private final Predicate readyTest;
   private final MemoryModuleType toSet;
   private final int memoryTimeToLive;

   public MobSensor(final int scanRate, final BiPredicate mobTest, final Predicate readyTest, final MemoryModuleType toSet, final int memoryTimeToLive) {
      super(scanRate);
      this.mobTest = mobTest;
      this.readyTest = readyTest;
      this.toSet = toSet;
      this.memoryTimeToLive = memoryTimeToLive;
   }

   protected void doTick(final ServerLevel level, final LivingEntity body) {
      if (!this.readyTest.test(body)) {
         this.clearMemory(body);
      } else {
         this.checkForMobsNearby(body);
      }

   }

   public Set requires() {
      return Set.of(MemoryModuleType.NEAREST_LIVING_ENTITIES);
   }

   public void checkForMobsNearby(final LivingEntity body) {
      Optional livingEntitiesMemory = body.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
      if (!livingEntitiesMemory.isEmpty()) {
         boolean mobPresent = ((List)livingEntitiesMemory.get()).stream().anyMatch((entity) -> this.mobTest.test(body, entity));
         if (mobPresent) {
            this.mobDetected(body);
         }

      }
   }

   public void mobDetected(final LivingEntity body) {
      body.getBrain().setMemoryWithExpiry(this.toSet, true, (long)this.memoryTimeToLive);
   }

   public void clearMemory(final LivingEntity body) {
      body.getBrain().eraseMemory(this.toSet);
   }
}
