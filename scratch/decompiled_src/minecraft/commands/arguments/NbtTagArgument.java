package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.nbt.Tag;
import net.minecraft.util.parsing.packrat.commands.CommandArgumentParser;
import net.minecraft.util.parsing.packrat.commands.ParserBasedArgument;

public class NbtTagArgument extends ParserBasedArgument {
   private static final Collection EXAMPLES = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");
   private static final CommandArgumentParser TAG_PARSER = SnbtGrammar.createParser(NbtOps.INSTANCE);

   private NbtTagArgument() {
      super(TAG_PARSER);
   }

   public static NbtTagArgument nbtTag() {
      return new NbtTagArgument();
   }

   public static Tag getNbtTag(final CommandContext context, final String name) {
      return (Tag)context.getArgument(name, Tag.class);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }
}
