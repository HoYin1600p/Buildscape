package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.CommonComponents;

public record NoticeDialog(CommonDialogData common, ActionButton action) implements SimpleDialog {
   public static final ActionButton DEFAULT_ACTION = new ActionButton(new CommonButtonData(CommonComponents.GUI_OK, 150), Optional.empty());
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(CommonDialogData.MAP_CODEC.forGetter(NoticeDialog::common), ActionButton.CODEC.optionalFieldOf("action", DEFAULT_ACTION).forGetter(NoticeDialog::action)).apply(i, NoticeDialog::new));

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Optional onCancel() {
      return this.action.action();
   }

   public List mainActions() {
      return List.of(this.action);
   }
}
