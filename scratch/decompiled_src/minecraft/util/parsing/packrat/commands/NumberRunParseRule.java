package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import org.jspecify.annotations.Nullable;

public abstract class NumberRunParseRule implements Rule {
   private final DelayedException noValueError;
   private final DelayedException underscoreNotAllowedError;

   public NumberRunParseRule(final DelayedException noValueError, final DelayedException underscoreNotAllowedError) {
      this.noValueError = noValueError;
      this.underscoreNotAllowedError = underscoreNotAllowedError;
   }

   public @Nullable String parse(final ParseState state) {
      StringReader input = (StringReader)state.input();
      input.skipWhitespace();
      String fullString = input.getString();
      int start = input.getCursor();

      int pos;
      for(pos = start; pos < fullString.length() && this.isAccepted(fullString.charAt(pos)); ++pos) {
      }

      int length = pos - start;
      if (length == 0) {
         state.errorCollector().store(state.mark(), this.noValueError);
         return null;
      } else if (fullString.charAt(start) != '_' && fullString.charAt(pos - 1) != '_') {
         input.setCursor(pos);
         return fullString.substring(start, pos);
      } else {
         state.errorCollector().store(state.mark(), this.underscoreNotAllowedError);
         return null;
      }
   }

   protected abstract boolean isAccepted(char c);
}
