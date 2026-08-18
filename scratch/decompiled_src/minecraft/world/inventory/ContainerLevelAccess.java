package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ContainerLevelAccess {
   ContainerLevelAccess NULL = new ContainerLevelAccess() {
      public Optional evaluate(final BiFunction action) {
         return Optional.empty();
      }
   };

   static ContainerLevelAccess create(final Level level, final BlockPos pos) {
      return new ContainerLevelAccess() {
         public Optional evaluate(final BiFunction action) {
            return Optional.of(action.apply(level, pos));
         }
      };
   }

   Optional evaluate(BiFunction action);

   default Object evaluate(final BiFunction action, final Object defaultValue) {
      return this.evaluate(action).orElse(defaultValue);
   }

   default void execute(final BiConsumer action) {
      this.evaluate((level, pos) -> {
         action.accept(level, pos);
         return Optional.empty();
      });
   }
}
