package net.minecraft.nbt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class NbtOps implements DynamicOps {
   public static final NbtOps INSTANCE = new NbtOps();

   private NbtOps() {
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public Tag emptyList() {
      return new ListTag();
   }

   public Tag emptyMap() {
      return new CompoundTag();
   }

   public Object convertTo(final DynamicOps outOps, final Tag input) {
      Objects.requireNonNull(input);
      Tag var3 = input;
      byte var4 = 0;

      while(true) {
         Throwable var43;
         switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
            case 0:
               EndTag ignored = (EndTag)var3;
               return outOps.empty();
            case 1:
               ByteTag var6 = (ByteTag)var3;
               ByteTag var55 = var6;

               try {
                  var56 = var55.value();
               } catch (Throwable var33) {
                  var43 = var33;
                  boolean var62 = false;
                  break;
               }

               byte var35 = var56;
               if (false) {
                  var4 = 2;
                  continue;
               }

               return outOps.createByte(var35);
            case 2:
               ShortTag var8 = (ShortTag)var3;
               ShortTag var53 = var8;

               try {
                  var54 = var53.value();
               } catch (Throwable var32) {
                  var43 = var32;
                  boolean var61 = false;
                  break;
               }

               short var36 = var54;
               if (false) {
                  var4 = 3;
                  continue;
               }

               return outOps.createShort(var36);
            case 3:
               IntTag var10 = (IntTag)var3;
               IntTag var51 = var10;

               try {
                  var52 = var51.value();
               } catch (Throwable var31) {
                  var43 = var31;
                  boolean var60 = false;
                  break;
               }

               int var37 = var52;
               if (false) {
                  var4 = 4;
                  continue;
               }

               return outOps.createInt(var37);
            case 4:
               LongTag var12 = (LongTag)var3;
               LongTag var49 = var12;

               try {
                  var50 = var49.value();
               } catch (Throwable var30) {
                  var43 = var30;
                  boolean var59 = false;
                  break;
               }

               long var38 = var50;
               if (false) {
                  var4 = 5;
                  continue;
               }

               return outOps.createLong(var38);
            case 5:
               FloatTag var15 = (FloatTag)var3;
               FloatTag var47 = var15;

               try {
                  var48 = var47.value();
               } catch (Throwable var29) {
                  var43 = var29;
                  boolean var58 = false;
                  break;
               }

               float var39 = var48;
               if (false) {
                  var4 = 6;
                  continue;
               }

               return outOps.createFloat(var39);
            case 6:
               DoubleTag var17 = (DoubleTag)var3;
               DoubleTag var45 = var17;

               try {
                  var46 = var45.value();
               } catch (Throwable var28) {
                  var43 = var28;
                  boolean var57 = false;
                  break;
               }

               double var40 = var46;
               if (false) {
                  var4 = 7;
                  continue;
               }

               return outOps.createDouble(var40);
            case 7:
               ByteArrayTag byteArrayTag = (ByteArrayTag)var3;
               return outOps.createByteList(ByteBuffer.wrap(byteArrayTag.getAsByteArray()));
            case 8:
               StringTag var21 = (StringTag)var3;
               StringTag var42 = var21;

               try {
                  var44 = var42.value();
               } catch (Throwable var27) {
                  var43 = var27;
                  boolean var10001 = false;
                  break;
               }

               String var41 = var44;
               return outOps.createString(var41);
            case 9:
               ListTag listTag = (ListTag)var3;
               return this.convertList(outOps, listTag);
            case 10:
               CompoundTag compoundTag = (CompoundTag)var3;
               return this.convertMap(outOps, compoundTag);
            case 11:
               IntArrayTag intArrayTag = (IntArrayTag)var3;
               return outOps.createIntList(Arrays.stream(intArrayTag.getAsIntArray()));
            case 12:
               LongArrayTag longArrayTag = (LongArrayTag)var3;
               return outOps.createLongList(Arrays.stream(longArrayTag.getAsLongArray()));
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         Throwable var34 = var43;
         throw new MatchException(var34.toString(), var34);
      }
   }

   public DataResult getNumberValue(final Tag input) {
      return (DataResult)input.asNumber().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Not a number"));
   }

   public Tag createNumeric(final Number i) {
      return DoubleTag.valueOf(i.doubleValue());
   }

   public Tag createByte(final byte value) {
      return ByteTag.valueOf(value);
   }

   public Tag createShort(final short value) {
      return ShortTag.valueOf(value);
   }

   public Tag createInt(final int value) {
      return IntTag.valueOf(value);
   }

   public Tag createLong(final long value) {
      return LongTag.valueOf(value);
   }

   public Tag createFloat(final float value) {
      return FloatTag.valueOf(value);
   }

   public Tag createDouble(final double value) {
      return DoubleTag.valueOf(value);
   }

   public DataResult getBooleanValue(final Tag input) {
      return this.getNumberValue(input).map((value) -> value.doubleValue() != 0.0D);
   }

   public Tag createBoolean(final boolean value) {
      return ByteTag.valueOf(value);
   }

   public DataResult getStringValue(final Tag input) {
      if (input instanceof StringTag var2) {
         StringTag var10000 = var2;

         try {
            var6 = var10000.value();
         } catch (Throwable var5) {
            throw new MatchException(var5.toString(), var5);
         }

         String var4 = var6;
         return DataResult.success(var4);
      } else {
         return DataResult.error(() -> "Not a string");
      }
   }

   public Tag createString(final String value) {
      return StringTag.valueOf(value);
   }

   public DataResult mergeToList(final Tag list, final Tag value) {
      return (DataResult)createCollector(list).map((collector) -> DataResult.success(collector.accept(value).result())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + String.valueOf(list), list));
   }

   public DataResult mergeToList(final Tag list, final List values) {
      return (DataResult)createCollector(list).map((collector) -> DataResult.success(collector.acceptAll(values).result())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + String.valueOf(list), list));
   }

   public DataResult mergeToMap(final Tag map, final Tag key, final Tag value) {
      if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(map), map);
      } else if (key instanceof StringTag) {
         StringTag output = (StringTag)key;
         StringTag var10000 = output;

         try {
            var10 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String tag = var10;
         CompoundTag var11;
         if (map instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag)map;
            var11 = tag.shallowCopy();
         } else {
            var11 = new CompoundTag();
         }

         CompoundTag output = var11;
         output.put(tag, value);
         return DataResult.success(output);
      } else {
         return DataResult.error(() -> "key is not a string: " + String.valueOf(key), map);
      }
   }

   public DataResult mergeToMap(final Tag map, final MapLike values) {
      if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(map), map);
      } else {
         Iterator valuesIterator = values.entries().iterator();
         if (!valuesIterator.hasNext()) {
            return map == this.empty() ? DataResult.success(this.emptyMap()) : DataResult.success(map);
         } else {
            CompoundTag var10000;
            if (map instanceof CompoundTag) {
               CompoundTag tag = (CompoundTag)map;
               var10000 = tag.shallowCopy();
            } else {
               var10000 = new CompoundTag();
            }

            CompoundTag output = var10000;
            List missed = new ArrayList();
            valuesIterator.forEachRemaining((entry) -> {
               Tag key = (Tag)entry.getFirst();
               if (key instanceof StringTag $b$0) {
                  StringTag var10000 = $b$0;

                  try {
                     var8 = var10000.value();
                  } catch (Throwable var7) {
                     throw new MatchException(var7.toString(), var7);
                  }

                  String patt1$temp = var8;
                  output.put(patt1$temp, (Tag)entry.getSecond());
               } else {
                  missed.add(key);
               }
            });
            return !missed.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + String.valueOf(missed), output) : DataResult.success(output);
         }
      }
   }

   public DataResult mergeToMap(final Tag map, final Map values) {
      if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(map), map);
      } else if (values.isEmpty()) {
         return map == this.empty() ? DataResult.success(this.emptyMap()) : DataResult.success(map);
      } else {
         CompoundTag var10000;
         if (map instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag)map;
            var10000 = tag.shallowCopy();
         } else {
            var10000 = new CompoundTag();
         }

         CompoundTag output = var10000;
         List missed = new ArrayList();

         for(Map.Entry entry : values.entrySet()) {
            Tag key = (Tag)entry.getKey();
            if (key instanceof StringTag) {
               StringTag var8 = (StringTag)key;
               StringTag var13 = var8;

               try {
                  var14 = var13.value();
               } catch (Throwable var11) {
                  throw new MatchException(var11.toString(), var11);
               }

               String var10 = var14;
               output.put(var10, (Tag)entry.getValue());
            } else {
               missed.add(key);
            }
         }

         return !missed.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + String.valueOf(missed), output) : DataResult.success(output);
      }
   }

   public DataResult getMapValues(final Tag input) {
      if (input instanceof CompoundTag tag) {
         return DataResult.success(tag.entrySet().stream().map((entry) -> Pair.of(this.createString((String)entry.getKey()), (Tag)entry.getValue())));
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(input));
      }
   }

   public DataResult getMapEntries(final Tag input) {
      if (input instanceof CompoundTag tag) {
         return DataResult.success((Consumer)(c) -> {
            for(Map.Entry entry : tag.entrySet()) {
               c.accept(this.createString((String)entry.getKey()), (Tag)entry.getValue());
            }

         });
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(input));
      }
   }

   public DataResult getMap(final Tag input) {
      if (input instanceof final CompoundTag tag) {
         return DataResult.success(new MapLike() {
            {
               Objects.requireNonNull(NbtOps.this);
            }

            public @Nullable Tag get(final Tag key) {
               if (key instanceof StringTag var2) {
                  StringTag var10000 = var2;

                  try {
                     var6 = var10000.value();
                  } catch (Throwable var5) {
                     throw new MatchException(var5.toString(), var5);
                  }

                  String var4 = var6;
                  return tag.get(var4);
               } else {
                  throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + String.valueOf(key));
               }
            }

            public @Nullable Tag get(final String key) {
               return tag.get(key);
            }

            public Stream entries() {
               return tag.entrySet().stream().map((entry) -> Pair.of(NbtOps.this.createString((String)entry.getKey()), (Tag)entry.getValue()));
            }

            public String toString() {
               return "MapLike[" + String.valueOf(tag) + "]";
            }
         });
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(input));
      }
   }

   public Tag createMap(final Stream map) {
      CompoundTag tag = new CompoundTag();
      map.forEach((entry) -> {
         Tag key = (Tag)entry.getFirst();
         Tag value = (Tag)entry.getSecond();
         if (key instanceof StringTag $b$0) {
            StringTag var10000 = $b$0;

            try {
               var8 = var10000.value();
            } catch (Throwable var7) {
               throw new MatchException(var7.toString(), var7);
            }

            String patt1$temp = var8;
            tag.put(patt1$temp, value);
         } else {
            throw new UnsupportedOperationException("Cannot create map with non-string key: " + String.valueOf(key));
         }
      });
      return tag;
   }

   public DataResult getStream(final Tag input) {
      if (input instanceof CollectionTag collection) {
         return DataResult.success(collection.stream());
      } else {
         return DataResult.error(() -> "Not a list");
      }
   }

   public DataResult getList(final Tag input) {
      if (input instanceof CollectionTag collection) {
         return DataResult.success(collection::forEach);
      } else {
         return DataResult.error(() -> "Not a list: " + String.valueOf(input));
      }
   }

   public DataResult getByteBuffer(final Tag input) {
      if (input instanceof ByteArrayTag array) {
         return DataResult.success(ByteBuffer.wrap(array.getAsByteArray()));
      } else {
         return super.getByteBuffer(input);
      }
   }

   public Tag createByteList(final ByteBuffer input) {
      ByteBuffer wholeBuffer = input.duplicate().clear();
      byte[] bytes = new byte[input.capacity()];
      wholeBuffer.get(0, bytes, 0, bytes.length);
      return new ByteArrayTag(bytes);
   }

   public DataResult getIntStream(final Tag input) {
      if (input instanceof IntArrayTag array) {
         return DataResult.success(Arrays.stream(array.getAsIntArray()));
      } else {
         return super.getIntStream(input);
      }
   }

   public Tag createIntList(final IntStream input) {
      return new IntArrayTag(input.toArray());
   }

   public DataResult getLongStream(final Tag input) {
      if (input instanceof LongArrayTag array) {
         return DataResult.success(Arrays.stream(array.getAsLongArray()));
      } else {
         return super.getLongStream(input);
      }
   }

   public Tag createLongList(final LongStream input) {
      return new LongArrayTag(input.toArray());
   }

   public Tag createList(final Stream input) {
      return new ListTag((List)input.collect(Util.toMutableList()));
   }

   public Tag remove(final Tag input, final String key) {
      if (input instanceof CompoundTag tag) {
         CompoundTag result = tag.shallowCopy();
         result.remove(key);
         return result;
      } else {
         return input;
      }
   }

   public String toString() {
      return "NBT";
   }

   public RecordBuilder mapBuilder() {
      return new NbtOps.NbtRecordBuilder(this);
   }

   private static Optional createCollector(final Tag tag) {
      if (tag instanceof EndTag) {
         return Optional.of(new NbtOps.GenericListCollector());
      } else if (tag instanceof CollectionTag) {
         CollectionTag collection = (CollectionTag)tag;
         if (collection.isEmpty()) {
            return Optional.of(new NbtOps.GenericListCollector());
         } else {
            Objects.requireNonNull(collection);
            byte var3 = 0;
            Optional var10000;
            switch (collection.typeSwitch<invokedynamic>(collection, var3)) {
               case 0:
                  ListTag list = (ListTag)collection;
                  var10000 = Optional.of(new NbtOps.GenericListCollector(list));
                  break;
               case 1:
                  ByteArrayTag array = (ByteArrayTag)collection;
                  var10000 = Optional.of(new NbtOps.ByteListCollector(array.getAsByteArray()));
                  break;
               case 2:
                  IntArrayTag array = (IntArrayTag)collection;
                  var10000 = Optional.of(new NbtOps.IntListCollector(array.getAsIntArray()));
                  break;
               case 3:
                  LongArrayTag array = (LongArrayTag)collection;
                  var10000 = Optional.of(new NbtOps.LongListCollector(array.getAsLongArray()));
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         }
      } else {
         return Optional.empty();
      }
   }

   private static class ByteListCollector implements NbtOps.ListCollector {
      private final ByteArrayList values = new ByteArrayList();

      public ByteListCollector(final byte[] initialValues) {
         this.values.addElements(0, initialValues);
      }

      public NbtOps.ListCollector accept(final Tag tag) {
         if (tag instanceof ByteTag byteTag) {
            this.values.add(byteTag.byteValue());
            return this;
         } else {
            return (new NbtOps.GenericListCollector(this.values)).accept(tag);
         }
      }

      public Tag result() {
         return new ByteArrayTag(this.values.toByteArray());
      }
   }

   private static class GenericListCollector implements NbtOps.ListCollector {
      private final ListTag result = new ListTag();

      private GenericListCollector() {
      }

      private GenericListCollector(final ListTag initial) {
         this.result.addAll(initial);
      }

      public GenericListCollector(final IntArrayList initials) {
         initials.forEach((v) -> this.result.add(IntTag.valueOf(v)));
      }

      public GenericListCollector(final ByteArrayList initials) {
         initials.forEach((v) -> this.result.add(ByteTag.valueOf(v)));
      }

      public GenericListCollector(final LongArrayList initials) {
         initials.forEach((v) -> this.result.add(LongTag.valueOf(v)));
      }

      public NbtOps.ListCollector accept(final Tag tag) {
         this.result.add(tag);
         return this;
      }

      public Tag result() {
         return this.result;
      }
   }

   private static class IntListCollector implements NbtOps.ListCollector {
      private final IntArrayList values = new IntArrayList();

      public IntListCollector(final int[] initialValues) {
         this.values.addElements(0, initialValues);
      }

      public NbtOps.ListCollector accept(final Tag tag) {
         if (tag instanceof IntTag intTag) {
            this.values.add(intTag.intValue());
            return this;
         } else {
            return (new NbtOps.GenericListCollector(this.values)).accept(tag);
         }
      }

      public Tag result() {
         return new IntArrayTag(this.values.toIntArray());
      }
   }

   private interface ListCollector {
      NbtOps.ListCollector accept(Tag t);

      default NbtOps.ListCollector acceptAll(final Iterable tags) {
         NbtOps.ListCollector collector = this;

         for(Tag tag : tags) {
            collector = collector.accept(tag);
         }

         return collector;
      }

      Tag result();
   }

   private static class LongListCollector implements NbtOps.ListCollector {
      private final LongArrayList values = new LongArrayList();

      public LongListCollector(final long[] initialValues) {
         this.values.addElements(0, initialValues);
      }

      public NbtOps.ListCollector accept(final Tag tag) {
         if (tag instanceof LongTag longTag) {
            this.values.add(longTag.longValue());
            return this;
         } else {
            return (new NbtOps.GenericListCollector(this.values)).accept(tag);
         }
      }

      public Tag result() {
         return new LongArrayTag(this.values.toLongArray());
      }
   }

   private class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder {
      protected NbtRecordBuilder(final NbtOps this$0) {
         Objects.requireNonNull(this$0);
         super(this$0);
      }

      protected CompoundTag initBuilder() {
         return new CompoundTag();
      }

      protected CompoundTag append(final String key, final Tag value, final CompoundTag builder) {
         builder.put(key, value);
         return builder;
      }

      protected DataResult build(final CompoundTag builder, final Tag prefix) {
         if (prefix != null && prefix != EndTag.INSTANCE) {
            if (!(prefix instanceof CompoundTag)) {
               return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(prefix), prefix);
            } else {
               CompoundTag compound = (CompoundTag)prefix;
               CompoundTag result = compound.shallowCopy();

               for(Map.Entry entry : builder.entrySet()) {
                  result.put((String)entry.getKey(), (Tag)entry.getValue());
               }

               return DataResult.success(result);
            }
         } else {
            return DataResult.success(builder);
         }
      }
   }
}
