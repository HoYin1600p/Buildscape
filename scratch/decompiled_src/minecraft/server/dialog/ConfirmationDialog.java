package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;

public record ConfirmationDialog(CommonDialogData common, ActionButton yesButton, ActionButton noButton) implements SimpleDialog {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(CommonDialogData.MAP_CODEC.forGetter(ConfirmationDialog::common), ActionButton.CODEC.fieldOf("yes").forGetter(ConfirmationDialog::yesButton), ActionButton.CODEC.fieldOf("no").forGetter(ConfirmationDialog::noButton)).apply(i, ConfirmationDialog::new));

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Optional onCancel() {
      return this.noButton.action();
   }

   public List mainActions() {
      return List.of(this.yesButton, this.noButton);
   }
}
