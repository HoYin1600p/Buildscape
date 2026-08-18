package net.minecraft.util.debug;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundDebugBlockValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugChunkValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEntityValuePacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public abstract class TrackingDebugSynchronizer {
   protected final DebugSubscription subscription;
   private final Set subscribedPlayers = new ObjectOpenHashSet();

   public TrackingDebugSynchronizer(final DebugSubscription subscription) {
      this.subscription = subscription;
   }

   public final void tick(final ServerLevel level) {
      for(ServerPlayer player : level.players()) {
         boolean wasSubscribed = this.subscribedPlayers.contains(player.getUUID());
         boolean isSubscribed = player.debugSubscriptions().contains(this.subscription);
         if (isSubscribed != wasSubscribed) {
            if (isSubscribed) {
               this.addSubscriber(player);
            } else {
               this.subscribedPlayers.remove(player.getUUID());
            }
         }
      }

      this.subscribedPlayers.removeIf((id) -> level.getPlayerByUUID(id) == null);
      if (!this.subscribedPlayers.isEmpty()) {
         this.pollAndSendUpdates(level);
      }

   }

   private void addSubscriber(final ServerPlayer player) {
      this.subscribedPlayers.add(player.getUUID());
      player.getChunkTrackingView().forEach((chunkPos) -> {
         if (!player.connection.chunkSender.isPending(chunkPos.pack())) {
            this.startTrackingChunk(player, chunkPos);
         }

      });
      player.level().getChunkSource().chunkMap.forEachEntityTrackedBy(player, (entity) -> this.startTrackingEntity(player, entity));
   }

   protected final void sendToPlayersTrackingChunk(final ServerLevel level, final ChunkPos trackedChunk, final Packet packet) {
      ChunkMap chunkMap = level.getChunkSource().chunkMap;

      for(UUID playerId : this.subscribedPlayers) {
         Player var8 = level.getPlayerByUUID(playerId);
         if (var8 instanceof ServerPlayer player) {
            if (chunkMap.isChunkTracked(player, trackedChunk.x(), trackedChunk.z())) {
               player.connection.send(packet);
            }
         }
      }

   }

   protected final void sendToPlayersTrackingEntity(final ServerLevel level, final Entity trackedEntity, final Packet packet) {
      ChunkMap chunkMap = level.getChunkSource().chunkMap;
      chunkMap.sendToTrackingPlayersFiltered(trackedEntity, packet, (player) -> this.subscribedPlayers.contains(player.getUUID()));
   }

   public final void startTrackingChunk(final ServerPlayer player, final ChunkPos chunkPos) {
      if (this.subscribedPlayers.contains(player.getUUID())) {
         this.sendInitialChunk(player, chunkPos);
      }

   }

   public final void startTrackingEntity(final ServerPlayer player, final Entity entity) {
      if (this.subscribedPlayers.contains(player.getUUID())) {
         this.sendInitialEntity(player, entity);
      }

   }

   protected void clear() {
   }

   protected void pollAndSendUpdates(final ServerLevel level) {
   }

   protected void sendInitialChunk(final ServerPlayer player, final ChunkPos chunkPos) {
   }

   protected void sendInitialEntity(final ServerPlayer player, final Entity entity) {
   }

   public static class PoiSynchronizer extends TrackingDebugSynchronizer {
      public PoiSynchronizer() {
         super(DebugSubscriptions.POIS);
      }

      protected void sendInitialChunk(final ServerPlayer player, final ChunkPos chunkPos) {
         ServerLevel level = player.level();
         PoiManager poiManager = level.getPoiManager();
         poiManager.getInChunk((t) -> true, chunkPos, PoiManager.Occupancy.ANY).forEach((record) -> player.connection.send(new ClientboundDebugBlockValuePacket(record.getPos(), this.subscription.packUpdate(new DebugPoiInfo(record)))));
      }

      public void onPoiAdded(final ServerLevel level, final PoiRecord record) {
         this.sendToPlayersTrackingChunk(level, ChunkPos.containing(record.getPos()), new ClientboundDebugBlockValuePacket(record.getPos(), this.subscription.packUpdate(new DebugPoiInfo(record))));
      }

      public void onPoiRemoved(final ServerLevel level, final BlockPos poiPos) {
         this.sendToPlayersTrackingChunk(level, ChunkPos.containing(poiPos), new ClientboundDebugBlockValuePacket(poiPos, this.subscription.emptyUpdate()));
      }

      public void onPoiTicketCountChanged(final ServerLevel level, final BlockPos poiPos) {
         this.sendToPlayersTrackingChunk(level, ChunkPos.containing(poiPos), new ClientboundDebugBlockValuePacket(poiPos, this.subscription.packUpdate(level.getPoiManager().getDebugPoiInfo(poiPos))));
      }
   }

   public static class SourceSynchronizer extends TrackingDebugSynchronizer {
      private final Map chunkSources = new HashMap();
      private final Map blockEntitySources = new HashMap();
      private final Map entitySources = new HashMap();

      public SourceSynchronizer(final DebugSubscription subscription) {
         super(subscription);
      }

      protected void clear() {
         this.chunkSources.clear();
         this.blockEntitySources.clear();
         this.entitySources.clear();
      }

      protected void pollAndSendUpdates(final ServerLevel level) {
         for(Map.Entry entry : this.chunkSources.entrySet()) {
            DebugSubscription.Update update = ((TrackingDebugSynchronizer.ValueSource)entry.getValue()).pollUpdate(this.subscription);
            if (update != null) {
               ChunkPos chunkPos = (ChunkPos)entry.getKey();
               this.sendToPlayersTrackingChunk(level, chunkPos, new ClientboundDebugChunkValuePacket(chunkPos, update));
            }
         }

         for(Map.Entry entry : this.blockEntitySources.entrySet()) {
            DebugSubscription.Update update = ((TrackingDebugSynchronizer.ValueSource)entry.getValue()).pollUpdate(this.subscription);
            if (update != null) {
               BlockPos blockPos = (BlockPos)entry.getKey();
               ChunkPos chunkPos = ChunkPos.containing(blockPos);
               this.sendToPlayersTrackingChunk(level, chunkPos, new ClientboundDebugBlockValuePacket(blockPos, update));
            }
         }

         for(Map.Entry entry : this.entitySources.entrySet()) {
            DebugSubscription.Update update = ((TrackingDebugSynchronizer.ValueSource)entry.getValue()).pollUpdate(this.subscription);
            if (update != null) {
               Entity entity = (Entity)Objects.requireNonNull(level.getEntity((UUID)entry.getKey()));
               this.sendToPlayersTrackingEntity(level, entity, new ClientboundDebugEntityValuePacket(entity.getId(), update));
            }
         }

      }

      public void registerChunk(final ChunkPos chunkPos, final DebugValueSource.ValueGetter getter) {
         this.chunkSources.put(chunkPos, new TrackingDebugSynchronizer.ValueSource(getter));
      }

      public void registerBlockEntity(final BlockPos blockPos, final DebugValueSource.ValueGetter getter) {
         this.blockEntitySources.put(blockPos, new TrackingDebugSynchronizer.ValueSource(getter));
      }

      public void registerEntity(final UUID entityId, final DebugValueSource.ValueGetter getter) {
         this.entitySources.put(entityId, new TrackingDebugSynchronizer.ValueSource(getter));
      }

      public void dropChunk(final ChunkPos chunkPos) {
         this.chunkSources.remove(chunkPos);
         this.blockEntitySources.keySet().removeIf(chunkPos::contains);
      }

      public void dropBlockEntity(final ServerLevel level, final BlockPos blockPos) {
         TrackingDebugSynchronizer.ValueSource source = (TrackingDebugSynchronizer.ValueSource)this.blockEntitySources.remove(blockPos);
         if (source != null) {
            ChunkPos chunkPos = ChunkPos.containing(blockPos);
            this.sendToPlayersTrackingChunk(level, chunkPos, new ClientboundDebugBlockValuePacket(blockPos, this.subscription.emptyUpdate()));
         }

      }

      public void dropEntity(final Entity entity) {
         this.entitySources.remove(entity.getUUID());
      }

      protected void sendInitialChunk(final ServerPlayer player, final ChunkPos chunkPos) {
         TrackingDebugSynchronizer.ValueSource chunkSource = (TrackingDebugSynchronizer.ValueSource)this.chunkSources.get(chunkPos);
         if (chunkSource != null && chunkSource.lastSyncedValue != null) {
            player.connection.send(new ClientboundDebugChunkValuePacket(chunkPos, this.subscription.packUpdate(chunkSource.lastSyncedValue)));
         }

         for(Map.Entry entry : this.blockEntitySources.entrySet()) {
            Object lastValue = (T)((TrackingDebugSynchronizer.ValueSource)entry.getValue()).lastSyncedValue;
            if (lastValue != null) {
               BlockPos blockPos = (BlockPos)entry.getKey();
               if (chunkPos.contains(blockPos)) {
                  player.connection.send(new ClientboundDebugBlockValuePacket(blockPos, this.subscription.packUpdate(lastValue)));
               }
            }
         }

      }

      protected void sendInitialEntity(final ServerPlayer player, final Entity entity) {
         TrackingDebugSynchronizer.ValueSource source = (TrackingDebugSynchronizer.ValueSource)this.entitySources.get(entity.getUUID());
         if (source != null && source.lastSyncedValue != null) {
            player.connection.send(new ClientboundDebugEntityValuePacket(entity.getId(), this.subscription.packUpdate(source.lastSyncedValue)));
         }

      }
   }

   private static class ValueSource {
      private final DebugValueSource.ValueGetter getter;
      private @Nullable Object lastSyncedValue;

      private ValueSource(final DebugValueSource.ValueGetter getter) {
         this.getter = getter;
      }

      public DebugSubscription.@Nullable Update pollUpdate(final DebugSubscription subscription) {
         Object newValue = (T)this.getter.get();
         if (!Objects.equals(newValue, this.lastSyncedValue)) {
            this.lastSyncedValue = newValue;
            return subscription.packUpdate(newValue);
         } else {
            return null;
         }
      }
   }

   public static class VillageSectionSynchronizer extends TrackingDebugSynchronizer {
      public VillageSectionSynchronizer() {
         super(DebugSubscriptions.VILLAGE_SECTIONS);
      }

      protected void sendInitialChunk(final ServerPlayer player, final ChunkPos chunkPos) {
         ServerLevel level = player.level();
         PoiManager poiManager = level.getPoiManager();
         poiManager.getInChunk((t) -> true, chunkPos, PoiManager.Occupancy.ANY).forEach((record) -> {
            SectionPos centerSection = SectionPos.of(record.getPos());
            forEachVillageSectionUpdate(level, centerSection, (sectionPos, isVillage) -> {
               BlockPos sectionBlockPos = sectionPos.center();
               player.connection.send(new ClientboundDebugBlockValuePacket(sectionBlockPos, this.subscription.packUpdate(isVillage ? Unit.INSTANCE : null)));
            });
         });
      }

      public void onPoiAdded(final ServerLevel level, final PoiRecord record) {
         this.sendVillageSectionsPacket(level, record.getPos());
      }

      public void onPoiRemoved(final ServerLevel level, final BlockPos poiPos) {
         this.sendVillageSectionsPacket(level, poiPos);
      }

      private void sendVillageSectionsPacket(final ServerLevel level, final BlockPos poiPos) {
         forEachVillageSectionUpdate(level, SectionPos.of(poiPos), (sectionPos, isVillage) -> {
            BlockPos sectionBlockPos = sectionPos.center();
            if (isVillage) {
               this.sendToPlayersTrackingChunk(level, ChunkPos.containing(sectionBlockPos), new ClientboundDebugBlockValuePacket(sectionBlockPos, this.subscription.packUpdate(Unit.INSTANCE)));
            } else {
               this.sendToPlayersTrackingChunk(level, ChunkPos.containing(sectionBlockPos), new ClientboundDebugBlockValuePacket(sectionBlockPos, this.subscription.emptyUpdate()));
            }

         });
      }

      private static void forEachVillageSectionUpdate(final ServerLevel level, final SectionPos centerSection, final BiConsumer consumer) {
         for(int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
            for(int offsetX = -1; offsetX <= 1; ++offsetX) {
               for(int offsetY = -1; offsetY <= 1; ++offsetY) {
                  SectionPos sectionPos = centerSection.offset(offsetX, offsetY, offsetZ);
                  if (level.isVillage(sectionPos.center())) {
                     consumer.accept(sectionPos, true);
                  } else {
                     consumer.accept(sectionPos, false);
                  }
               }
            }
         }

      }
   }
}
