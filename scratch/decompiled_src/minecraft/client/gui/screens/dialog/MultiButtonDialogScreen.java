package net.minecraft.client.gui.screens.dialog;

import java.util.stream.Stream;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.MultiActionDialog;
import org.jspecify.annotations.Nullable;

public class MultiButtonDialogScreen extends ButtonListDialogScreen {
   public MultiButtonDialogScreen(final @Nullable Screen previousScreen, final MultiActionDialog dialog, final DialogConnectionAccess connectionAccess) {
      super(previousScreen, dialog, connectionAccess);
   }

   protected Stream createListActions(final MultiActionDialog dialog, final DialogConnectionAccess connectionAccess) {
      return dialog.actions().stream();
   }
}
