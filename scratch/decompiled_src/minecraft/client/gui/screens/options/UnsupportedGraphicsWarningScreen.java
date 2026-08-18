package net.minecraft.client.gui.screens.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

public class UnsupportedGraphicsWarningScreen extends Screen {
   private static final int BUTTON_PADDING = 20;
   private static final int BUTTON_MARGIN = 5;
   private static final int BUTTON_HEIGHT = 20;
   private final Component narrationMessage;
   private final List message;
   private final ImmutableList buttonOptions;
   private MultiLineLabel messageLines = MultiLineLabel.EMPTY;
   private int contentTop;
   private int buttonWidth;

   protected UnsupportedGraphicsWarningScreen(final Component title, final List message, final ImmutableList buttonOptions) {
      super(title);
      this.message = message;
      this.narrationMessage = CommonComponents.joinForNarration(title, ComponentUtils.formatList(message, CommonComponents.EMPTY));
      this.buttonOptions = buttonOptions;
   }

   public Component getNarrationMessage() {
      return this.narrationMessage;
   }

   public void init() {
      UnsupportedGraphicsWarningScreen.ButtonOption buttonOption;
      for(UnmodifiableIterator buttonAdvance = this.buttonOptions.iterator(); buttonAdvance.hasNext(); this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width(buttonOption.message) + 20)) {
         buttonOption = (UnsupportedGraphicsWarningScreen.ButtonOption)buttonAdvance.next();
      }

      int buttonAdvance = 5 + this.buttonWidth + 5;
      int contentWidth = buttonAdvance * this.buttonOptions.size();
      this.messageLines = MultiLineLabel.create(this.font, contentWidth, (Component[])this.message.toArray(new Component[0]));
      int messageHeight = this.messageLines.getLineCount() * 9;
      this.contentTop = (int)((double)this.height / 2.0D - (double)messageHeight / 2.0D);
      int buttonTop = this.contentTop + messageHeight + 9 * 2;
      int x = (int)((double)this.width / 2.0D - (double)contentWidth / 2.0D);

      for(UnmodifiableIterator var6 = this.buttonOptions.iterator(); var6.hasNext(); x += buttonAdvance) {
         UnsupportedGraphicsWarningScreen.ButtonOption buttonOption = (UnsupportedGraphicsWarningScreen.ButtonOption)var6.next();
         this.addRenderableWidget(Button.builder(buttonOption.message, buttonOption.onPress).bounds(x, buttonTop, this.buttonWidth, 20).build());
      }

   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      graphics.centeredText(this.font, this.title, this.width / 2, this.contentTop - 9 * 2, -1);
      this.messageLines.visitLines(TextAlignment.CENTER, this.width / 2, this.contentTop, 9, textRenderer);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public static final class ButtonOption {
      private final Component message;
      private final Button.OnPress onPress;

      public ButtonOption(final Component message, final Button.OnPress onPress) {
         this.message = message;
         this.onPress = onPress;
      }
   }
}
