package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public class KeybindContents implements ComponentContents {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("keybind").forGetter((o) -> o.name)).apply(i, KeybindContents::new));
   private final String name;
   private @Nullable Supplier nameResolver;

   public KeybindContents(final String name) {
      this.name = name;
   }

   private Component getNestedComponent() {
      if (this.nameResolver == null) {
         this.nameResolver = (Supplier)KeybindResolver.keyResolver.apply(this.name);
      }

      return (Component)this.nameResolver.get();
   }

   public Optional visit(final FormattedText.ContentConsumer output) {
      return this.getNestedComponent().visit(output);
   }

   public Optional visit(final FormattedText.StyledContentConsumer output, final Style currentStyle) {
      return this.getNestedComponent().visit(output, currentStyle);
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         if (o instanceof KeybindContents) {
            KeybindContents that = (KeybindContents)o;
            if (this.name.equals(that.name)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String toString() {
      return "keybind{" + this.name + "}";
   }

   public String getName() {
      return this.name;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }
}
