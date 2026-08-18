package net.minecraft.server.level;

public enum FullChunkStatus {
   INACCESSIBLE,
   FULL,
   BLOCK_TICKING,
   ENTITY_TICKING;

   public boolean isOrAfter(final FullChunkStatus step) {
      return this.ordinal() >= step.ordinal();
   }

   // $FF: synthetic method
   private static FullChunkStatus[] $values() {
      return new FullChunkStatus[]{INACCESSIBLE, FULL, BLOCK_TICKING, ENTITY_TICKING};
   }
}
