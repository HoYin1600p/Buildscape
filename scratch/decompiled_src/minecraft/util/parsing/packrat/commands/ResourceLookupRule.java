package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.resources.Identifier;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import org.jspecify.annotations.Nullable;

public abstract class ResourceLookupRule implements Rule, ResourceSuggestion {
   private final NamedRule idParser;
   protected final Object context;
   private final DelayedException error;

   protected ResourceLookupRule(final NamedRule idParser, final Object context) {
      this.idParser = idParser;
      this.context = context;
      this.error = DelayedException.create(Identifier.ERROR_INVALID);
   }

   public @Nullable Object parse(final ParseState state) {
      ((StringReader)state.input()).skipWhitespace();
      int mark = state.mark();
      Identifier id = (Identifier)state.parse(this.idParser);
      if (id != null) {
         try {
            return this.validateElement((ImmutableStringReader)state.input(), id);
         } catch (Exception var5) {
            state.errorCollector().store(mark, this, var5);
            return null;
         }
      } else {
         state.errorCollector().store(mark, this, this.error);
         return null;
      }
   }

   protected abstract Object validateElement(ImmutableStringReader reader, Identifier id) throws Exception;
}
