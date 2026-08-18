package net.minecraft.commands.functions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

class FunctionBuilder {
   private @Nullable List plainEntries = new ArrayList();
   private @Nullable List macroEntries;
   private final List macroArguments = new ArrayList();

   public void addCommand(final UnboundEntryAction command) {
      if (this.macroEntries != null) {
         this.macroEntries.add(new MacroFunction.PlainTextEntry(command));
      } else {
         this.plainEntries.add(command);
      }

   }

   private int getArgumentIndex(final String id) {
      int index = this.macroArguments.indexOf(id);
      if (index == -1) {
         index = this.macroArguments.size();
         this.macroArguments.add(id);
      }

      return index;
   }

   private IntList convertToIndices(final List ids) {
      IntArrayList result = new IntArrayList(ids.size());

      for(String id : ids) {
         result.add(this.getArgumentIndex(id));
      }

      return result;
   }

   public void addMacro(final String command, final int line, final ExecutionCommandSource compilationContext) {
      StringTemplate parseResults;
      try {
         parseResults = StringTemplate.fromString(command);
      } catch (Exception var7) {
         throw new IllegalArgumentException("Can't parse function line " + line + ": '" + command + "'", var7);
      }

      if (this.plainEntries != null) {
         this.macroEntries = new ArrayList(this.plainEntries.size() + 1);

         for(UnboundEntryAction plainEntry : this.plainEntries) {
            this.macroEntries.add(new MacroFunction.PlainTextEntry(plainEntry));
         }

         this.plainEntries = null;
      }

      this.macroEntries.add(new MacroFunction.MacroEntry(parseResults, this.convertToIndices(parseResults.variables()), compilationContext));
   }

   public CommandFunction build(final Identifier id) {
      return (CommandFunction)(this.macroEntries != null ? new MacroFunction(id, this.macroEntries, this.macroArguments) : new PlainTextFunction(id, this.plainEntries));
   }
}
