package net.minecraft.world.level.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.BitStorage;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.util.ZeroBitStorage;
import org.jspecify.annotations.Nullable;

public class PalettedContainer implements PaletteResize, PalettedContainerRO {
   private static final int MIN_PALETTE_BITS = 0;
   private volatile PalettedContainer.Data data;
   private final Strategy strategy;
   private final ThreadingDetector threadingDetector = new ThreadingDetector("PalettedContainer");

   public void acquire() {
      this.threadingDetector.checkAndLock();
   }

   public void release() {
      this.threadingDetector.checkAndUnlock();
   }

   public static Codec codecRW(final Codec elementCodec, final Strategy strategy, final Object defaultValue) {
      PalettedContainerRO.Unpacker unpacker = PalettedContainer::unpack;
      return codec(elementCodec, strategy, defaultValue, unpacker);
   }

   public static Codec codecRO(final Codec elementCodec, final Strategy strategy, final Object defaultValue) {
      PalettedContainerRO.Unpacker unpacker = (s, data) -> unpack(s, data).map((e) -> e);
      return codec(elementCodec, strategy, defaultValue, unpacker);
   }

   private static Codec codec(final Codec elementCodec, final Strategy strategy, final Object defaultValue, final PalettedContainerRO.Unpacker unpacker) {
      return RecordCodecBuilder.create((i) -> i.group(elementCodec.mapResult(ExtraCodecs.orElsePartial(defaultValue)).listOf().fieldOf("palette").forGetter(PalettedContainerRO.PackedData::paletteEntries), Codec.LONG_STREAM.lenientOptionalFieldOf("data").forGetter(PalettedContainerRO.PackedData::storage)).apply(i, PalettedContainerRO.PackedData::new)).comapFlatMap((discData) -> unpacker.read(strategy, discData), (palettedContainer) -> palettedContainer.pack(strategy));
   }

   private PalettedContainer(final Strategy strategy, final Configuration dataConfiguration, final BitStorage storage, final Palette palette) {
      this.strategy = strategy;
      this.data = new PalettedContainer.Data(dataConfiguration, storage, palette);
   }

   private PalettedContainer(final PalettedContainer source) {
      this.strategy = source.strategy;
      this.data = source.data.copy();
   }

   public PalettedContainer(final Object initialValue, final Strategy strategy) {
      this.strategy = strategy;
      this.data = this.createOrReuseData((PalettedContainer.Data)null, 0);
      this.data.palette.idFor(initialValue, this);
   }

   private PalettedContainer.Data createOrReuseData(final PalettedContainer.@Nullable Data oldData, final int targetBits) {
      Configuration dataConfiguration = this.strategy.getConfigurationForBitCount(targetBits);
      if (oldData != null && dataConfiguration.equals(oldData.configuration())) {
         return oldData;
      } else {
         BitStorage storage = (BitStorage)(dataConfiguration.bitsInMemory() == 0 ? new ZeroBitStorage(this.strategy.entryCount()) : new SimpleBitStorage(dataConfiguration.bitsInMemory(), this.strategy.entryCount()));
         Palette palette = dataConfiguration.createPalette(this.strategy, List.of());
         return new PalettedContainer.Data(dataConfiguration, storage, palette);
      }
   }

   public int onResize(final int bits, final Object lastAddedValue) {
      PalettedContainer.Data oldData = this.data;
      PalettedContainer.Data newData = this.createOrReuseData(oldData, bits);
      newData.copyFrom(oldData.palette, oldData.storage);
      this.data = newData;
      return newData.palette.idFor(lastAddedValue, PaletteResize.noResizeExpected());
   }

   public Object getAndSet(final int x, final int y, final int z, final Object value) {
      this.acquire();

      Object var5;
      try {
         var5 = this.getAndSet(this.strategy.getIndex(x, y, z), value);
      } finally {
         this.release();
      }

      return var5;
   }

   public Object getAndSetUnchecked(final int x, final int y, final int z, final Object value) {
      return this.getAndSet(this.strategy.getIndex(x, y, z), value);
   }

