package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class GenericMessageScreen extends Screen {
   private @Nullable FocusableTextWidget textWidget;

   public GenericMessageScreen(final Component title) {
      super(title);
   }

   protected void init() {
      this.textWidget = (FocusableTextWidget)this.addRenderableWidget(FocusableTextWidget.builder(this.title, this.font, 12).textWidth(this.font.width(this.title)).build());
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.textWidget != null) {
         this.textWidget.setPosition(this.width / 2 - this.textWidget.getWidth() / 2, this.height / 2 - 9 / 2);
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean shouldNarrateNavigation() {
      return false;
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      this.extractPanorama(graphics, a);
      this.extractBlurredBackground(graphics);
      this.extractMenuBackground(graphics);
   }
}
