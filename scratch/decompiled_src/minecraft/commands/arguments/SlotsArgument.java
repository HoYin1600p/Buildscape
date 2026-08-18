package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;

public class SlotsArgument implements ArgumentType {
   static final Collection EXAMPLES = List.of("container.*", "container.5", "weapon");

   public static SlotsArgument slots() {
      return new SlotsArgument();
   }

   public static SlotRange getSlots(final CommandContext context, final String name) {
      return (SlotRange)context.getArgument(name, SlotRange.class);
   }

   public SlotRange parse(final StringReader reader) throws CommandSyntaxException {
      return SlotRanges.read(reader);
   }

   public CompletableFuture listSuggestions(final CommandContext contextBuilder, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(SlotRanges.allNames(), builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }
}
