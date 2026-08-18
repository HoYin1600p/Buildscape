package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import java.util.Optional;

public interface ButtonListDialog extends Dialog {
   MapCodec codec();

   int columns();

   Optional exitAction();

   default Optional onCancel() {
      return this.exitAction().flatMap(ActionButton::action);
   }
}
