package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SectionStorage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String SECTIONS_TAG = "Sections";
   private final SimpleRegionStorage simpleRegionStorage;
   private final Long2ObjectMap storage = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet dirtyChunks = new LongLinkedOpenHashSet();
   private final Codec codec;
   private final Function packer;
   private final BiFunction unpacker;
   private final Function factory;
   private final RegistryAccess registryAccess;
   private final ChunkIOErrorReporter errorReporter;
   protected final LevelHeightAccessor levelHeightAccessor;
   private final LongSet loadedChunks = new LongOpenHashSet();
   private final Long2ObjectMap pendingLoads = new Long2ObjectOpenHashMap();
   private final Object loadLock = new Object();

   public SectionStorage(final SimpleRegionStorage simpleRegionStorage, final Codec codec, final Function packer, final BiFunction unpacker, final Function factory, final RegistryAccess registryAccess, final ChunkIOErrorReporter errorReporter, final LevelHeightAccessor levelHeightAccessor) {
      this.simpleRegionStorage = simpleRegionStorage;
      this.codec = codec;
      this.packer = packer;
      this.unpacker = unpacker;
      this.factory = factory;
      this.registryAccess = registryAccess;
      this.errorReporter = errorReporter;
      this.levelHeightAccessor = levelHeightAccessor;
   }

   protected void tick(final BooleanSupplier haveTime) {
      LongIterator iterator = this.dirtyChunks.iterator();

      while(iterator.hasNext() && haveTime.getAsBoolean()) {
         ChunkPos chunkPos = ChunkPos.unpack(iterator.nextLong());
         iterator.remove();
         this.writeChunk(chunkPos);
      }

      this.unpackPendingLoads();
   }

   private void unpackPendingLoads() {
      synchronized(this.loadLock) {
         Iterator iterator = Long2ObjectMaps.fastIterator(this.pendingLoads);

         while(iterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            Optional chunk = (Optional)((CompletableFuture)entry.getValue()).getNow((Object)null);
            if (chunk != null) {
               long chunkKey = entry.getLongKey();
               this.unpackChunk(ChunkPos.unpack(chunkKey), (SectionStorage.PackedChunk)chunk.orElse((Object)null));
               iterator.remove();
               this.loadedChunks.add(chunkKey);
            }
         }

      }
   }

   public void flushAll() {
      if (!this.dirtyChunks.isEmpty()) {
         this.dirtyChunks.forEach((pos) -> this.writeChunk(ChunkPos.unpack(pos)));
         this.dirtyChunks.clear();
      }

   }

   public boolean hasWork() {
      return !this.dirtyChunks.isEmpty();
   }

   protected @Nullable Optional get(final long sectionPos) {
      return (Optional)this.storage.get(sectionPos);
   }

   protected Optional getOrLoad(final long sectionPos) {
      if (this.outsideStoredRange(sectionPos)) {
         return Optional.empty();
      } else {
         Optional r = this.get(sectionPos);
         if (r != null) {
            return r;
         } else {
            this.unpackChunk(SectionPos.of(sectionPos).chunk());
            r = this.get(sectionPos);
            if (r == null) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            } else {
               return r;
            }
         }
      }
   }

   protected boolean outsideStoredRange(final long sectionPos) {
      int y = SectionPos.sectionToBlockCoord(SectionPos.y(sectionPos));
      return this.levelHeightAccessor.isOutsideBuildHeight(y);
   }

   protected Object getOrCreate(final long sectionPos) {
      if (this.outsideStoredRange(sectionPos)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
      } else {
         Optional r = this.getOrLoad(sectionPos);
         if (r.isPresent()) {
            return r.get();
         } else {
            Object newR = (R)this.factory.apply((Runnable)() -> this.setDirty(sectionPos));
            this.storage.put(sectionPos, Optional.of(newR));
            return newR;
         }
      }
   }

   public CompletableFuture prefetch(final ChunkPos chunkPos) {
      synchronized(this.loadLock) {
         long chunkKey = chunkPos.pack();
         return this.loadedChunks.contains(chunkKey) ? CompletableFuture.completedFuture((Object)null) : (CompletableFuture)this.pendingLoads.computeIfAbsent(chunkKey, (k) -> this.tryRead(chunkPos));
      }
   }

   private void unpackChunk(final ChunkPos chunkPos) {
      long chunkKey = chunkPos.pack();
      CompletableFuture future;
      synchronized(this.loadLock) {
         if (!this.loadedChunks.add(chunkKey)) {
            return;
         }

         future = (CompletableFuture)this.pendingLoads.computeIfAbsent(chunkKey, (k) -> this.tryRead(chunkPos));
      }

      this.unpackChunk(chunkPos, (SectionStorage.PackedChunk)((Optional)future.join()).orElse((Object)null));
      synchronized(this.loadLock) {
         this.pendingLoads.remove(chunkKey);
      }
   }

   private CompletableFuture tryRead(final ChunkPos chunkPos) {
      RegistryOps registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      return this.simpleRegionStorage.read(chunkPos).thenApplyAsync((result) -> result.map((tag) -> SectionStorage.PackedChunk.parse(this.codec, registryOps, tag, this.simpleRegionStorage, this.levelHeightAccessor)), Util.backgroundExecutor().forName("parseSection")).exceptionally((throwable) -> {
         if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
         }

         if (throwable instanceof IOException e) {
            LOGGER.error("Error reading chunk {} data from disk", chunkPos, e);
            this.errorReporter.reportChunkLoadFailure(e, this.simpleRegionStorage.storageInfo(), chunkPos);
            return Optional.empty();
         } else {
            throw new CompletionException(throwable);
         }
      });
   }

   private void unpackChunk(final ChunkPos pos, final SectionStorage.@Nullable PackedChunk packedChunk) {
      if (packedChunk == null) {
         for(int sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
            this.storage.put(getKey(pos, sectionY), Optional.empty());
         }
      } else {
         boolean versionChanged = packedChunk.versionChanged();

         for(int sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
            long key = getKey(pos, sectionY);
            Optional section = Optional.ofNullable(packedChunk.sectionsByY.get(sectionY)).map((packed) -> this.unpacker.apply(packed, (Runnable)() -> this.setDirty(key)));
            this.storage.put(key, section);
            section.ifPresent((s) -> {
               this.onSectionLoad(key);
               if (versionChanged) {
                  this.setDirty(key);
               }

            });
         }
      }

   }

   private void writeChunk(final ChunkPos chunkPos) {
      RegistryOps registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      Dynamic tag = this.writeChunk(chunkPos, registryOps);
      Tag value = (Tag)tag.getValue();
      if (value instanceof CompoundTag compoundTag) {
         this.simpleRegionStorage.write(chunkPos, compoundTag).exceptionally((throwable) -> {
            this.errorReporter.reportChunkSaveFailure(throwable, this.simpleRegionStorage.storageInfo(), chunkPos);
            return null;
         });
      } else {
         LOGGER.error("Expected compound tag, got {}", value);
      }

   }

   private Dynamic writeChunk(final ChunkPos chunkPos, final DynamicOps ops) {
      Map sections = Maps.newHashMap();

      for(int sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
         long key = getKey(chunkPos, sectionY);
         Optional r = (Optional)this.storage.get(key);
         if (r != null && !r.isEmpty()) {
            DataResult serializedSection = this.codec.encodeStart(ops, this.packer.apply(r.get()));
            String yName = Integer.toString(sectionY);
            serializedSection.resultOrPartial(LOGGER::error).ifPresent((s) -> sections.put(ops.createString(yName), s));
         }
      }

      return new Dynamic(ops, ops.createMap(ImmutableMap.of(ops.createString("Sections"), ops.createMap(sections), ops.createString("DataVersion"), ops.createInt(SharedConstants.getCurrentVersion().dataVersion().version()))));
   }

   private static long getKey(final ChunkPos chunkPos, final int sectionY) {
      return SectionPos.asLong(chunkPos.x(), sectionY, chunkPos.z());
   }

   protected void onSectionLoad(final long sectionPos) {
   }

   protected void setDirty(final long sectionPos) {
      Optional r = (Optional)this.storage.get(sectionPos);
      if (r != null && !r.isEmpty()) {
         this.dirtyChunks.add(ChunkPos.pack(SectionPos.x(sectionPos), SectionPos.z(sectionPos)));
      } else {
         LOGGER.warn("No data for position: {}", SectionPos.of(sectionPos));
      }
   }

   public void flush(final ChunkPos chunkPos) {
      if (this.dirtyChunks.remove(chunkPos.pack())) {
         this.writeChunk(chunkPos);
      }

   }

   public void close() throws IOException {
      this.simpleRegionStorage.close();
   }

   private static record PackedChunk(Int2ObjectMap sectionsByY, boolean versionChanged) {
      public static SectionStorage.PackedChunk parse(final Codec codec, final DynamicOps ops, final Tag tag, final SimpleRegionStorage simpleRegionStorage, final LevelHeightAccessor levelHeightAccessor) {
         Dynamic originalTag = new Dynamic(ops, tag);
         Dynamic fixedTag = simpleRegionStorage.upgradeChunkTag(originalTag, 1945);
         boolean versionChanged = originalTag != fixedTag;
         OptionalDynamic sections = fixedTag.get("Sections");
         Int2ObjectMap sectionsByY = new Int2ObjectOpenHashMap();

         for(int sectionY = levelHeightAccessor.getMinSectionY(); sectionY <= levelHeightAccessor.getMaxSectionY(); ++sectionY) {
            Optional section = sections.get(Integer.toString(sectionY)).result().flatMap((sectionData) -> codec.parse(sectionData).resultOrPartial(SectionStorage.LOGGER::error));
            if (section.isPresent()) {
               sectionsByY.put(sectionY, section.get());
            }
         }

         return new SectionStorage.PackedChunk(sectionsByY, versionChanged);
      }
   }
}
