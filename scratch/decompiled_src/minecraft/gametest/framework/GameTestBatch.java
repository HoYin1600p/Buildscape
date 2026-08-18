package net.minecraft.gametest.framework;

import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record GameTestBatch(int index, Collection gameTestInfos, Holder environment, ResourceKey dimension) {
   public GameTestBatch {
      if (gameTestInfos.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
      }
   }
}
