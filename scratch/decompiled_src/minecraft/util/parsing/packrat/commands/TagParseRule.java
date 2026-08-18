package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import org.jspecify.annotations.Nullable;

public class TagParseRule implements Rule {
   private final TagParser parser;

   public TagParseRule(final DynamicOps ops) {
      this.parser = TagParser.create(ops);
   }

   public @Nullable Dynamic parse(final ParseState state) {
      ((StringReader)state.input()).skipWhitespace();
      int mark = state.mark();

      try {
         return new Dynamic(this.parser.getOps(), this.parser.parseAsArgument((StringReader)state.input()));
      } catch (Exception var4) {
         state.errorCollector().store(mark, var4);
         return null;
      }
   }
}
