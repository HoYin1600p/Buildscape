package net.minecraft.client.gui.components;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationTrigger;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public abstract class ContainerObjectSelectionList extends AbstractSelectionList {
   public ContainerObjectSelectionList(final Minecraft minecraft, final int width, final int height, final int y, final int itemHeight) {
      super(minecraft, width, height, y, itemHeight);
   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      if (this.getItemCount() == 0) {
         return null;
      } else if (!(navigationEvent instanceof FocusNavigationEvent.ArrowNavigation)) {
         return super.nextFocusPath(navigationEvent);
      } else {
         FocusNavigationEvent.ArrowNavigation arrowNavigation = (FocusNavigationEvent.ArrowNavigation)navigationEvent;
         ContainerObjectSelectionList.Entry focused = (E)((ContainerObjectSelectionList.Entry)this.getFocused());
         if (arrowNavigation.direction().getAxis() == ScreenAxis.HORIZONTAL && focused != null) {
            return ComponentPath.path(this, focused.nextFocusPath(navigationEvent));
         } else {
            int index = -1;
            ScreenDirection direction = arrowNavigation.direction();
            if (focused != null) {
               index = focused.children().indexOf(focused.getFocused());
            }

            if (index == -1) {
               switch (direction) {
                  case LEFT:
                     index = Integer.MAX_VALUE;
                     direction = ScreenDirection.DOWN;
                     break;
                  case RIGHT:
                     index = 0;
                     direction = ScreenDirection.DOWN;
                     break;
                  default:
                     index = 0;
               }
            }

            ContainerObjectSelectionList.Entry entry = focused;

            ComponentPath componentPath;
            do {
               entry = (E)((ContainerObjectSelectionList.Entry)this.nextEntry(direction, (e) -> !e.children().isEmpty(), entry));
               if (entry == null) {
                  return null;
               }

               componentPath = entry.focusPathAtIndex(arrowNavigation, index);
            } while(componentPath == null);

            return ComponentPath.path(this, componentPath);
         }
      }
   }

   public void setFocused(final @Nullable GuiEventListener focused) {
      if (this.getFocused() != focused) {
         super.setFocused(focused);
         if (focused == null) {
            this.setSelected((AbstractSelectionList.Entry)null);
         }

      }
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.isFocused() ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
   }

   protected boolean entriesCanBeSelected() {
      return false;
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      ContainerObjectSelectionList.Entry hovered = (E)((ContainerObjectSelectionList.Entry)this.getHovered());
      NarrationTrigger narrationTrigger = output.narrationTrigger();
      if ((narrationTrigger == NarrationTrigger.MOUSE || narrationTrigger == NarrationTrigger.SYSTEM) && hovered != null) {
         hovered.updateNarration(output.nest());
         this.narrateListElementPosition(output, hovered);
      } else {
         ContainerObjectSelectionList.Entry focused = (E)((ContainerObjectSelectionList.Entry)this.getFocused());
         if (focused != null) {
            focused.updateNarration(output.nest());
            this.narrateListElementPosition(output, focused);
         }
      }

   }

   public abstract static class Entry extends AbstractSelectionList.Entry implements ContainerEventHandler {
      private @Nullable GuiEventListener focused;
      private @Nullable NarratableEntry lastNarratable;
      private boolean dragging;

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(final boolean dragging) {
         this.dragging = dragging;
      }

      public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
         return ContainerEventHandler.super.mouseClicked(event, doubleClick);
      }

      public void setFocused(final @Nullable GuiEventListener focused) {
         if (this.focused != null) {
            this.focused.setFocused(false);
         }

         if (focused != null) {
            focused.setFocused(true);
         }

         this.focused = focused;
      }

      public @Nullable GuiEventListener getFocused() {
         return this.focused;
      }

      public @Nullable ComponentPath focusPathAtIndex(final FocusNavigationEvent navigationEvent, final int currentIndex) {
         if (this.children().isEmpty()) {
            return null;
         } else {
            ComponentPath componentPath = ((GuiEventListener)this.children().get(Math.min(currentIndex, this.children().size() - 1))).nextFocusPath(navigationEvent);
            return ComponentPath.path(this, componentPath);
         }
      }

      public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
         if (navigationEvent instanceof FocusNavigationEvent.ArrowNavigation) {
            FocusNavigationEvent.ArrowNavigation arrowNavigation = (FocusNavigationEvent.ArrowNavigation)navigationEvent;
            byte var10000;
            switch (arrowNavigation.direction()) {
               case LEFT:
                  var10000 = -1;
                  break;
               case RIGHT:
                  var10000 = 1;
                  break;
               case UP:
               case DOWN:
                  var10000 = 0;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            int delta = var10000;
            if (delta == 0) {
               return null;
            }

            int index = Mth.clamp(delta + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

            for(int i = index; i >= 0 && i < this.children().size(); i += delta) {
               GuiEventListener child = (GuiEventListener)this.children().get(i);
               ComponentPath componentPath = child.nextFocusPath(navigationEvent);
               if (componentPath != null) {
                  return ComponentPath.path(this, componentPath);
               }
            }
         }

         return ContainerEventHandler.super.nextFocusPath(navigationEvent);
      }

      public abstract List narratables();

      void updateNarration(final NarrationElementOutput output) {
         List narratables = this.narratables();
         Screen.NarratableSearchResult result = Screen.findNarratableWidget(narratables, this.lastNarratable);
         if (result != null) {
            if (result.priority().isTerminal()) {
               this.lastNarratable = result.entry();
            }

            if (narratables.size() > 1) {
               output.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.object_list", result.index() + 1, narratables.size()));
            }

            result.entry().updateNarration(output.nest());
         }

      }
   }
}
