package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.util.Util;

public class LinearLayout implements Layout {
   private final GridLayout wrapped;
   private final LinearLayout.Orientation orientation;
   private int nextChildIndex = 0;

   private LinearLayout(final LinearLayout.Orientation orientation) {
      this(0, 0, orientation);
   }

   public LinearLayout(final int x, final int y, final LinearLayout.Orientation orientation) {
      this.wrapped = new GridLayout(x, y);
      this.orientation = orientation;
   }

   public LinearLayout spacing(final int spacing) {
      this.orientation.setSpacing(this.wrapped, spacing);
      return this;
   }

   public LayoutSettings newCellSettings() {
      return this.wrapped.newCellSettings();
   }

   public LayoutSettings defaultCellSetting() {
      return this.wrapped.defaultCellSetting();
   }

   public LayoutElement addChild(final LayoutElement child, final LayoutSettings cellSettings) {
      return this.orientation.addChild(this.wrapped, child, this.nextChildIndex++, cellSettings);
   }

   public LayoutElement addChild(final LayoutElement child) {
      return this.addChild(child, this.newCellSettings());
   }

   public LayoutElement addChild(final LayoutElement child, final Consumer layoutSettingsAdjustments) {
      return this.orientation.addChild(this.wrapped, child, this.nextChildIndex++, (LayoutSettings)Util.make(this.newCellSettings(), layoutSettingsAdjustments));
   }

   public void visitChildren(final Consumer layoutElementVisitor) {
      this.wrapped.visitChildren(layoutElementVisitor);
   }

   public void removeChildren() {
      this.wrapped.removeChildren();
      this.nextChildIndex = 0;
   }

   public void arrangeElements() {
      this.wrapped.arrangeElements();
   }

   public int getWidth() {
      return this.wrapped.getWidth();
   }

   public int getHeight() {
      return this.wrapped.getHeight();
   }

   public void setX(final int x) {
      this.wrapped.setX(x);
   }

   public void setY(final int y) {
      this.wrapped.setY(y);
   }

   public int getX() {
      return this.wrapped.getX();
   }

   public int getY() {
      return this.wrapped.getY();
   }

   public static LinearLayout vertical() {
      return new LinearLayout(LinearLayout.Orientation.VERTICAL);
   }

   public static LinearLayout horizontal() {
      return new LinearLayout(LinearLayout.Orientation.HORIZONTAL);
   }

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      private void setSpacing(final GridLayout gridLayout, final int spacing) {
         switch (this.ordinal()) {
            case 0:
               gridLayout.columnSpacing(spacing);
               break;
            case 1:
               gridLayout.rowSpacing(spacing);
         }

      }

      public LayoutElement addChild(final GridLayout gridLayout, final LayoutElement child, final int index, final LayoutSettings cellSettings) {
         LayoutElement var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = gridLayout.addChild(child, 0, index, cellSettings);
               break;
            case 1:
               var10000 = gridLayout.addChild(child, index, 0, cellSettings);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static LinearLayout.Orientation[] $values() {
         return new LinearLayout.Orientation[]{HORIZONTAL, VERTICAL};
      }
   }
}
