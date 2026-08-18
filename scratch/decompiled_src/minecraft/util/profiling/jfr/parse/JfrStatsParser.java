package net.minecraft.util.profiling.jfr.parse;

import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.ChunkIdentification;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.FpsStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.IoSummary;
import net.minecraft.util.profiling.jfr.stats.PacketIdentification;
import net.minecraft.util.profiling.jfr.stats.StructureGenStat;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import org.jspecify.annotations.Nullable;

public class JfrStatsParser {
   private Instant recordingStarted = Instant.EPOCH;
   private Instant recordingEnded = Instant.EPOCH;
   private final List chunkGenStats = new ArrayList();
   private final List structureGenStats = new ArrayList();
   private final List cpuLoadStat = new ArrayList();
   private final Map receivedPackets = new HashMap();
   private final Map sentPackets = new HashMap();
   private final Map readChunks = new HashMap();
   private final Map writtenChunks = new HashMap();
   private final List fileWrites = new ArrayList();
   private final List fileReads = new ArrayList();
   private int garbageCollections;
   private Duration gcTotalDuration = Duration.ZERO;
   private final List gcHeapStats = new ArrayList();
   private final List threadAllocationStats = new ArrayList();
   private final List fps = new ArrayList();
   private final List serverTickTimes = new ArrayList();
   private @Nullable Duration worldCreationDuration = null;

   private JfrStatsParser(final Stream events) {
      this.capture(events);
   }

   public static JfrStatsResult parse(final Path path) {
      try {
         final RecordingFile recordingFile = new RecordingFile(path);

         JfrStatsResult var4;
         try {
            Iterator iterator = new Iterator() {
               public boolean hasNext() {
                  return recordingFile.hasMoreEvents();
               }

               public RecordedEvent next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     try {
                        return recordingFile.readEvent();
                     } catch (IOException var2) {
                        throw new UncheckedIOException(var2);
                     }
                  }
               }
            };
            Stream events = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 1297), false);
            var4 = (new JfrStatsParser(events)).results();
         } catch (Throwable var6) {
            try {
               recordingFile.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         recordingFile.close();
         return var4;
      } catch (IOException var7) {
         throw new UncheckedIOException(var7);
      }
   }

   private JfrStatsResult results() {
      Duration recordingDuration = Duration.between(this.recordingStarted, this.recordingEnded);
      return new JfrStatsResult(this.recordingStarted, this.recordingEnded, recordingDuration, this.worldCreationDuration, this.fps, this.serverTickTimes, this.cpuLoadStat, GcHeapStat.summary(recordingDuration, this.gcHeapStats, this.gcTotalDuration, this.garbageCollections), ThreadAllocationStat.summary(this.threadAllocationStats), collectIoStats(recordingDuration, this.receivedPackets), collectIoStats(recordingDuration, this.sentPackets), collectIoStats(recordingDuration, this.writtenChunks), collectIoStats(recordingDuration, this.readChunks), FileIOStat.summary(recordingDuration, this.fileWrites), FileIOStat.summary(recordingDuration, this.fileReads), this.chunkGenStats, this.structureGenStats);
   }

   private void capture(final Stream events) {
      events.forEach((event) -> {
         if (event.getEndTime().isAfter(this.recordingEnded) || this.recordingEnded.equals(Instant.EPOCH)) {
            this.recordingEnded = event.getEndTime();
         }

         if (event.getStartTime().isBefore(this.recordingStarted) || this.recordingStarted.equals(Instant.EPOCH)) {
            this.recordingStarted = event.getStartTime();
         }

         switch (event.getEventType().getName()) {
            case "minecraft.ChunkGeneration":
               this.chunkGenStats.add(ChunkGenStat.from(event));
               break;
            case "minecraft.StructureGeneration":
               this.structureGenStats.add(StructureGenStat.from(event));
               break;
            case "minecraft.LoadWorld":
               this.worldCreationDuration = event.getDuration();
               break;
            case "minecraft.ClientFps":
               this.fps.add(FpsStat.from(event, "fps"));
               break;
            case "minecraft.ServerTickTime":
               this.serverTickTimes.add(TickTimeStat.from(event));
               break;
            case "minecraft.PacketReceived":
               this.incrementPacket(event, event.getInt("bytes"), this.receivedPackets);
               break;
            case "minecraft.PacketSent":
               this.incrementPacket(event, event.getInt("bytes"), this.sentPackets);
               break;
            case "minecraft.ChunkRegionRead":
               this.incrementChunk(event, event.getInt("bytes"), this.readChunks);
               break;
            case "minecraft.ChunkRegionWrite":
               this.incrementChunk(event, event.getInt("bytes"), this.writtenChunks);
               break;
            case "jdk.ThreadAllocationStatistics":
               this.threadAllocationStats.add(ThreadAllocationStat.from(event));
               break;
            case "jdk.GCHeapSummary":
               this.gcHeapStats.add(GcHeapStat.from(event));
               break;
            case "jdk.CPULoad":
               this.cpuLoadStat.add(CpuLoadStat.from(event));
               break;
            case "jdk.FileWrite":
               this.appendFileIO(event, this.fileWrites, "bytesWritten");
               break;
            case "jdk.FileRead":
               this.appendFileIO(event, this.fileReads, "bytesRead");
               break;
            case "jdk.GarbageCollection":
               ++this.garbageCollections;
               this.gcTotalDuration = this.gcTotalDuration.plus(event.getDuration());
         }

      });
   }

   private void incrementPacket(final RecordedEvent event, final int packetSize, final Map packets) {
      ((JfrStatsParser.MutableCountAndSize)packets.computeIfAbsent(PacketIdentification.from(event), (ignored) -> new JfrStatsParser.MutableCountAndSize())).increment(packetSize);
   }

   private void incrementChunk(final RecordedEvent event, final int chunkSize, final Map packets) {
      ((JfrStatsParser.MutableCountAndSize)packets.computeIfAbsent(ChunkIdentification.from(event), (ignored) -> new JfrStatsParser.MutableCountAndSize())).increment(chunkSize);
   }

   private void appendFileIO(final RecordedEvent event, final List stats, final String sizeField) {
      stats.add(new FileIOStat(event.getDuration(), event.getString("path"), event.getLong(sizeField)));
   }

   private static IoSummary collectIoStats(final Duration recordingDuration, final Map packetStats) {
      List summaryStats = packetStats.entrySet().stream().map((e) -> Pair.of(e.getKey(), ((JfrStatsParser.MutableCountAndSize)e.getValue()).toCountAndSize())).toList();
      return new IoSummary(recordingDuration, summaryStats);
   }

   public static final class MutableCountAndSize {
      private long count;
      private long totalSize;

      public void increment(final int bytes) {
         this.totalSize += (long)bytes;
         ++this.count;
      }

      public IoSummary.CountAndSize toCountAndSize() {
         return new IoSummary.CountAndSize(this.count, this.totalSize);
      }
   }
}
