package net.minecraft.world.level.chunk.status;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;

@FunctionalInterface
public interface ChunkStatusTask {
   CompletableFuture doWork(WorldGenContext context, ChunkStep step, StaticCache2D chunks, ChunkAccess chunk);
}
