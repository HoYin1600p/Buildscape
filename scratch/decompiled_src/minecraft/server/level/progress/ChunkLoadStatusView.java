package net.minecraft.server.level.progress;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

public interface ChunkLoadStatusView {
   void moveTo(ResourceKey dimension, ChunkPos centerChunk);

   @Nullable ChunkStatus get(int x, int z);

   int radius();
}
