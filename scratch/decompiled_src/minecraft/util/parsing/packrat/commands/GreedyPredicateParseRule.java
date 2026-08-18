package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import org.jspecify.annotations.Nullable;

public abstract class GreedyPredicateParseRule implements Rule {
   private final int minSize;
   private final int maxSize;
   private final DelayedException error;

   public GreedyPredicateParseRule(final int minSize, final DelayedException error) {
      this(minSize, Integer.MAX_VALUE, error);
   }

   public GreedyPredicateParseRule(final int minSize, final int maxSize, final DelayedException error) {
      this.minSize = minSize;
      this.maxSize = maxSize;
      this.error = error;
   }

   public @Nullable String parse(final ParseState state) {
      StringReader input = (StringReader)state.input();
      String fullString = input.getString();
      int start = input.getCursor();

      int pos;
      for(pos = start; pos < fullString.length() && this.isAccepted(fullString.charAt(pos)) && pos - start < this.maxSize; ++pos) {
      }

      int length = pos - start;
      if (length < this.minSize) {
         state.errorCollector().store(state.mark(), this.error);
         return null;
      } else {
         input.setCursor(pos);
         return fullString.substring(start, pos);
      }
   }

   protected abstract boolean isAccepted(char c);
}
