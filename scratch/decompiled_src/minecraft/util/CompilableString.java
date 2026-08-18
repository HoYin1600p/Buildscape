package net.minecraft.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class CompilableString {
   private final String source;
   private final Object compiled;

   private CompilableString(final String source, final Object compiled) {
      this.source = source;
      this.compiled = compiled;
   }

   public static Codec codec(final Function compiler) {
      return Codec.STRING.comapFlatMap((s) -> ((DataResult)compiler.apply(s)).map((compiled) -> new CompilableString(s, compiled)), CompilableString::source);
   }

   public String source() {
      return this.source;
   }

   public Object compiled() {
      return this.compiled;
   }

   public boolean equals(final Object o) {
      if (o instanceof CompilableString that) {
         if (Objects.equals(this.source, that.source)) {
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      return this.source.hashCode();
   }

   public String toString() {
      return this.source;
   }

   public abstract static class CommandParserHelper implements Function {
      private static final DynamicCommandExceptionType TRAILING_DATA = new DynamicCommandExceptionType((commandAndRemainder) -> Component.translatableEscape("command.trailing_data", commandAndRemainder));

      public final DataResult apply(final String contents) {
         StringReader reader = new StringReader(contents);

         try {
            Object result = (T)this.parse(reader);
            if (reader.canRead()) {
               String parsed = reader.getString().substring(0, reader.getCursor());
               String leftovers = reader.getString().substring(reader.getCursor());
               throw TRAILING_DATA.create(parsed + "[" + leftovers + "]");
            } else {
               return DataResult.success(result);
            }
         } catch (CommandSyntaxException var6) {
            return DataResult.error(() -> this.errorMessage(contents, var6));
         }
      }

      protected abstract Object parse(StringReader reader) throws CommandSyntaxException;

      protected abstract String errorMessage(String original, CommandSyntaxException exception);
   }
}
