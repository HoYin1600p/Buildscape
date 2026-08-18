package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.Util;

public class EqualSpacingLayout extends AbstractLayout {
   private final EqualSpacingLayout.Orientation orientation;
   private final List children = new ArrayList();
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

   public EqualSpacingLayout(final int width, final int height, final EqualSpacingLayout.Orientation orientation) {
      this(0, 0, width, height, orientation);
   }

   public EqualSpacingLayout(final int x, final int y, final int width, final int height, final EqualSpacingLayout.Orientation orientation) {
      super(x, y, width, height);
      this.orientation = orientation;
   }

   public void arrangeElements() {
      super.arrangeElements();
      if (!this.children.isEmpty()) {
         int totalChildPrimaryLength = 0;
         int maxChildSecondaryLength = this.orientation.getSecondaryLength(this);

         for(EqualSpacingLayout.ChildContainer child : this.children) {
            totalChildPrimaryLength += this.orientation.getPrimaryLength(child);
            maxChildSecondaryLength = Math.max(maxChildSecondaryLength, this.orientation.getSecondaryLength(child));
         }

         int remainingSpace = this.orientation.getPrimaryLength(this) - totalChildPrimaryLength;
         int position = this.orientation.getPrimaryPosition(this);
         Iterator childIterator = this.children.iterator();
         EqualSpacingLayout.ChildContainer firstChild = (EqualSpacingLayout.ChildContainer)childIterator.next();
         this.orientation.setPrimaryPosition(firstChild, position);
         position += this.orientation.getPrimaryLength(firstChild);
         EqualSpacingLayout.ChildContainer child;
         if (this.children.size() >= 2) {
            for(Divisor divisor = new Divisor(remainingSpace, this.children.size() - 1); divisor.hasNext(); position += this.orientation.getPrimaryLength(child)) {
               position += divisor.nextInt();
               child = (EqualSpacingLayout.ChildContainer)childIterator.next();
               this.orientation.setPrimaryPosition(child, position);
            }
         }

         int thisSecondaryPosition = this.orientation.getSecondaryPosition(this);

         for(EqualSpacingLayout.ChildContainer child : this.children) {
            this.orientation.setSecondaryPosition(child, thisSecondaryPosition, maxChildSecondaryLength);
         }

         switch (this.orientation.ordinal()) {
            case 0:
               this.height = maxChildSecondaryLength;
               break;
            case 1:
               this.width = maxChildSecondaryLength;
         }

      }
   }

   public void visitChildren(final Consumer layoutElementVisitor) {
      this.children.forEach((wrapper) -> layoutElementVisitor.accept(wrapper.child));
   }

   public void removeChildren() {
      this.children.clear();
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public LayoutElement addChild(final LayoutElement child) {
      return this.addChild(child, this.newChildLayoutSettings());
   }

   public LayoutElement addChild(final LayoutElement child, final LayoutSettings layoutSettings) {
      this.children.add(new EqualSpacingLayout.ChildContainer(child, layoutSettings));
      return child;
   }

   public LayoutElement addChild(final LayoutElement child, final Consumer layoutSettingsAdjustments) {
      return this.addChild(child, (LayoutSettings)Util.make(this.newChildLayoutSettings(), layoutSettingsAdjustments));
   }

   private static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
      protected ChildContainer(final LayoutElement child, final LayoutSettings layoutSettings) {
         super(child, layoutSettings);
      }
   }

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      private int getPrimaryLength(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = widget.getWidth();
               break;
            case 1:
               var10000 = widget.getHeight();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getPrimaryLength(final EqualSpacingLayout.ChildContainer childContainer) {
         int var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = childContainer.getWidth();
               break;
            case 1:
               var10000 = childContainer.getHeight();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getSecondaryLength(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = widget.getHeight();
               break;
            case 1:
               var10000 = widget.getWidth();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getSecondaryLength(final EqualSpacingLayout.ChildContainer childContainer) {
         int var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = childContainer.getHeight();
               break;
            case 1:
               var10000 = childContainer.getWidth();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private void setPrimaryPosition(final EqualSpacingLayout.ChildContainer childContainer, final int position) {
         switch (this.ordinal()) {
            case 0:
               childContainer.setX(position, childContainer.getWidth());
               break;
            case 1:
               childContainer.setY(position, childContainer.getHeight());
         }

      }

      private void setSecondaryPosition(final EqualSpacingLayout.ChildContainer childContainer, final int position, final int availableSpace) {
         switch (this.ordinal()) {
            case 0:
               childContainer.setY(position, availableSpace);
               break;
            case 1:
               childContainer.setX(position, availableSpace);
         }

      }

      private int getPrimaryPosition(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = widget.getX();
               break;
            case 1:
               var10000 = widget.getY();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getSecondaryPosition(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = widget.getY();
               break;
            case 1:
               var10000 = widget.getX();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static EqualSpacingLayout.Orientation[] $values() {
         return new EqualSpacingLayout.Orientation[]{HORIZONTAL, VERTICAL};
      }
   }
}
