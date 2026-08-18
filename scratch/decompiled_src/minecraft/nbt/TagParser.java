package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.commands.Grammar;

public class TagParser {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(Component.translatable("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_COMPOUND = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.compound"));
   public static final char ELEMENT_SEPARATOR = ',';
   public static final char NAME_VALUE_SEPARATOR = ':';
   private static final TagParser NBT_OPS_PARSER = create(NbtOps.INSTANCE);
   public static final Codec FLATTENED_CODEC = Codec.STRING.comapFlatMap((s) -> {
      try {
         Tag result = (Tag)NBT_OPS_PARSER.parseFully(s);
         if (result instanceof CompoundTag compoundTag) {
            return DataResult.success(compoundTag, Lifecycle.stable());
         } else {
            return DataResult.error(() -> "Expected compound tag, got " + String.valueOf(result));
         }
      } catch (CommandSyntaxException var3) {
         return DataResult.error(var3::getMessage);
      }
   }, CompoundTag::toString);
   public static final Codec LENIENT_CODEC = Codec.withAlternative(FLATTENED_CODEC, CompoundTag.CODEC);
   private final DynamicOps ops;
   private final Grammar grammar;

   private TagParser(final DynamicOps ops, final Grammar grammar) {
      this.ops = ops;
      this.grammar = grammar;
   }

   public DynamicOps getOps() {
      return this.ops;
   }

   public static TagParser create(final DynamicOps ops) {
      return new TagParser(ops, SnbtGrammar.createParser(ops));
   }

   private static CompoundTag castToCompoundOrThrow(final StringReader reader, final Tag result) throws CommandSyntaxException {
      if (result instanceof CompoundTag) {
         return (CompoundTag)result;
      } else {
         throw ERROR_EXPECTED_COMPOUND.createWithContext(reader);
      }
   }

   public static CompoundTag parseCompoundFully(final String input) throws CommandSyntaxException {
      StringReader reader = new StringReader(input);
      return castToCompoundOrThrow(reader, (Tag)NBT_OPS_PARSER.parseFully(reader));
   }

   public Object parseFully(final String input) throws CommandSyntaxException {
      return this.parseFully(new StringReader(input));
   }

   public Object parseFully(final StringReader reader) throws CommandSyntaxException {
      Object result = (T)this.grammar.parseForCommands(reader);
      reader.skipWhitespace();
      if (reader.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(reader);
      } else {
         return result;
      }
   }

   public Object parseAsArgument(final StringReader reader) throws CommandSyntaxException {
      return this.grammar.parseForCommands(reader);
   }

   public static CompoundTag parseCompoundAsArgument(final StringReader reader) throws CommandSyntaxException {
      Tag result = (Tag)NBT_OPS_PARSER.parseAsArgument(reader);
      return castToCompoundOrThrow(reader, result);
   }
}
