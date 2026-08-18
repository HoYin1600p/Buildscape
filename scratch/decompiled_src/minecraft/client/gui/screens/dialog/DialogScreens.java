package net.minecraft.client.gui.screens.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.ServerLinksDialog;
import org.jspecify.annotations.Nullable;

public class DialogScreens {
   private static final Map FACTORIES = new HashMap();

   private static void register(final MapCodec type, final DialogScreens.Factory factory) {
      FACTORIES.put(type, factory);
   }

   public static @Nullable DialogScreen createFromData(final Dialog dialog, final @Nullable Screen previousScreen, final DialogConnectionAccess connectionAccess) {
      DialogScreens.Factory factory = (DialogScreens.Factory)FACTORIES.get(dialog.codec());
      return factory != null ? factory.create(previousScreen, dialog, connectionAccess) : null;
   }

   public static void bootstrap() {
      register(ConfirmationDialog.MAP_CODEC, SimpleDialogScreen::new);
      register(NoticeDialog.MAP_CODEC, SimpleDialogScreen::new);
      register(DialogListDialog.MAP_CODEC, DialogListDialogScreen::new);
      register(MultiActionDialog.MAP_CODEC, MultiButtonDialogScreen::new);
      register(ServerLinksDialog.MAP_CODEC, ServerLinksDialogScreen::new);
   }

   @FunctionalInterface
   public interface Factory {
      DialogScreen create(@Nullable Screen previousScreen, Dialog data, DialogConnectionAccess connectionAccess);
   }
}
