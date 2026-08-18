package net.minecraft.server.level;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;

public interface GeneratingChunkMap {
   GenerationChunkHolder acquireGeneration(long chunkNode);

   void releaseGeneration(GenerationChunkHolder chunkHolder);

   CompletableFuture applyStep(GenerationChunkHolder chunkHolder, ChunkStep step, StaticCache2D cache);

   ChunkGenerationTask scheduleGenerationTask(ChunkStatus targetStatus, ChunkPos pos);

   void runGenerationTasks();
}
