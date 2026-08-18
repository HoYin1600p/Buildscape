package net.minecraft.server.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.ExtraCodecs;

public record SingleOptionInput(int width, List entries, Component label, boolean labelVisible) implements InputControl {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(SingleOptionInput::width), ExtraCodecs.nonEmptyList(SingleOptionInput.Entry.CODEC.listOf()).fieldOf("options").forGetter(SingleOptionInput::entries), ComponentSerialization.CODEC.fieldOf("label").forGetter(SingleOptionInput::label), Codec.BOOL.optionalFieldOf("label_visible", true).forGetter(SingleOptionInput::labelVisible)).apply(i, SingleOptionInput::new)).validate((o) -> {
      long initialCount = o.entries.stream().filter(SingleOptionInput.Entry::initial).count();
      return initialCount > 1L ? DataResult.error(() -> "Multiple initial values") : DataResult.success(o);
   });

   public MapCodec mapCodec() {
      return MAP_CODEC;
   }

   public Optional initial() {
      return this.entries.stream().filter(SingleOptionInput.Entry::initial).findFirst();
   }

   public static record Entry(String id, Optional display, boolean initial) {
      public static final Codec FULL_CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("id").forGetter(SingleOptionInput.Entry::id), ComponentSerialization.CODEC.optionalFieldOf("display").forGetter(SingleOptionInput.Entry::display), Codec.BOOL.optionalFieldOf("initial", false).forGetter(SingleOptionInput.Entry::initial)).apply(i, SingleOptionInput.Entry::new));
      public static final Codec CODEC = Codec.withAlternative(FULL_CODEC, Codec.STRING, (id) -> new SingleOptionInput.Entry(id, Optional.empty(), false));

      public Component displayOrDefault() {
         return (Component)this.display.orElseGet(() -> Component.literal(this.id));
      }
   }
}
