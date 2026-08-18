package net.minecraft.client.gui.navigation;

public enum ScreenAxis {
   HORIZONTAL,
   VERTICAL;

   public ScreenAxis orthogonal() {
      ScreenAxis var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = VERTICAL;
            break;
         case 1:
            var10000 = HORIZONTAL;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenDirection getPositive() {
      ScreenDirection var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = ScreenDirection.RIGHT;
            break;
         case 1:
            var10000 = ScreenDirection.DOWN;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenDirection getNegative() {
      ScreenDirection var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = ScreenDirection.LEFT;
            break;
         case 1:
            var10000 = ScreenDirection.UP;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenDirection getDirection(final boolean positive) {
      return positive ? this.getPositive() : this.getNegative();
   }

   // $FF: synthetic method
   private static ScreenAxis[] $values() {
      return new ScreenAxis[]{HORIZONTAL, VERTICAL};
   }
}
