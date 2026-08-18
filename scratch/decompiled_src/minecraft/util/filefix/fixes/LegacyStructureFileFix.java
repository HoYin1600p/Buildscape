package net.minecraft.util.filefix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.filefix.CanceledFileFixException;
import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.access.ChunkNbt;
import net.minecraft.util.filefix.access.CompressedNbt;
import net.minecraft.util.filefix.access.FileAccess;
import net.minecraft.util.filefix.access.FileAccessProvider;
import net.minecraft.util.filefix.access.FileRelation;
import net.minecraft.util.filefix.access.FileResourceTypes;
import net.minecraft.util.filefix.access.LevelDat;
import net.minecraft.util.filefix.access.SavedDataNbt;
import net.minecraft.util.worldupdate.UpgradeProgress;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

public class LegacyStructureFileFix extends FileFix {
   public static final int STRUCTURE_RANGE = 8;
   public static final List OVERWORLD_LEGACY_STRUCTURES = List.of("Monument", "Stronghold", "Mineshaft", "Temple", "Mansion");
   public static final Map LEGACY_TO_CURRENT_MAP = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put("Iglu", "Igloo");
      map.put("TeDP", "Desert_Pyramid");
      map.put("TeJP", "Jungle_Pyramid");
      map.put("TeSH", "Swamp_Hut");
   });
   public static final List NETHER_LEGACY_STRUCTURES = List.of("Fortress");
   public static final List END_LEGACY_STRUCTURES = List.of("EndCity");
   private static final ResourceKey OVERWORLD_KEY = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("overworld"));
   private static final ResourceKey NETHER_KEY = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("the_nether"));
   private static final ResourceKey END_KEY = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("the_end"));

   public LegacyStructureFileFix(final Schema schema) {
      super(schema);
   }

   public void makeFixer() {
      this.addFileContentFix((files) -> {
         List overworldStructureData = OVERWORLD_LEGACY_STRUCTURES.stream().map((structureId) -> getLegacyStructureData(files, structureId)).toList();
         RegionStorageInfo overworldInfo = new RegionStorageInfo("overworld", OVERWORLD_KEY, "chunk");
         List netherStructureData = NETHER_LEGACY_STRUCTURES.stream().map((structureId) -> getLegacyStructureData(files, structureId)).toList();
         RegionStorageInfo netherInfo = new RegionStorageInfo("the_nether", NETHER_KEY, "chunk");
         List endStructureData = END_LEGACY_STRUCTURES.stream().map((structureId) -> getLegacyStructureData(files, structureId)).toList();
         RegionStorageInfo endInfo = new RegionStorageInfo("the_end", END_KEY, "chunk");
         FileAccess levelDat = files.getFileAccess(FileResourceTypes.LEVEL_DAT, FileRelation.ORIGIN.forFile("level.dat"));
         FileAccess overworldChunks = files.getFileAccess(FileResourceTypes.chunk(DataFixTypes.CHUNK, overworldInfo), FileRelation.OLD_OVERWORLD.resolve(FileRelation.REGION));
         FileAccess netherChunks = files.getFileAccess(FileResourceTypes.chunk(DataFixTypes.CHUNK, netherInfo), FileRelation.OLD_NETHER.resolve(FileRelation.REGION));
         FileAccess endChunks = files.getFileAccess(FileResourceTypes.chunk(DataFixTypes.CHUNK, endInfo), FileRelation.OLD_END.resolve(FileRelation.REGION));
         return (upgradeProgress) -> {
            Optional levelData = ((LevelDat)levelDat.getOnlyFile()).read();
            if (!levelData.isEmpty()) {
               upgradeProgress.setType(UpgradeProgress.Type.LEGACY_STRUCTURES);
               extractAndStoreLegacyStructureData((Dynamic)levelData.get(), List.of(new LegacyStructureFileFix.DimensionFixEntry(OVERWORLD_KEY, overworldStructureData, overworldChunks, new Long2ObjectOpenHashMap()), new LegacyStructureFileFix.DimensionFixEntry(NETHER_KEY, netherStructureData, netherChunks, new Long2ObjectOpenHashMap()), new LegacyStructureFileFix.DimensionFixEntry(END_KEY, endStructureData, endChunks, new Long2ObjectOpenHashMap())), upgradeProgress);
            }
         };
      });
   }

   private static void extractAndStoreLegacyStructureData(final Dynamic levelData, final List dimensionFixEntries, final UpgradeProgress upgradeProgress) throws IOException {
      upgradeProgress.setStatus(UpgradeProgress.Status.COUNTING);

      for(LegacyStructureFileFix.DimensionFixEntry dimensionFixEntry : dimensionFixEntries) {
         Long2ObjectOpenHashMap structures = dimensionFixEntry.structures;

         for(FileAccess structureDataFileAccess : dimensionFixEntry.structureFileAccess) {
            SavedDataNbt targetFile = (SavedDataNbt)structureDataFileAccess.getOnlyFile();
            Optional structureData = targetFile.read();
            if (!structureData.isEmpty()) {
               extractLegacyStructureData((Dynamic)structureData.get(), structures);
            }
         }

         upgradeProgress.addTotalFileFixOperations(structures.size());
      }

      upgradeProgress.setStatus(UpgradeProgress.Status.UPGRADING);

      for(LegacyStructureFileFix.DimensionFixEntry dimensionFixEntry : dimensionFixEntries) {
         ResourceKey dimensionKey = dimensionFixEntry.dimensionKey;
         ChunkNbt chunkNbt = (ChunkNbt)dimensionFixEntry.chunkFileAccess.getOnlyFile();
         String chunkGeneratorType;
         if (dimensionKey == OVERWORLD_KEY) {
            String var10000;
            switch (levelData.get("generatorName").asString("buffet")) {
               case "flat":
                  var10000 = "minecraft:flat";
                  break;
               case "debug_all_block_states":
                  var10000 = "minecraft:debug";
                  break;
               default:
                  var10000 = "minecraft:noise";
            }

            chunkGeneratorType = var10000;
         } else {
            chunkGeneratorType = "minecraft:noise";
         }

         Optional generatorIdentifier = Optional.ofNullable(Identifier.tryParse(chunkGeneratorType));
         CompoundTag dataFixContext = ChunkMap.getChunkDataFixContextTag(dimensionKey, generatorIdentifier);
         storeLegacyStructureDataToChunks(dimensionFixEntry.structures, chunkNbt, dataFixContext, upgradeProgress);
      }

   }

   private static FileAccess getLegacyStructureData(final FileAccessProvider files, final String structureId) {
      return files.getFileAccess(FileResourceTypes.savedData(References.SAVED_DATA_STRUCTURE_FEATURE_INDICES, CompressedNbt.MissingSeverity.MINOR), FileRelation.DATA.forFile(structureId + ".dat"));
   }

   private static void extractLegacyStructureData(final Dynamic structureData, final Long2ObjectMap extractedDataContainer) {
      OptionalDynamic features = structureData.get("Features");
      Map map = features.asMap(Function.identity(), Function.identity());

      for(Dynamic value : map.values()) {
         long pos = ChunkPos.pack(value.get("ChunkX").asInt(0), value.get("ChunkZ").asInt(0));
         List childList = value.get("Children").asList(Function.identity());
         if (!childList.isEmpty()) {
            Optional id = ((Dynamic)childList.getFirst()).get("id").asString().result().map(LEGACY_TO_CURRENT_MAP::get);
            if (id.isPresent()) {
               value = value.set("id", value.createString((String)id.get()));
            }
         }

         Dynamic finalValue = value;
         value.get("id").asString().ifSuccess((id) -> {
            ((LegacyStructureFileFix.LegacyStructureData)extractedDataContainer.computeIfAbsent(pos, (l) -> new LegacyStructureFileFix.LegacyStructureData())).addStart(id, finalValue);

            for(int neighborX = ChunkPos.getX(pos) - 8; neighborX <= ChunkPos.getX(pos) + 8; ++neighborX) {
               for(int neighborZ = ChunkPos.getZ(pos) - 8; neighborZ <= ChunkPos.getZ(pos) + 8; ++neighborZ) {
                  ((LegacyStructureFileFix.LegacyStructureData)extractedDataContainer.computeIfAbsent(ChunkPos.pack(neighborX, neighborZ), (l) -> new LegacyStructureFileFix.LegacyStructureData())).addIndex(id, pos);
               }
            }

         });
      }

   }

   private static void storeLegacyStructureDataToChunks(final Long2ObjectMap structures, final ChunkNbt chunksAccess, final CompoundTag dataFixContext, final UpgradeProgress upgradeProgress) {
      List entries = structures.long2ObjectEntrySet().stream().sorted(Comparator.comparingLong((entryx) -> ChunkPos.pack(ChunkPos.getRegionX(entryx.getLongKey()), ChunkPos.getRegionZ(entryx.getLongKey())))).toList();
      LegacyStructureFileFix.IncrementalFutureSequence futures = new LegacyStructureFileFix.IncrementalFutureSequence(8);

      for(Long2ObjectMap.Entry entry : entries) {
         if (upgradeProgress.isCanceled()) {
            throw new CanceledFileFixException();
         }

         long pos = entry.getLongKey();
         LegacyStructureFileFix.LegacyStructureData legacyData = (LegacyStructureFileFix.LegacyStructureData)entry.getValue();
         int finished = futures.push(chunksAccess.updateChunk(ChunkPos.unpack(pos), dataFixContext, (tag) -> {
            CompoundTag levelTag = tag.getCompoundOrEmpty("Level");
            CompoundTag structureTag = levelTag.getCompoundOrEmpty("Structures");
            CompoundTag startTag = structureTag.getCompoundOrEmpty("Starts");
            CompoundTag referencesTag = structureTag.getCompoundOrEmpty("References");
            legacyData.starts().forEach((id, value) -> startTag.put(id, (Tag)value.convert(NbtOps.INSTANCE).getValue()));
            legacyData.indexes().forEach((id, indexes) -> referencesTag.putLongArray(id, indexes.toLongArray()));
            structureTag.put("Starts", startTag);
            structureTag.put("References", referencesTag);
            levelTag.put("Structures", structureTag);
            tag.put("Level", levelTag);
            return tag;
         }));
         upgradeProgress.incrementFinishedOperationsBy(finished);
      }

      upgradeProgress.incrementFinishedOperationsBy(futures.waitForAll());
   }

   private static record DimensionFixEntry(ResourceKey dimensionKey, List structureFileAccess, FileAccess chunkFileAccess, Long2ObjectOpenHashMap structures) {
   }

   private static class IncrementalFutureSequence {
      private final int maxConcurrency;
      private final List futures;

      public IncrementalFutureSequence(final int maxConcurrency) {
         this.maxConcurrency = maxConcurrency;
         this.futures = new ArrayList(maxConcurrency);
      }

      public int push(final CompletableFuture future) {
         int finished = 0;
         if (this.futures.size() >= this.maxConcurrency) {
            finished += this.waitOnAny();
         }

         this.futures.add(future);
         return finished;
      }

      private int waitOnAny() {
         int oldSize = this.futures.size();
         CompletableFuture.anyOf((CompletableFuture[])this.futures.toArray((x$0) -> new CompletableFuture[x$0])).join();
         this.futures.removeIf(CompletableFuture::isDone);
         return oldSize - this.futures.size();
      }

      public int waitForAll() {
         int oldSize = this.futures.size();
         CompletableFuture.allOf((CompletableFuture[])this.futures.toArray((x$0) -> new CompletableFuture[x$0])).join();
         this.futures.clear();
         return oldSize;
      }
   }

   public static record LegacyStructureData(Map starts, Map indexes) {
      public LegacyStructureData() {
         this(new HashMap(), new HashMap());
      }

      public void addStart(final String id, final Dynamic data) {
         this.starts.put(id, data);
      }

      public void addIndex(final String id, final long sourcePos) {
         ((LongList)this.indexes.computeIfAbsent(id, (l) -> new LongArrayList())).add(sourcePos);
      }
   }
}
