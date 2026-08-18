package net.minecraft.util.parsing.packrat;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;

public interface DelayedException {
   Exception create(String contents, int position);

   static DelayedException create(final SimpleCommandExceptionType type) {
      return (contents, position) -> type.createWithContext(StringReaderTerms.createReader(contents, position));
   }

   static DelayedException create(final DynamicCommandExceptionType type, final String argument) {
      return (contents, position) -> type.createWithContext(StringReaderTerms.createReader(contents, position), argument);
   }
}
