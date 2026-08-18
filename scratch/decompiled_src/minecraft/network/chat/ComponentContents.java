package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Optional;

public interface ComponentContents {
   default Optional visit(final FormattedText.StyledContentConsumer output, final Style currentStyle) {
      return Optional.empty();
   }

   default Optional visit(final FormattedText.ContentConsumer output) {
      return Optional.empty();
   }

   default MutableComponent resolve(final ResolutionContext context, final int recursionDepth) throws CommandSyntaxException {
      return MutableComponent.create(this);
   }

   MapCodec codec();
}
