package net.minecraft.server.packs.repository;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public interface PackSource {
   UnaryOperator NO_DECORATION = UnaryOperator.identity();
   PackSource DEFAULT = create(NO_DECORATION, true);
   PackSource BUILT_IN = create(decorateWithSource("pack.source.builtin"), true);
   PackSource FEATURE = create(decorateWithSource("pack.source.feature"), false);
   PackSource WORLD = create(decorateWithSource("pack.source.world"), true);
   PackSource SERVER = create(decorateWithSource("pack.source.server"), true);

   Component decorate(final Component packDescription);

   boolean shouldAddAutomatically();

   static PackSource create(final UnaryOperator decorator, final boolean addAutomatically) {
      return new PackSource() {
         public Component decorate(final Component packDescription) {
            return (Component)decorator.apply(packDescription);
         }

         public boolean shouldAddAutomatically() {
            return addAutomatically;
         }
      };
   }

   private static UnaryOperator decorateWithSource(final String descriptionId) {
      Component description = Component.translatable(descriptionId);
      return (packDescription) -> Component.translatable("pack.nameAndSource", packDescription, description).withStyle(ChatFormatting.GRAY);
   }
}
