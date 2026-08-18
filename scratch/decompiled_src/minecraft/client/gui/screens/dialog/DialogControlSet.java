package net.minecraft.client.gui.screens.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.dialog.input.InputControlHandlers;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.action.Action;

public class DialogControlSet {
   public static final Supplier EMPTY_ACTION = Optional::empty;
   private final DialogScreen screen;
   private final Map valueGetters = new HashMap();

   public DialogControlSet(final DialogScreen screen) {
      this.screen = screen;
   }

   public void addInput(final Input data, final Consumer output) {
      String key = data.key();
      InputControlHandlers.createHandler(data.control(), this.screen, (element, valueGetter) -> {
         this.valueGetters.put(key, valueGetter);
         output.accept(element);
      });
   }

   private static Button.Builder createDialogButton(final CommonButtonData data, final Button.OnPress clickAction) {
      Button.Builder result = Button.builder(data.label(), clickAction);
      result.width(data.width());
      if (data.tooltip().isPresent()) {
         result = result.tooltip(Tooltip.create((Component)data.tooltip().get()));
      }

      return result;
   }

   public Supplier bindAction(final Optional maybeAction) {
      if (maybeAction.isPresent()) {
         Action action = (Action)maybeAction.get();
         return () -> action.createAction(this.valueGetters);
      } else {
         return EMPTY_ACTION;
      }
   }

   public Button.Builder createActionButton(final ActionButton actionButton) {
      Supplier action = this.bindAction(actionButton.action());
      return createDialogButton(actionButton.button(), (button) -> this.screen.runAction((Optional)action.get()));
   }
}
