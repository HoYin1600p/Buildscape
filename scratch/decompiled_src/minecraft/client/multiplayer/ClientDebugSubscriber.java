package net.minecraft.client.multiplayer;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundDebugSubscriptionRequestPacket;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ClientDebugSubscriber {
   private final ClientPacketListener connection;
   private final DebugScreenOverlay debugScreenOverlay;
   private Set remoteSubscriptions = Set.of();
   private final Map valuesBySubscription = new HashMap();

   public ClientDebugSubscriber(final ClientPacketListener connection, final DebugScreenOverlay debugScreenOverlay) {
      this.debugScreenOverlay = debugScreenOverlay;
      this.connection = connection;
   }

   private static void addFlag(final Set output, final DebugSubscription subscription, final boolean flag) {
      if (flag) {
         output.add(subscription);
      }

   }

   private Set requestedSubscriptions() {
      Set subscriptions = new ReferenceOpenHashSet();
      addFlag(subscriptions, RemoteDebugSampleType.TICK_TIME.subscription(), this.debugScreenOverlay.showFpsCharts());
      if (SharedConstants.DEBUG_ENABLED) {
         addFlag(subscriptions, DebugSubscriptions.BEES, SharedConstants.DEBUG_BEES);
         addFlag(subscriptions, DebugSubscriptions.BEE_HIVES, SharedConstants.DEBUG_BEES);
         addFlag(subscriptions, DebugSubscriptions.BRAINS, SharedConstants.DEBUG_BRAIN);
         addFlag(subscriptions, DebugSubscriptions.BREEZES, SharedConstants.DEBUG_BREEZE_MOB);
         addFlag(subscriptions, DebugSubscriptions.ENTITY_BLOCK_INTERSECTIONS, SharedConstants.DEBUG_ENTITY_BLOCK_INTERSECTION);
         addFlag(subscriptions, DebugSubscriptions.ENTITY_PATHS, SharedConstants.DEBUG_PATHFINDING);
         addFlag(subscriptions, DebugSubscriptions.GAME_EVENTS, SharedConstants.DEBUG_GAME_EVENT_LISTENERS);
         addFlag(subscriptions, DebugSubscriptions.GAME_EVENT_LISTENERS, SharedConstants.DEBUG_GAME_EVENT_LISTENERS);
         addFlag(subscriptions, DebugSubscriptions.GOAL_SELECTORS, SharedConstants.DEBUG_GOAL_SELECTOR || SharedConstants.DEBUG_BEES);
         addFlag(subscriptions, DebugSubscriptions.NEIGHBOR_UPDATES, SharedConstants.DEBUG_NEIGHBORSUPDATE);
         addFlag(subscriptions, DebugSubscriptions.POIS, SharedConstants.DEBUG_POI);
         addFlag(subscriptions, DebugSubscriptions.RAIDS, SharedConstants.DEBUG_RAIDS);
         addFlag(subscriptions, DebugSubscriptions.REDSTONE_WIRE_ORIENTATIONS, SharedConstants.DEBUG_EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER);
         addFlag(subscriptions, DebugSubscriptions.STRUCTURES, SharedConstants.DEBUG_STRUCTURES);
         addFlag(subscriptions, DebugSubscriptions.VILLAGE_SECTIONS, SharedConstants.DEBUG_VILLAGE_SECTIONS);
      }

      return subscriptions;
   }

   public void clear() {
      this.remoteSubscriptions = Set.of();
      this.dropLevel();
   }

   public void tick(final long gameTime) {
      Set newSubscriptions = this.requestedSubscriptions();
      if (!newSubscriptions.equals(this.remoteSubscriptions)) {
         this.remoteSubscriptions = newSubscriptions;
         this.onSubscriptionsChanged(newSubscriptions);
      }

      this.valuesBySubscription.forEach((subscription, valueMaps) -> {
         if (subscription.expireAfterTicks() != 0) {
            valueMaps.purgeExpired(gameTime);
         }

      });
   }

   private void onSubscriptionsChanged(final Set newSubscriptions) {
      this.valuesBySubscription.keySet().retainAll(newSubscriptions);
      this.initializeSubscriptions(newSubscriptions);
      this.connection.send(new ServerboundDebugSubscriptionRequestPacket(newSubscriptions));
   }

   private void initializeSubscriptions(final Set newSubscriptions) {
      for(DebugSubscription subscription : newSubscriptions) {
         this.valuesBySubscription.computeIfAbsent(subscription, (s) -> new ClientDebugSubscriber.ValueMaps());
      }

   }

   private ClientDebugSubscriber.@Nullable ValueMaps getValueMaps(final DebugSubscription subscription) {
      return (ClientDebugSubscriber.ValueMaps)this.valuesBySubscription.get(subscription);
   }

   private ClientDebugSubscriber.@Nullable ValueMap getValueMap(final DebugSubscription subscription, final ClientDebugSubscriber.ValueMapType mapType) {
      ClientDebugSubscriber.ValueMaps maps = this.getValueMaps(subscription);
      return maps != null ? mapType.get(maps) : null;
   }

   private @Nullable Object getValue(final DebugSubscription subscription, final Object key, final ClientDebugSubscriber.ValueMapType type) {
      ClientDebugSubscriber.ValueMap values = this.getValueMap(subscription, type);
      return values != null ? values.getValue(key) : null;
   }

   public DebugValueAccess createDebugValueAccess(final Level level) {
      return new DebugValueAccess() {
         {
            Objects.requireNonNull(ClientDebugSubscriber.this);
         }

         public void forEachChunk(final DebugSubscription subscription, final BiConsumer consumer) {
            ClientDebugSubscriber.this.forEachValue(subscription, ClientDebugSubscriber.chunks(), consumer);
         }

         public @Nullable Object getChunkValue(final DebugSubscription subscription, final ChunkPos chunkPos) {
            return ClientDebugSubscriber.this.getValue(subscription, chunkPos, ClientDebugSubscriber.chunks());
         }

         public void forEachBlock(final DebugSubscription subscription, final BiConsumer consumer) {
            ClientDebugSubscriber.this.forEachValue(subscription, ClientDebugSubscriber.blocks(), consumer);
         }

         public @Nullable Object getBlockValue(final DebugSubscription subscription, final BlockPos blockPos) {
            return ClientDebugSubscriber.this.getValue(subscription, blockPos, ClientDebugSubscriber.blocks());
         }

         public void forEachEntity(final DebugSubscription subscription, final BiConsumer consumer) {
            ClientDebugSubscriber.this.forEachValue(subscription, ClientDebugSubscriber.entities(), (entityId, value) -> {
               Entity entity = level.getEntity(entityId);
               if (entity != null) {
                  consumer.accept(entity, value);
               }

            });
         }

         public @Nullable Object getEntityValue(final DebugSubscription subscription, final Entity entity) {
            return ClientDebugSubscriber.this.getValue(subscription, entity.getUUID(), ClientDebugSubscriber.entities());
         }

         public void forEachEvent(final DebugSubscription subscription, final DebugValueAccess.EventVisitor visitor) {
            ClientDebugSubscriber.ValueMaps values = ClientDebugSubscriber.this.getValueMaps(subscription);
            if (values != null) {
               long gameTime = level.getGameTime();

               for(ClientDebugSubscriber.ValueWrapper event : values.events) {
                  int remainingTicks = (int)(event.expiresAfterTime() - gameTime);
                  int totalLifetime = subscription.expireAfterTicks();
                  visitor.accept(event.value(), remainingTicks, totalLifetime);
               }

            }
         }
      };
   }

   public void updateChunk(final long gameTime, final ChunkPos chunkPos, final DebugSubscription.Update update) {
      this.updateMap(gameTime, chunkPos, update, chunks());
   }

   public void updateBlock(final long gameTime, final BlockPos blockPos, final DebugSubscription.Update update) {
      this.updateMap(gameTime, blockPos, update, blocks());
   }

   public void updateEntity(final long gameTime, final Entity entity, final DebugSubscription.Update update) {
      this.updateMap(gameTime, entity.getUUID(), update, entities());
   }

   public void pushEvent(final long gameTime, final DebugSubscription.Event event) {
      ClientDebugSubscriber.ValueMaps values = this.getValueMaps(event.subscription());
      if (values != null) {
         values.events.add(new ClientDebugSubscriber.ValueWrapper(event.value(), gameTime + (long)event.subscription().expireAfterTicks()));
      }

   }

   private void updateMap(final long gameTime, final Object key, final DebugSubscription.Update update, final ClientDebugSubscriber.ValueMapType type) {
      ClientDebugSubscriber.ValueMap values = this.getValueMap(update.subscription(), type);
      if (values != null) {
         values.apply(gameTime, key, update);
      }

   }

   private void forEachValue(final DebugSubscription subscription, final ClientDebugSubscriber.ValueMapType type, final BiConsumer consumer) {
      ClientDebugSubscriber.ValueMap values = this.getValueMap(subscription, type);
      if (values != null) {
         values.forEach(consumer);
      }

   }

   public void dropLevel() {
      this.valuesBySubscription.clear();
      this.initializeSubscriptions(this.remoteSubscriptions);
   }

   public void dropChunk(final ChunkPos chunkPos) {
      if (!this.valuesBySubscription.isEmpty()) {
         for(ClientDebugSubscriber.ValueMaps values : this.valuesBySubscription.values()) {
            values.dropChunkAndBlocks(chunkPos);
         }

      }
   }

   public void dropEntity(final Entity entity) {
      if (!this.valuesBySubscription.isEmpty()) {
         for(ClientDebugSubscriber.ValueMaps values : this.valuesBySubscription.values()) {
            values.entityValues.removeKey(entity.getUUID());
         }

      }
   }

   private static ClientDebugSubscriber.ValueMapType entities() {
      return (v) -> v.entityValues;
   }

   private static ClientDebugSubscriber.ValueMapType blocks() {
      return (v) -> v.blockValues;
   }

   private static ClientDebugSubscriber.ValueMapType chunks() {
      return (v) -> v.chunkValues;
   }

   private static class ValueMap {
      private final Map values = new HashMap();

      public void removeValues(final Predicate predicate) {
         this.values.values().removeIf(predicate);
      }

      public void removeKey(final Object key) {
         this.values.remove(key);
      }

      public void removeKeys(final Predicate predicate) {
         this.values.keySet().removeIf(predicate);
      }

      public @Nullable Object getValue(final Object key) {
         ClientDebugSubscriber.ValueWrapper result = (ClientDebugSubscriber.ValueWrapper)this.values.get(key);
         return result != null ? result.value() : null;
      }

      public void apply(final long gameTime, final Object key, final DebugSubscription.Update update) {
         if (update.value().isPresent()) {
            this.values.put(key, new ClientDebugSubscriber.ValueWrapper(update.value().get(), gameTime + (long)update.subscription().expireAfterTicks()));
         } else {
            this.values.remove(key);
         }

      }

      public void forEach(final BiConsumer output) {
         this.values.forEach((k, v) -> output.accept(k, v.value()));
      }
   }

   @FunctionalInterface
   private interface ValueMapType {
      ClientDebugSubscriber.ValueMap get(ClientDebugSubscriber.ValueMaps maps);
   }

   private static class ValueMaps {
      private final ClientDebugSubscriber.ValueMap chunkValues = new ClientDebugSubscriber.ValueMap();
      private final ClientDebugSubscriber.ValueMap blockValues = new ClientDebugSubscriber.ValueMap();
      private final ClientDebugSubscriber.ValueMap entityValues = new ClientDebugSubscriber.ValueMap();
      private final List events = new ArrayList();

      public void purgeExpired(final long gameTime) {
         Predicate expiredPredicate = (v) -> v.hasExpired(gameTime);
         this.chunkValues.removeValues(expiredPredicate);
         this.blockValues.removeValues(expiredPredicate);
         this.entityValues.removeValues(expiredPredicate);
         this.events.removeIf(expiredPredicate);
      }

      public void dropChunkAndBlocks(final ChunkPos chunkPos) {
         this.chunkValues.removeKey(chunkPos);
         this.blockValues.removeKeys(chunkPos::contains);
      }
   }

   private static record ValueWrapper(Object value, long expiresAfterTime) {
      private static final long NO_EXPIRY = -1L;

      public boolean hasExpired(final long gameTime) {
         if (this.expiresAfterTime == -1L) {
            return false;
         } else {
            return gameTime >= this.expiresAfterTime;
         }
      }
   }
}
