package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.dialog.action.Action;

public record ActionButton(CommonButtonData button, Optional action) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(CommonButtonData.MAP_CODEC.forGetter(ActionButton::button), Action.CODEC.optionalFieldOf("action").forGetter(ActionButton::action)).apply(i, ActionButton::new));
}
