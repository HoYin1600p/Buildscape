package net.minecraft.client.gui.navigation;

public record ScreenPosition(int x, int y) {
   public static ScreenPosition of(final ScreenAxis axis, final int primaryValue, final int secondaryValue) {
      ScreenPosition var10000;
      switch (axis) {
         case HORIZONTAL:
            var10000 = new ScreenPosition(primaryValue, secondaryValue);
            break;
         case VERTICAL:
            var10000 = new ScreenPosition(secondaryValue, primaryValue);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenPosition step(final ScreenDirection direction) {
      ScreenPosition var10000;
      switch (direction) {
         case DOWN:
            var10000 = new ScreenPosition(this.x, this.y + 1);
            break;
         case UP:
            var10000 = new ScreenPosition(this.x, this.y - 1);
            break;
         case LEFT:
            var10000 = new ScreenPosition(this.x - 1, this.y);
            break;
         case RIGHT:
            var10000 = new ScreenPosition(this.x + 1, this.y);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int getCoordinate(final ScreenAxis axis) {
      int var10000;
      switch (axis) {
         case HORIZONTAL:
            var10000 = this.x;
            break;
         case VERTICAL:
            var10000 = this.y;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
