package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public class NullOps implements DynamicOps {
   public static final NullOps INSTANCE = new NullOps();
   private static final MapLike EMPTY_MAP = new MapLike() {
      public @Nullable Unit get(final Unit key) {
         return null;
      }

      public @Nullable Unit get(final String key) {
         return null;
      }

      public Stream entries() {
         return Stream.empty();
      }
   };

   private NullOps() {
   }

   public Object convertTo(final DynamicOps outOps, final Unit input) {
      return outOps.empty();
   }

   public Unit empty() {
      return Unit.INSTANCE;
   }

   public Unit emptyMap() {
      return Unit.INSTANCE;
   }

   public Unit emptyList() {
      return Unit.INSTANCE;
   }

   public Unit createNumeric(final Number value) {
      return Unit.INSTANCE;
   }

   public Unit createByte(final byte value) {
      return Unit.INSTANCE;
   }

   public Unit createShort(final short value) {
      return Unit.INSTANCE;
   }

   public Unit createInt(final int value) {
      return Unit.INSTANCE;
   }

   public Unit createLong(final long value) {
      return Unit.INSTANCE;
   }

   public Unit createFloat(final float value) {
      return Unit.INSTANCE;
   }

   public Unit createDouble(final double value) {
      return Unit.INSTANCE;
   }

   public Unit createBoolean(final boolean value) {
      return Unit.INSTANCE;
   }

   public Unit createString(final String value) {
      return Unit.INSTANCE;
   }

   public DataResult getNumberValue(final Unit input) {
      return DataResult.success(0);
   }

   public DataResult getBooleanValue(final Unit input) {
      return DataResult.success(false);
   }

   public DataResult getStringValue(final Unit input) {
      return DataResult.success("");
   }

   public DataResult mergeToList(final Unit input, final Unit value) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToList(final Unit input, final List values) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToMap(final Unit input, final Unit key, final Unit value) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToMap(final Unit input, final Map values) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToMap(final Unit input, final MapLike values) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult getMapValues(final Unit input) {
      return DataResult.success(Stream.empty());
   }

   public DataResult getMapEntries(final Unit input) {
      return DataResult.success((Consumer)(consumer) -> {
      });
   }

   public DataResult getMap(final Unit input) {
      return DataResult.success(EMPTY_MAP);
   }

   public DataResult getStream(final Unit input) {
      return DataResult.success(Stream.empty());
   }

   public DataResult getList(final Unit input) {
      return DataResult.success((Consumer)(consumer) -> {
      });
   }

   public DataResult getByteBuffer(final Unit input) {
      return DataResult.success(ByteBuffer.wrap(new byte[0]));
   }

   public DataResult getIntStream(final Unit input) {
      return DataResult.success(IntStream.empty());
   }

   public DataResult getLongStream(final Unit input) {
      return DataResult.success(LongStream.empty());
   }

   public Unit createMap(final Stream map) {
      return Unit.INSTANCE;
   }

   public Unit createMap(final Map map) {
      return Unit.INSTANCE;
   }

   public Unit createList(final Stream input) {
      return Unit.INSTANCE;
   }

   public Unit createByteList(final ByteBuffer input) {
      return Unit.INSTANCE;
   }

   public Unit createIntList(final IntStream input) {
      return Unit.INSTANCE;
   }

   public Unit createLongList(final LongStream input) {
      return Unit.INSTANCE;
   }

   public Unit remove(final Unit input, final String key) {
      return input;
   }

   public RecordBuilder mapBuilder() {
      return new NullOps.NullMapBuilder(this);
   }

   public ListBuilder listBuilder() {
      return new NullOps.NullListBuilder(this);
   }

   public String toString() {
      return "Null";
   }

   private static final class NullListBuilder extends AbstractListBuilder {
      public NullListBuilder(final DynamicOps ops) {
         super(ops);
      }

      protected Unit initBuilder() {
         return Unit.INSTANCE;
      }

      protected Unit append(final Unit builder, final Unit value) {
         return builder;
      }

      protected DataResult build(final Unit builder, final Unit prefix) {
         return DataResult.success(builder);
      }
   }

   private static final class NullMapBuilder extends RecordBuilder.AbstractUniversalBuilder {
      public NullMapBuilder(final DynamicOps ops) {
         super(ops);
      }

      protected Unit initBuilder() {
         return Unit.INSTANCE;
      }

      protected Unit append(final Unit key, final Unit value, final Unit builder) {
         return builder;
      }

      protected DataResult build(final Unit builder, final Unit prefix) {
         return DataResult.success(prefix);
      }
   }
}
