package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface ListModifier extends AttributeModifier {
   static ListModifier append() {
      return ListModifier.Append.INSTANCE;
   }

   default Codec argumentCodec(final EnvironmentAttribute attribute) {
      return attribute.valueCodec();
   }

   default LerpFunction argumentKeyframeLerp(final EnvironmentAttribute attribute) {
      return attribute.type().keyframeLerp();
   }

   public static record Append() implements ListModifier {
      private static final ListModifier.Append INSTANCE = new ListModifier.Append();

      public List apply(final List subject, final List argument) {
         if (argument.isEmpty()) {
            return subject;
         } else {
            return subject.isEmpty() ? argument : Util.join(subject, argument);
         }
      }
   }
}
