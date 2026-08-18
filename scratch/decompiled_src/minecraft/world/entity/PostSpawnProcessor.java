package net.minecraft.world.entity;

import java.util.Objects;

public interface PostSpawnProcessor {
   void apply(Entity target);

   default PostSpawnProcessor andThen(final PostSpawnProcessor after) {
      Objects.requireNonNull(after);
      return (t) -> {
         this.apply(t);
         after.apply(t);
      };
   }

   static PostSpawnProcessor nop() {
      return (var0) -> {
      };
   }
}
