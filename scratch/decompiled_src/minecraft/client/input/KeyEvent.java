package net.minecraft.client.input;

import com.mojang.blaze3d.platform.InputConstants.Value;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public record KeyEvent(@Value int key, int keycode, @InputWithModifiers.Modifiers int modifiers) implements InputWithModifiers {
   public int input() {
      return this.key;
   }

   public int shortcutKey() {
      return this.keycode;
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface Action {
   }
}
