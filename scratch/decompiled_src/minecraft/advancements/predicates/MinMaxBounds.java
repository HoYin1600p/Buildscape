package net.minecraft.advancements.predicates;

import com.google.common.collect.Range;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public interface MinMaxBounds {
   SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(Component.translatable("argument.range.empty"));
   SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(Component.translatable("argument.range.swapped"));

   MinMaxBounds.Bounds bounds();

   default Optional min() {
      return this.bounds().min;
   }

   default Optional max() {
      return this.bounds().max;
   }

   default boolean isAny() {
      return this.bounds().isAny();
   }

   static Function validateContainedInRange(final MinMaxBounds allowed) {
      Range allowedRange = allowed.bounds().asRange();
      return (target) -> {
         Range selfAsRange = target.bounds().asRange();
         return !allowedRange.encloses(selfAsRange) ? DataResult.error(() -> "Range must be within " + String.valueOf(allowedRange) + ", but was " + String.valueOf(selfAsRange)) : DataResult.success(target);
      };
   }

   public static record Bounds(Optional min, Optional max) {
      public boolean isAny() {
         return this.min().isEmpty() && this.max().isEmpty();
      }

      public DataResult validateSwappedBoundsInCodec() {
         return this.areSwapped() ? DataResult.error(() -> "Swapped bounds in range: " + String.valueOf(this.min()) + " is higher than " + String.valueOf(this.max())) : DataResult.success(this);
      }

      public boolean areSwapped() {
         return this.min.isPresent() && this.max.isPresent() && ((Comparable)((Number)this.min.get())).compareTo((Number)this.max.get()) > 0;
      }

      public Range asRange() {
         if (this.min.isPresent()) {
            return this.max.isPresent() ? Range.closed((Number)this.min.get(), (Number)this.max.get()) : Range.atLeast((Number)this.min.get());
         } else {
            return this.max.isPresent() ? Range.atMost((Number)this.max.get()) : Range.all();
         }
      }

      public Optional asPoint() {
         Optional min = this.min();
         Optional max = this.max();
         return min.equals(max) ? min : Optional.empty();
      }

      public static MinMaxBounds.Bounds any() {
         return new MinMaxBounds.Bounds(Optional.empty(), Optional.empty());
      }

      public static MinMaxBounds.Bounds exactly(final Number value) {
         Optional wrapped = Optional.of(value);
         return new MinMaxBounds.Bounds(wrapped, wrapped);
      }

      public static MinMaxBounds.Bounds between(final Number min, final Number max) {
         return new MinMaxBounds.Bounds(Optional.of(min), Optional.of(max));
      }

      public static MinMaxBounds.Bounds atLeast(final Number value) {
         return new MinMaxBounds.Bounds(Optional.of(value), Optional.empty());
      }

      public static MinMaxBounds.Bounds atMost(final Number value) {
         return new MinMaxBounds.Bounds(Optional.empty(), Optional.of(value));
      }

      public MinMaxBounds.Bounds map(final Function mapper) {
         return new MinMaxBounds.Bounds(this.min.map(mapper), this.max.map(mapper));
      }

      public static Codec createCodec(final Codec numberCodec) {
         Codec rangeCodec = RecordCodecBuilder.create((i) -> i.group(numberCodec.optionalFieldOf("min").forGetter(MinMaxBounds.Bounds::min), numberCodec.optionalFieldOf("max").forGetter(MinMaxBounds.Bounds::max)).apply(i, MinMaxBounds.Bounds::new));
         return Codec.either(rangeCodec, numberCodec).xmap((either) -> (MinMaxBounds.Bounds)either.map((v) -> v, (x$0) -> exactly(x$0)), (bounds) -> {
            Optional point = bounds.asPoint();
            return point.isPresent() ? Either.right((Number)point.get()) : Either.left(bounds);
         });
      }

      public static StreamCodec createStreamCodec(final StreamCodec numberCodec) {
         return new StreamCodec() {
            private static final int MIN_FLAG = 1;
            private static final int MAX_FLAG = 2;

            public MinMaxBounds.Bounds decode(final ByteBuf input) {
               byte flags = input.readByte();
               Optional min = (flags & 1) != 0 ? Optional.of((Number)numberCodec.decode(input)) : Optional.empty();
               Optional max = (flags & 2) != 0 ? Optional.of((Number)numberCodec.decode(input)) : Optional.empty();
               return new MinMaxBounds.Bounds(min, max);
            }

            public void encode(final ByteBuf output, final MinMaxBounds.Bounds value) {
               Optional min = value.min();
               Optional max = value.max();
               output.writeByte((min.isPresent() ? 1 : 0) | (max.isPresent() ? 2 : 0));
               min.ifPresent((v) -> numberCodec.encode(output, v));
               max.ifPresent((v) -> numberCodec.encode(output, v));
            }
         };
      }

      public static MinMaxBounds.Bounds fromReader(final StringReader reader, final Function converter, final Supplier parseExc) throws CommandSyntaxException {
         if (!reader.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
         } else {
            int start = reader.getCursor();

            try {
               Optional min = readNumber(reader, converter, parseExc);
               Optional max;
               if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
                  reader.skip();
                  reader.skip();
                  max = readNumber(reader, converter, parseExc);
               } else {
                  max = min;
               }

               if (min.isEmpty() && max.isEmpty()) {
                  throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
               } else {
                  return new MinMaxBounds.Bounds(min, max);
               }
            } catch (CommandSyntaxException var6) {
               reader.setCursor(start);
               throw new CommandSyntaxException(var6.getType(), var6.getRawMessage(), var6.getInput(), start);
            }
         }
      }

      private static Optional readNumber(final StringReader reader, final Function converter, final Supplier parseExc) throws CommandSyntaxException {
         int start = reader.getCursor();

         while(reader.canRead() && isAllowedInputChar(reader)) {
            reader.skip();
         }

         String number = reader.getString().substring(start, reader.getCursor());
         if (number.isEmpty()) {
            return Optional.empty();
         } else {
            try {
               return Optional.of((Number)converter.apply(number));
            } catch (NumberFormatException var6) {
               throw ((DynamicCommandExceptionType)parseExc.get()).createWithContext(reader, number);
            }
         }
      }

      private static boolean isAllowedInputChar(final StringReader reader) {
         char c = reader.peek();
         if ((c < '0' || c > '9') && c != '-') {
            if (c != '.') {
               return false;
            } else {
               return !reader.canRead(2) || reader.peek(1) != '.';
            }
         } else {
            return true;
         }
      }
   }

   public static record Doubles(MinMaxBounds.Bounds bounds, MinMaxBounds.Bounds boundsSqr) implements MinMaxBounds {
      public static final MinMaxBounds.Doubles ANY = new MinMaxBounds.Doubles(MinMaxBounds.Bounds.any());
      public static final Codec CODEC = MinMaxBounds.Bounds.createCodec(Codec.DOUBLE).validate(MinMaxBounds.Bounds::validateSwappedBoundsInCodec).xmap(MinMaxBounds.Doubles::new, MinMaxBounds.Doubles::bounds);
      public static final StreamCodec STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.DOUBLE).map(MinMaxBounds.Doubles::new, MinMaxBounds.Doubles::bounds);

      private Doubles(final MinMaxBounds.Bounds bounds) {
         this(bounds, bounds.map(Mth::square));
      }

      public static MinMaxBounds.Doubles exactly(final double value) {
         return new MinMaxBounds.Doubles(MinMaxBounds.Bounds.exactly(value));
      }

      public static MinMaxBounds.Doubles between(final double min, final double max) {
         return new MinMaxBounds.Doubles(MinMaxBounds.Bounds.between(min, max));
      }

      public static MinMaxBounds.Doubles atLeast(final double value) {
         return new MinMaxBounds.Doubles(MinMaxBounds.Bounds.atLeast(value));
      }

      public static MinMaxBounds.Doubles atMost(final double value) {
         return new MinMaxBounds.Doubles(MinMaxBounds.Bounds.atMost(value));
      }

      public boolean matches(final double value) {
         if (this.bounds.min.isPresent() && this.bounds.min.get() > value) {
            return false;
         } else {
            return this.bounds.max.isEmpty() || !(this.bounds.max.get() < value);
         }
      }

      public boolean matchesSqr(final double valueSqr) {
         if (this.boundsSqr.min.isPresent() && this.boundsSqr.min.get() > valueSqr) {
            return false;
         } else {
            return this.boundsSqr.max.isEmpty() || !(this.boundsSqr.max.get() < valueSqr);
         }
      }

      public static MinMaxBounds.Doubles fromReader(final StringReader reader) throws CommandSyntaxException {
         int start = reader.getCursor();
         MinMaxBounds.Bounds bounds = MinMaxBounds.Bounds.fromReader(reader, Double::parseDouble, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidDouble);
         if (bounds.areSwapped()) {
            reader.setCursor(start);
            throw ERROR_SWAPPED.createWithContext(reader);
         } else {
            return new MinMaxBounds.Doubles(bounds);
         }
      }
   }

   public static record FloatDegrees(MinMaxBounds.Bounds bounds) implements MinMaxBounds {
      public static final MinMaxBounds.FloatDegrees ANY = new MinMaxBounds.FloatDegrees(MinMaxBounds.Bounds.any());
      public static final Codec CODEC = MinMaxBounds.Bounds.createCodec(Codec.FLOAT).xmap(MinMaxBounds.FloatDegrees::new, MinMaxBounds.FloatDegrees::bounds);
      public static final StreamCodec STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.FLOAT).map(MinMaxBounds.FloatDegrees::new, MinMaxBounds.FloatDegrees::bounds);

      public static MinMaxBounds.FloatDegrees fromReader(final StringReader reader) throws CommandSyntaxException {
         MinMaxBounds.Bounds bounds = MinMaxBounds.Bounds.fromReader(reader, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat);
         return new MinMaxBounds.FloatDegrees(bounds);
      }
   }

   public static record Ints(MinMaxBounds.Bounds bounds, MinMaxBounds.Bounds boundsSqr) implements MinMaxBounds {
      public static final MinMaxBounds.Ints ANY = new MinMaxBounds.Ints(MinMaxBounds.Bounds.any());
      public static final Codec CODEC = MinMaxBounds.Bounds.createCodec(Codec.INT).validate(MinMaxBounds.Bounds::validateSwappedBoundsInCodec).xmap(MinMaxBounds.Ints::new, MinMaxBounds.Ints::bounds);
      public static final StreamCodec STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.INT).map(MinMaxBounds.Ints::new, MinMaxBounds.Ints::bounds);

      private Ints(final MinMaxBounds.Bounds bounds) {
         this(bounds, bounds.map((i) -> Mth.square(i.longValue())));
      }

      public static MinMaxBounds.Ints exactly(final int value) {
         return new MinMaxBounds.Ints(MinMaxBounds.Bounds.exactly(value));
      }

      public static MinMaxBounds.Ints between(final int min, final int max) {
         return new MinMaxBounds.Ints(MinMaxBounds.Bounds.between(min, max));
      }

      public static MinMaxBounds.Ints atLeast(final int value) {
         return new MinMaxBounds.Ints(MinMaxBounds.Bounds.atLeast(value));
      }

      public static MinMaxBounds.Ints atMost(final int value) {
         return new MinMaxBounds.Ints(MinMaxBounds.Bounds.atMost(value));
      }

      public boolean matches(final int value) {
         if (this.bounds.min.isPresent() && this.bounds.min.get() > value) {
            return false;
         } else {
            return this.bounds.max.isEmpty() || this.bounds.max.get() >= value;
         }
      }

      public boolean matchesSqr(final long valueSqr) {
         if (this.boundsSqr.min.isPresent() && this.boundsSqr.min.get() > valueSqr) {
            return false;
         } else {
            return this.boundsSqr.max.isEmpty() || this.boundsSqr.max.get() >= valueSqr;
         }
      }

      public static MinMaxBounds.Ints fromReader(final StringReader reader) throws CommandSyntaxException {
         int start = reader.getCursor();
         MinMaxBounds.Bounds bounds = MinMaxBounds.Bounds.fromReader(reader, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt);
         if (bounds.areSwapped()) {
            reader.setCursor(start);
            throw ERROR_SWAPPED.createWithContext(reader);
         } else {
            return new MinMaxBounds.Ints(bounds);
         }
      }
   }
}
