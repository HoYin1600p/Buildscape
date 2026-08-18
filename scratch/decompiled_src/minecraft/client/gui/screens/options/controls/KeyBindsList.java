package net.minecraft.client.gui.screens.options.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

public class KeyBindsList extends ContainerObjectSelectionList {
   private static final int ITEM_HEIGHT = 20;
   private final KeyBindsScreen keyBindsScreen;
   private int maxNameWidth;

   public KeyBindsList(final KeyBindsScreen keyBindsScreen, final Minecraft minecraft) {
      super(minecraft, keyBindsScreen.width, keyBindsScreen.layout.getContentHeight(), keyBindsScreen.layout.getHeaderHeight(), 20);
      this.keyBindsScreen = keyBindsScreen;
      KeyMapping[] keyMappings = (KeyMapping[])ArrayUtils.clone(minecraft.options.keyMappings);
      Arrays.sort(keyMappings);
      KeyMapping.Category previousCategory = null;

      for(KeyMapping key : keyMappings) {
         KeyMapping.Category category = key.getCategory();
         if (category != previousCategory) {
            previousCategory = category;
            this.addEntry(new KeyBindsList.CategoryEntry(category));
         }

         Component name = Component.translatable(key.getName());
         int width = minecraft.font.width(name);
         if (width > this.maxNameWidth) {
            this.maxNameWidth = width;
         }

         this.addEntry(new KeyBindsList.KeyEntry(key, name));
      }

   }

   public void resetMappingAndUpdateButtons() {
      KeyMapping.resetMapping();
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.children().forEach(KeyBindsList.Entry::refreshEntry);
   }

   public int getRowWidth() {
      return 340;
   }

   // $FF: synthetic method
   static Minecraft access$400(KeyBindsList x0) {
      return x0.minecraft;
   }

   public class CategoryEntry extends KeyBindsList.Entry {
      private final FocusableTextWidget categoryName;

      public CategoryEntry(final KeyMapping.Category category) {
         Objects.requireNonNull(KeyBindsList.this);
         super();
         this.categoryName = FocusableTextWidget.builder(category.label().copy().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD), KeyBindsList.this.minecraft.font).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS).build();
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         this.categoryName.setPosition(KeyBindsList.this.width / 2 - this.categoryName.getWidth() / 2, this.getContentBottom() - this.categoryName.getHeight());
         this.categoryName.extractRenderState(graphics, mouseX, mouseY, a);
      }

      public List children() {
         return List.of(this.categoryName);
      }

      public List narratables() {
         return List.of(this.categoryName);
      }

      public void refreshEntry() {
      }
   }

   public abstract static class Entry extends ContainerObjectSelectionList.Entry {
      public abstract void refreshEntry();
   }

   public class KeyEntry extends KeyBindsList.Entry {
      private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
      private static final int PADDING = 10;
      private final KeyMapping key;
      private final Component name;
      private final Button changeButton;
      private final Button resetButton;
      private boolean hasCollision;

      private KeyEntry(final KeyMapping key, final Component name) {
         Objects.requireNonNull(KeyBindsList.this);
         super();
         this.hasCollision = false;
         this.key = key;
         this.name = name;
         this.changeButton = Button.builder(name, (button) -> {
            KeyBindsList.this.keyBindsScreen.selectedKey = key;
            KeyBindsList.this.resetMappingAndUpdateButtons();
         }).bounds(0, 0, 75, 20).createNarration((defaultNarrationSupplier) -> key.isUnbound() ? Component.translatable("narrator.controls.unbound", name) : Component.translatable("narrator.controls.bound", name, defaultNarrationSupplier.get())).build();
         this.resetButton = Button.builder(RESET_BUTTON_TITLE, (button) -> {
            key.setKey(key.getDefaultKey());
            KeyBindsList.this.resetMappingAndUpdateButtons();
         }).bounds(0, 0, 50, 20).createNarration((defaultNarrationSupplier) -> Component.translatable("narrator.controls.reset", name)).build();
         this.refreshEntry();
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         this.updateChangeButtonMessage();
         int resetButtonX = KeyBindsList.this.scrollBarX() - this.resetButton.getWidth() - 10;
         int buttonY = this.getContentY() - 2;
         this.resetButton.setPosition(resetButtonX, buttonY);
         this.resetButton.extractRenderState(graphics, mouseX, mouseY, a);
         int changeButtonX = resetButtonX - 5 - this.changeButton.getWidth();
         this.changeButton.setPosition(changeButtonX, buttonY);
         this.changeButton.extractRenderState(graphics, mouseX, mouseY, a);
         graphics.text(KeyBindsList.this.minecraft.font, this.name, this.getContentX(), this.getContentYMiddle() - 9 / 2, -1);
         if (this.hasCollision) {
            int stripeWidth = 3;
            int stripeLeft = this.changeButton.getX() - 6;
            graphics.fill(stripeLeft, this.getContentY() - 1, stripeLeft + 3, this.getContentBottom(), -256);
         }

      }

      public List children() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      public List narratables() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      public void refreshEntry() {
         this.resetButton.active = !this.key.isDefault();
         this.hasCollision = false;
         MutableComponent tooltip = Component.empty();
         if (!this.key.isUnbound()) {
            for(KeyMapping otherKey : KeyBindsList.this.minecraft.options.keyMappings) {
               if (otherKey != this.key && this.key.same(otherKey) && (!otherKey.isDefault() || !this.key.isDefault())) {
                  if (this.hasCollision) {
                     tooltip.append(", ");
                  }

                  this.hasCollision = true;
                  tooltip.append(Component.translatable(otherKey.getName()));
               }
            }
         }

         if (this.hasCollision) {
            this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", tooltip)));
         } else {
            this.changeButton.setTooltip((Tooltip)null);
         }

         this.updateChangeButtonMessage();
      }

      private void updateChangeButtonMessage() {
         Component message = this.key.getTranslatedKeyMessage();
         if (this.hasCollision) {
            message = Component.literal("[ ").append(message.copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.YELLOW);
         }

         if (KeyBindsList.this.keyBindsScreen.selectedKey == this.key) {
            message = Component.literal("> ").append(message.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW);
         }

         this.changeButton.setMessage(message);
      }
   }
}