   private Object getAndSet(final int index, final Object value) {
      int id = this.data.palette.idFor(value, this);
      int oldId = this.data.storage.getAndSet(index, id);
      return this.data.palette.valueFor(oldId);
   }

   public void set(final int x, final int y, final int z, final Object value) {
      this.acquire();

      try {
         this.set(this.strategy.getIndex(x, y, z), value);
      } finally {
         this.release();
      }

   }

   private void set(final int index, final Object value) {
      int id = this.data.palette.idFor(value, this);
      this.data.storage.set(index, id);
   }

   public Object get(final int x, final int y, final int z) {
      return this.get(this.strategy.getIndex(x, y, z));
   }

   protected Object get(final int index) {
      PalettedContainer.Data data = this.data;
      return data.palette.valueFor(data.storage.get(index));
   }

   public void getAll(final Consumer consumer) {
      Palette palette = this.data.palette();
      IntSet allExistingEntries = new IntArraySet();
      this.data.storage.getAll(allExistingEntries::add);
      allExistingEntries.forEach((state) -> consumer.accept(palette.valueFor(state)));
   }

   public void read(final FriendlyByteBuf buffer) {
      this.acquire();

      try {
         int newBits = buffer.readByte();
         PalettedContainer.Data newData = this.createOrReuseData(this.data, newBits);
         newData.palette.read(buffer, this.strategy.globalMap());
         buffer.readFixedSizeLongArray(newData.storage.getRaw());
         this.data = newData;
      } finally {
         this.release();
      }

   }

   public void write(final FriendlyByteBuf buffer) {
      this.acquire();

      try {
         this.data.write(buffer, this.strategy.globalMap());
      } finally {
         this.release();
      }

   }

   @VisibleForTesting
   public static DataResult unpack(final Strategy strategy, final PalettedContainerRO.PackedData discData) {
      List paletteEntries = discData.paletteEntries();
      int entryCount = strategy.entryCount();
      Configuration storedConfiguration = strategy.getConfigurationForPaletteSize(paletteEntries.size());
      int bitsOnDisc = storedConfiguration.bitsInStorage();
      if (discData.bitsPerEntry() != -1 && bitsOnDisc != discData.bitsPerEntry()) {
         return DataResult.error(() -> "Invalid bit count, calculated " + bitsOnDisc + ", but container declared " + discData.bitsPerEntry());
      } else {
         BitStorage storage;
         Palette palette;
         if (storedConfiguration.bitsInMemory() == 0) {
            palette = storedConfiguration.createPalette(strategy, paletteEntries);
            storage = new ZeroBitStorage(entryCount);
         } else {
            Optional dataOpt = discData.storage();
            if (dataOpt.isEmpty()) {
               return DataResult.error(() -> "Missing values for non-zero storage");
            }

            long[] data = ((LongStream)dataOpt.get()).toArray();

            try {
               if (!storedConfiguration.alwaysRepack() && storedConfiguration.bitsInMemory() == bitsOnDisc) {
                  palette = storedConfiguration.createPalette(strategy, paletteEntries);
                  storage = new SimpleBitStorage(storedConfiguration.bitsInMemory(), entryCount, data);
               } else {
                  Palette oldPalette = new HashMapPalette(bitsOnDisc, paletteEntries);
                  SimpleBitStorage oldStorage = new SimpleBitStorage(bitsOnDisc, entryCount, data);
                  Palette newPalette = storedConfiguration.createPalette(strategy, paletteEntries);
                  int[] newContents = reencodeContents(oldStorage, oldPalette, newPalette);
                  palette = newPalette;
                  storage = new SimpleBitStorage(storedConfiguration.bitsInMemory(), entryCount, newContents);
               }
            } catch (SimpleBitStorage.InitializationException var14) {
               return DataResult.error(() -> "Failed to read PalettedContainer: " + var14.getMessage());
            }
         }

         return DataResult.success(new PalettedContainer(strategy, storedConfiguration, storage, palette));
      }
   }

