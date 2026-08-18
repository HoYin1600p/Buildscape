package net.minecraft.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Map;
import net.minecraft.util.Util;
import org.lwjgl.sdl.SDLKeyboard;

public class InputQuirks {
   private static final Util.OS PLATFORM = Util.getPlatform();
   private static final boolean ON_WINDOWS = PLATFORM == Util.OS.WINDOWS;
   private static final boolean ON_OSX = PLATFORM == Util.OS.OSX;
   public static final boolean REPLACE_CTRL_KEY_WITH_CMD_KEY = ON_OSX;
   public static final int EDIT_SHORTCUT_KEY_MODIFIER = REPLACE_CTRL_KEY_WITH_CMD_KEY ? 3072 : 192;
   public static final boolean SIMULATE_RIGHT_CLICK_WITH_LONG_LEFT_CLICK = ON_OSX;
   public static final boolean SHIFT_INVERTS_SCROLL_AXIS = ON_OSX;
   public static final boolean RESTORE_KEY_STATE_AFTER_MOUSE_GRAB = !ON_OSX;
   private static final Map KEYBOARD_DISPLAY_OVERRIDES;

   public static boolean isQuitShortcutDown() {
      int modifiers = SDLKeyboard.SDL_GetModState();
      return ON_OSX ? (modifiers & 3072) != 0 && InputConstants.isKeyDown(20) : (modifiers & 768) != 0 && InputConstants.isKeyDown(61);
   }

   public static String keyboardTranslationKey(final String name) {
      return (String)KEYBOARD_DISPLAY_OVERRIDES.getOrDefault(name, name);
   }

   static {
      Map var10000;
      switch (PLATFORM) {
         case OSX:
            var10000 = Map.of("key.keyboard.left.alt", "key.keyboard.left.option", "key.keyboard.right.alt", "key.keyboard.right.option", "key.keyboard.left.win", "key.keyboard.left.command", "key.keyboard.right.win", "key.keyboard.right.command");
            break;
         case WINDOWS:
            var10000 = Map.of("key.keyboard.left.win", "key.keyboard.left.windows", "key.keyboard.right.win", "key.keyboard.right.windows");
            break;
         default:
            var10000 = Map.of();
      }

      KEYBOARD_DISPLAY_OVERRIDES = var10000;
   }
}
