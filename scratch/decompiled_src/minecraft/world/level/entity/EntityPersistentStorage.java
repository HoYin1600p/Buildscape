package net.minecraft.world.level.entity;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.ChunkPos;

public interface EntityPersistentStorage extends AutoCloseable {
   CompletableFuture loadEntities(ChunkPos pos);

   void storeEntities(final ChunkEntities chunk);

   void flush(boolean flushStorage);

   default void close() throws IOException {
   }
}