   public PalettedContainerRO.PackedData pack(final Strategy strategy) {
      this.acquire();

      PalettedContainerRO.PackedData var14;
      try {
         BitStorage currentStorage = this.data.storage;
         Palette currentPalette = this.data.palette;
         HashMapPalette newPalette = new HashMapPalette(currentStorage.getBits());
         int entryCount = strategy.entryCount();
         int[] newContents = reencodeContents(currentStorage, currentPalette, newPalette);
         Configuration storedConfiguration = strategy.getConfigurationForPaletteSize(newPalette.getSize());
         int bitsOnDisc = storedConfiguration.bitsInStorage();
         Optional values;
         if (bitsOnDisc != 0) {
            SimpleBitStorage storage = new SimpleBitStorage(bitsOnDisc, entryCount, newContents);
            values = Optional.of(Arrays.stream(storage.getRaw()));
         } else {
            values = Optional.empty();
         }

         var14 = new PalettedContainerRO.PackedData(newPalette.getEntries(), values, bitsOnDisc);
      } finally {
         this.release();
      }

      return var14;
   }

   private static int[] reencodeContents(final BitStorage storage, final Palette oldPalette, final Palette newPalette) {
      int[] buffer = new int[storage.getSize()];
      storage.unpack(buffer);
      PaletteResize dummyResizer = PaletteResize.noResizeExpected();
      int lastReadId = -1;
      int lastWrittenId = -1;

      for(int index = 0; index < buffer.length; ++index) {
         int id = buffer[index];
         if (id != lastReadId) {
            lastReadId = id;
            lastWrittenId = newPalette.idFor(oldPalette.valueFor(id), dummyResizer);
         }

         buffer[index] = lastWrittenId;
      }

      return buffer;
   }

   public int getSerializedSize() {
      return this.data.getSerializedSize(this.strategy.globalMap());
   }

   public int bitsPerEntry() {
      return this.data.storage().getBits();
   }

   public boolean maybeHas(final Predicate predicate) {
      return this.data.palette.maybeHas(predicate);
   }

   public void forEachInPalette(final Consumer consumer) {
      for(int i = 0; i < this.data.palette.getSize(); ++i) {
         consumer.accept(this.data.palette.valueFor(i));
      }

   }

   public PalettedContainer copy() {
      return new PalettedContainer(this);
   }

   public PalettedContainer recreate() {
      return new PalettedContainer(this.data.palette.valueFor(0), this.strategy);
   }

   public void count(final PalettedContainer.CountConsumer output) {
      if (this.data.palette.getSize() == 1) {
         output.accept(this.data.palette.valueFor(0), this.data.storage.getSize());
      } else {
         Int2IntOpenHashMap counts = new Int2IntOpenHashMap();
         this.data.storage.getAll((state) -> counts.addTo(state, 1));
         counts.int2IntEntrySet().forEach((entry) -> output.accept(this.data.palette.valueFor(entry.getIntKey()), entry.getIntValue()));
      }
   }

   @FunctionalInterface
   public interface CountConsumer {
      void accept(final Object entry, final int count);
   }

   private static record Data(Configuration configuration, BitStorage storage, Palette palette) {
      public void copyFrom(final Palette oldPalette, final BitStorage oldStorage) {
         PaletteResize dummyResizer = PaletteResize.noResizeExpected();

         for(int i = 0; i < oldStorage.getSize(); ++i) {
            Object value = (T)oldPalette.valueFor(oldStorage.get(i));
            this.storage.set(i, this.palette.idFor(value, dummyResizer));
         }

      }

      public int getSerializedSize(final IdMap globalMap) {
         return 1 + this.palette.getSerializedSize(globalMap) + this.storage.getRaw().length * 8;
      }

      public void write(final FriendlyByteBuf buffer, final IdMap globalMap) {
         buffer.writeByte(this.storage.getBits());
         this.palette.write(buffer, globalMap);
         buffer.writeFixedSizeLongArray(this.storage.getRaw());
      }

      public PalettedContainer.Data copy() {
         return new PalettedContainer.Data(this.configuration, this.storage.copy(), this.palette.copy());
      }
   }
}
