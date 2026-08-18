package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.Mirror;

public class TemplateMirrorArgument extends StringRepresentableArgument {
   private TemplateMirrorArgument() {
      super(Mirror.CODEC, Mirror::values);
   }

   public static StringRepresentableArgument templateMirror() {
      return new TemplateMirrorArgument();
   }

   public static Mirror getMirror(final CommandContext context, final String name) {
      return (Mirror)context.getArgument(name, Mirror.class);
   }
}
