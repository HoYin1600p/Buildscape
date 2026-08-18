package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class SwizzleArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("arguments.swizzle.invalid"));

   public static SwizzleArgument swizzle() {
      return new SwizzleArgument();
   }

   public static EnumSet getSwizzle(final CommandContext context, final String name) {
      return (EnumSet)context.getArgument(name, EnumSet.class);
   }

   public EnumSet parse(final StringReader reader) throws CommandSyntaxException {
      EnumSet result = EnumSet.noneOf(Direction.Axis.class);

      while(reader.canRead() && reader.peek() != ' ') {
         char c = reader.read();
         Direction.Axis var10000;
         switch (c) {
            case 'x':
               var10000 = Direction.Axis.X;
               break;
            case 'y':
               var10000 = Direction.Axis.Y;
               break;
            case 'z':
               var10000 = Direction.Axis.Z;
               break;
            default:
               throw ERROR_INVALID.createWithContext(reader);
         }

         Direction.Axis axis = var10000;
         if (result.contains(axis)) {
            throw ERROR_INVALID.createWithContext(reader);
         }

         result.add(axis);
      }

      return result;
   }

   public Collection getExamples() {
      return EXAMPLES;
   }
}
