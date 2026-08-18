package net.minecraft.util.debug;

import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public interface DebugValueAccess {
   void forEachChunk(DebugSubscription subscription, BiConsumer consumer);

   @Nullable Object getChunkValue(DebugSubscription subscription, ChunkPos chunkPos);

   void forEachBlock(DebugSubscription subscription, BiConsumer consumer);

   @Nullable Object getBlockValue(DebugSubscription subscription, BlockPos blockPos);

   void forEachEntity(DebugSubscription subscription, BiConsumer consumer);

   @Nullable Object getEntityValue(DebugSubscription subscription, Entity entity);

   void forEachEvent(DebugSubscription subscription, DebugValueAccess.EventVisitor visitor);

   @FunctionalInterface
   public interface EventVisitor {
      void accept(Object value, int remainingTicks, int totalLifetime);
   }
}
