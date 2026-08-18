package net.minecraft.world.level.entity;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.level.ChunkPos;

public class ChunkEntities {
   private final ChunkPos pos;
   private final List entities;

   public ChunkEntities(final ChunkPos pos, final List entities) {
      this.pos = pos;
      this.entities = entities;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public Stream getEntities() {
      return this.entities.stream();
   }

   public boolean isEmpty() {
      return this.entities.isEmpty();
   }
}
