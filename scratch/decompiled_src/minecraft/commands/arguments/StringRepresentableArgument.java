package net.minecraft.commands.arguments;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public class StringRepresentableArgument implements ArgumentType {
   private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.enum.invalid", value));
   private final Codec codec;
   private final Supplier values;

   protected StringRepresentableArgument(final Codec codec, final Supplier values) {
      this.codec = codec;
      this.values = values;
   }

   public Enum parse(final StringReader reader) throws CommandSyntaxException {
      String id = reader.readUnquotedString();
      return (Enum)this.codec.parse(JsonOps.INSTANCE, new JsonPrimitive(id)).result().orElseThrow(() -> ERROR_INVALID_VALUE.createWithContext(reader, id));
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest((Iterable)Arrays.stream((Enum[])this.values.get()).map((rec$) -> ((StringRepresentable)rec$).getSerializedName()).map(this::convertId).collect(Collectors.toList()), builder);
   }

   public Collection getExamples() {
      return (Collection)Arrays.stream((Enum[])this.values.get()).map((rec$) -> ((StringRepresentable)rec$).getSerializedName()).map(this::convertId).limit(2L).collect(Collectors.toList());
   }

   protected String convertId(final String id) {
      return id;
   }
}
