package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.TeamColor;

public class TeamColorArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("red", "green");
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.color.invalid", value));

   private TeamColorArgument() {
   }

   public static TeamColorArgument teamColor() {
      return new TeamColorArgument();
   }

   public static TeamColor getTeamColor(final CommandContext context, final String name) {
      return (TeamColor)context.getArgument(name, TeamColor.class);
   }

   public TeamColor parse(final StringReader reader) throws CommandSyntaxException {
      String id = reader.readUnquotedString();
      TeamColor result = TeamColor.byName(id);
      if (result == null) {
         throw ERROR_INVALID_VALUE.createWithContext(reader, id);
      } else {
         return result;
      }
   }

   public CompletableFuture listSuggestions(final CommandContext contextBuilder, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(TeamColor.VALUES.stream().map(TeamColor::getSerializedName), builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }
}
