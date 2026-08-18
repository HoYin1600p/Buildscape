package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;
import org.jspecify.annotations.Nullable;

public class SnbtOperations {
   private static final DelayedException ERROR_EXPECTED_STRING_UUID = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_string_uuid")));
   private static final DelayedException ERROR_EXPECTED_NUMBER_OR_BOOLEAN = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_number_or_boolean")));
   public static final String BUILTIN_TRUE = "true";
   public static final String BUILTIN_FALSE = "false";
   public static final Map BUILTIN_OPERATIONS = Map.of(new SnbtOperations.BuiltinKey("bool", 1), new SnbtOperations.BuiltinOperation() {
      public Object run(final DynamicOps ops, final List arguments, final ParseState state) {
         Boolean result = convert(ops, arguments.getFirst());
         if (result == null) {
            state.errorCollector().store(state.mark(), SnbtOperations.ERROR_EXPECTED_NUMBER_OR_BOOLEAN);
            return null;
         } else {
            return ops.createBoolean(result);
         }
      }

      private static @Nullable Boolean convert(final DynamicOps ops, final Object arg) {
         Optional asBoolean = ops.getBooleanValue(arg).result();
         if (asBoolean.isPresent()) {
            return (Boolean)asBoolean.get();
         } else {
            Optional asNumber = ops.getNumberValue(arg).result();
            return asNumber.isPresent() ? ((Number)asNumber.get()).doubleValue() != 0.0D : null;
         }
      }
   }, new SnbtOperations.BuiltinKey("uuid", 1), new SnbtOperations.BuiltinOperation() {
      public Object run(final DynamicOps ops, final List arguments, final ParseState state) {
         Optional arg = ops.getStringValue(arguments.getFirst()).result();
         if (arg.isEmpty()) {
            state.errorCollector().store(state.mark(), SnbtOperations.ERROR_EXPECTED_STRING_UUID);
            return null;
         } else {
            UUID uuid;
            try {
               uuid = UUID.fromString((String)arg.get());
            } catch (IllegalArgumentException var7) {
               state.errorCollector().store(state.mark(), SnbtOperations.ERROR_EXPECTED_STRING_UUID);
               return null;
            }

            return ops.createIntList(IntStream.of(UUIDUtil.uuidToIntArray(uuid)));
         }
      }
   });
   public static final SuggestionSupplier BUILTIN_IDS = new SuggestionSupplier() {
      private final Set keys = (Set)Stream.concat(Stream.of("false", "true"), SnbtOperations.BUILTIN_OPERATIONS.keySet().stream().map(SnbtOperations.BuiltinKey::id)).collect(Collectors.toSet());

      public Stream possibleValues(final ParseState state) {
         return this.keys.stream();
      }
   };

   public static record BuiltinKey(String id, int argCount) {
      public String toString() {
         return this.id + "/" + this.argCount;
      }
   }

   public interface BuiltinOperation {
      @Nullable Object run(DynamicOps ops, List arguments, ParseState state);
   }
}
