package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;

public record CommandTemplate(ParsedTemplate template) implements Action {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ParsedTemplate.CODEC.fieldOf("template").forGetter(CommandTemplate::template)).apply(i, CommandTemplate::new));

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Optional createAction(final Map parameters) {
      String command = this.template.instantiate(Action.ValueGetter.getAsTemplateSubstitutions(parameters));
      return Optional.of(new ClickEvent.RunCommand(command));
   }
}
