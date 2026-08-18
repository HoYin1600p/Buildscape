package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.jspecify.annotations.Nullable;

public interface MemoryCondition {
   MemoryModuleType memory();

   MemoryStatus condition();

   @Nullable MemoryAccessor createAccessor(Brain brain, final Optional value);

   public static record Absent(MemoryModuleType memory) implements MemoryCondition {
      public MemoryStatus condition() {
         return MemoryStatus.VALUE_ABSENT;
      }

      public MemoryAccessor createAccessor(final Brain brain, final Optional value) {
         return value.isPresent() ? null : new MemoryAccessor(brain, this.memory, Const.create(Unit.INSTANCE));
      }
   }

   public static record Present(MemoryModuleType memory) implements MemoryCondition {
      public MemoryStatus condition() {
         return MemoryStatus.VALUE_PRESENT;
      }

      public MemoryAccessor createAccessor(final Brain brain, final Optional value) {
         return value.isEmpty() ? null : new MemoryAccessor(brain, this.memory, IdF.create(value.get()));
      }
   }

   public static record Registered(MemoryModuleType memory) implements MemoryCondition {
      public MemoryStatus condition() {
         return MemoryStatus.REGISTERED;
      }

      public MemoryAccessor createAccessor(final Brain brain, final Optional value) {
         return new MemoryAccessor(brain, this.memory, OptionalBox.create(value));
      }
   }
}
