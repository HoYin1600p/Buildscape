package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;

public interface Layout extends LayoutElement {
   void visitChildren(Consumer layoutElementVisitor);

   default void visitWidgets(final Consumer widgetVisitor) {
      this.visitChildren((child) -> child.visitWidgets(widgetVisitor));
   }

   default void arrangeElements() {
      this.visitChildren((child) -> {
         if (child instanceof Layout layout) {
            layout.arrangeElements();
         }

      });
   }

   void removeChildren();
}
