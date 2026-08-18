package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DelegatingOps implements DynamicOps {
   protected final DynamicOps delegate;

   protected DelegatingOps(final DynamicOps delegate) {
      this.delegate = delegate;
   }

   public Object empty() {
      return this.delegate.empty();
   }

   public Object emptyMap() {
      return this.delegate.emptyMap();
   }

   public Object emptyList() {
      return this.delegate.emptyList();
   }

   public Object convertTo(final DynamicOps outOps, final Object input) {
      return Objects.equals(outOps, this.delegate) ? input : this.delegate.convertTo(outOps, input);
   }

   public DataResult getNumberValue(final Object input) {
      return this.delegate.getNumberValue(input);
   }

   public Object createNumeric(final Number i) {
      return this.delegate.createNumeric(i);
   }

   public Object createByte(final byte value) {
      return this.delegate.createByte(value);
   }

   public Object createShort(final short value) {
      return this.delegate.createShort(value);
   }

   public Object createInt(final int value) {
      return this.delegate.createInt(value);
   }

   public Object createLong(final long value) {
      return this.delegate.createLong(value);
   }

   public Object createFloat(final float value) {
      return this.delegate.createFloat(value);
   }

   public Object createDouble(final double value) {
      return this.delegate.createDouble(value);
   }

   public DataResult getBooleanValue(final Object input) {
      return this.delegate.getBooleanValue(input);
   }

   public Object createBoolean(final boolean value) {
      return this.delegate.createBoolean(value);
   }

   public DataResult getStringValue(final Object input) {
      return this.delegate.getStringValue(input);
   }

   public Object createString(final String value) {
      return this.delegate.createString(value);
   }

   public DataResult mergeToList(final Object list, final Object value) {
      return this.delegate.mergeToList(list, value);
   }

   public DataResult mergeToList(final Object list, final List values) {
      return this.delegate.mergeToList(list, values);
   }

   public DataResult mergeToMap(final Object map, final Object key, final Object value) {
      return this.delegate.mergeToMap(map, key, value);
   }

   public DataResult mergeToMap(final Object map, final MapLike values) {
      return this.delegate.mergeToMap(map, values);
   }

   public DataResult mergeToMap(final Object map, final Map values) {
      return this.delegate.mergeToMap(map, values);
   }

   public DataResult mergeToPrimitive(final Object prefix, final Object value) {
      return this.delegate.mergeToPrimitive(prefix, value);
   }

   public DataResult getMapValues(final Object input) {
      return this.delegate.getMapValues(input);
   }

   public DataResult getMapEntries(final Object input) {
      return this.delegate.getMapEntries(input);
   }

   public Object createMap(final Map map) {
      return this.delegate.createMap(map);
   }

   public Object createMap(final Stream map) {
      return this.delegate.createMap(map);
   }

   public DataResult getMap(final Object input) {
      return this.delegate.getMap(input);
   }

   public DataResult getStream(final Object input) {
      return this.delegate.getStream(input);
   }

   public DataResult getList(final Object input) {
      return this.delegate.getList(input);
   }

   public Object createList(final Stream input) {
      return this.delegate.createList(input);
   }

   public DataResult getByteBuffer(final Object input) {
      return this.delegate.getByteBuffer(input);
   }

   public Object createByteList(final ByteBuffer input) {
      return this.delegate.createByteList(input);
   }

   public DataResult getIntStream(final Object input) {
      return this.delegate.getIntStream(input);
   }

   public Object createIntList(final IntStream input) {
      return this.delegate.createIntList(input);
   }

   public DataResult getLongStream(final Object input) {
      return this.delegate.getLongStream(input);
   }

   public Object createLongList(final LongStream input) {
      return this.delegate.createLongList(input);
   }

   public Object remove(final Object input, final String key) {
      return this.delegate.remove(input, key);
   }

   public boolean compressMaps() {
      return this.delegate.compressMaps();
   }

   public ListBuilder listBuilder() {
      return new DelegatingOps.DelegateListBuilder(this.delegate.listBuilder());
   }

   public RecordBuilder mapBuilder() {
      return new DelegatingOps.DelegateRecordBuilder(this.delegate.mapBuilder());
   }

   protected class DelegateListBuilder implements ListBuilder {
      private final ListBuilder original;

      protected DelegateListBuilder(final ListBuilder original) {
         Objects.requireNonNull(DelegatingOps.this);
         super();
         this.original = original;
      }

      public DynamicOps ops() {
         return DelegatingOps.this;
      }

      public DataResult build(final Object prefix) {
         return this.original.build(prefix);
      }

      public ListBuilder add(final Object value) {
         this.original.add(value);
         return this;
      }

      public ListBuilder add(final DataResult value) {
         this.original.add(value);
         return this;
      }

      public ListBuilder add(final Object value, final Encoder encoder) {
         this.original.add(encoder.encodeStart(this.ops(), value));
         return this;
      }

      public ListBuilder addAll(final Iterable values, final Encoder encoder) {
         values.forEach((v) -> this.original.add(encoder.encode(v, this.ops(), this.ops().empty())));
         return this;
      }

      public ListBuilder withErrorsFrom(final DataResult result) {
         this.original.withErrorsFrom(result);
         return this;
      }

      public ListBuilder mapError(final UnaryOperator onError) {
         this.original.mapError(onError);
         return this;
      }

      public DataResult build(final DataResult prefix) {
         return this.original.build(prefix);
      }
   }

   protected class DelegateRecordBuilder implements RecordBuilder {
      private final RecordBuilder original;

      protected DelegateRecordBuilder(final RecordBuilder original) {
         Objects.requireNonNull(DelegatingOps.this);
         super();
         this.original = original;
      }

      public DynamicOps ops() {
         return DelegatingOps.this;
      }

      public RecordBuilder add(final Object key, final Object value) {
         this.original.add(key, value);
         return this;
      }

      public RecordBuilder add(final Object key, final DataResult value) {
         this.original.add(key, value);
         return this;
      }

      public RecordBuilder add(final DataResult key, final DataResult value) {
         this.original.add(key, value);
         return this;
      }

      public RecordBuilder add(final String key, final Object value) {
         this.original.add(key, value);
         return this;
      }

      public RecordBuilder add(final String key, final DataResult value) {
         this.original.add(key, value);
         return this;
      }

      public RecordBuilder add(final String key, final Object value, final Encoder encoder) {
         return this.original.add(key, encoder.encodeStart(this.ops(), value));
      }

      public RecordBuilder withErrorsFrom(final DataResult result) {
         this.original.withErrorsFrom(result);
         return this;
      }

      public RecordBuilder setLifecycle(final Lifecycle lifecycle) {
         this.original.setLifecycle(lifecycle);
         return this;
      }

      public RecordBuilder mapError(final UnaryOperator onError) {
         this.original.mapError(onError);
         return this;
      }

      public DataResult build(final Object prefix) {
         return this.original.build(prefix);
      }

      public DataResult build(final DataResult prefix) {
         return this.original.build(prefix);
      }
   }
}
