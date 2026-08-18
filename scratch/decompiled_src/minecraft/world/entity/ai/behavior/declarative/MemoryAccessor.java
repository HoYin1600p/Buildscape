package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public final class MemoryAccessor {
   private final Brain brain;
   private final MemoryModuleType memoryType;
   private final App value;

   public MemoryAccessor(final Brain brain, final MemoryModuleType memoryType, final App value) {
      this.brain = brain;
      this.memoryType = memoryType;
      this.value = value;
   }

   public App value() {
      return this.value;
   }

   public void set(final Object value) {
      this.brain.setMemory(this.memoryType, Optional.of(value));
   }

   public void setOrErase(final Optional value) {
      this.brain.setMemory(this.memoryType, value);
   }

   public void setWithExpiry(final Object value, final long timeToLive) {
      this.brain.setMemoryWithExpiry(this.memoryType, value, timeToLive);
   }

   public void erase() {
      this.brain.eraseMemory(this.memoryType);
   }
}
