package net.minecraft.server.level.progress;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public interface LevelLoadListener {
   static LevelLoadListener compose(final LevelLoadListener first, final LevelLoadListener second) {
      return new LevelLoadListener() {
         public void start(final LevelLoadListener.Stage stage, final int totalChunks) {
            first.start(stage, totalChunks);
            second.start(stage, totalChunks);
         }

         public void update(final LevelLoadListener.Stage stage, final int currentChunks, final int totalChunks) {
            first.update(stage, currentChunks, totalChunks);
            second.update(stage, currentChunks, totalChunks);
         }

         public void finish(final LevelLoadListener.Stage stage) {
            first.finish(stage);
            second.finish(stage);
         }

         public void updateFocus(final ResourceKey dimension, final ChunkPos chunkPos) {
            first.updateFocus(dimension, chunkPos);
            second.updateFocus(dimension, chunkPos);
         }
      };
   }

   void start(LevelLoadListener.Stage stage, int totalChunks);

   void update(LevelLoadListener.Stage stage, int currentChunks, int totalChunks);

   void finish(LevelLoadListener.Stage stage);

   void updateFocus(ResourceKey dimension, ChunkPos chunkPos);

   public static enum Stage {
      START_SERVER,
      PREPARE_GLOBAL_SPAWN,
      LOAD_INITIAL_CHUNKS,
      LOAD_PLAYER_CHUNKS;

      // $FF: synthetic method
      private static LevelLoadListener.Stage[] $values() {
         return new LevelLoadListener.Stage[]{START_SERVER, PREPARE_GLOBAL_SPAWN, LOAD_INITIAL_CHUNKS, LOAD_PLAYER_CHUNKS};
      }
   }
}
