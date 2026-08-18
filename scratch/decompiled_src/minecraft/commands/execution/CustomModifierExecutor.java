package net.minecraft.commands.execution;

import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;

public interface CustomModifierExecutor {
   void apply(Object originalSource, List currentSources, ContextChain currentStep, ChainModifiers modifiers, ExecutionControl output);

   public interface ModifierAdapter extends CustomModifierExecutor, RedirectModifier {
      default Collection apply(final CommandContext context) throws CommandSyntaxException {
         throw new UnsupportedOperationException("This function should not run");
      }
   }
}
