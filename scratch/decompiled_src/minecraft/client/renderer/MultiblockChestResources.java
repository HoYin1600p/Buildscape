package net.minecraft.client.renderer;

import java.util.function.Function;
import net.minecraft.world.level.block.state.properties.ChestType;

public record MultiblockChestResources(Object single, Object left, Object right) {
   public Object select(final ChestType chestType) {
      Object var10000;
      switch (chestType) {
         case SINGLE:
            var10000 = this.single;
            break;
         case LEFT:
            var10000 = this.left;
            break;
         case RIGHT:
            var10000 = this.right;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public MultiblockChestResources map(final Function mapper) {
      return new MultiblockChestResources(mapper.apply(this.single), mapper.apply(this.left), mapper.apply(this.right));
   }
}
