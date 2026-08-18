package net.minecraft.world.clock;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.timeline.Timeline;
import org.apache.commons.lang3.mutable.MutableObject;

public class ServerClockManager extends SavedData implements ClockManager {
   public static final SavedDataType TYPE = new SavedDataType(Identifier.withDefaultNamespace("world_clocks"), () -> new ServerClockManager(PackedClockStates.EMPTY), PackedClockStates.CODEC.xmap(ServerClockManager::new, ServerClockManager::packState), DataFixTypes.SAVED_DATA_WORLD_CLOCKS);
   private final PackedClockStates packedClockStates;
   private MinecraftServer server;
   private final Map clocks = new HashMap();

   private ServerClockManager(final PackedClockStates packedClockStates) {
      this.packedClockStates = packedClockStates;
   }

   public void init(final MinecraftServer server) {
      this.server = server;
      server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).listElements().forEach((definition) -> this.clocks.put(definition, new ServerClockManager.ServerClockInstance()));
      server.registryAccess().lookupOrThrow(Registries.TIMELINE).listElements().forEach((timeline) -> ((Timeline)timeline.value()).registerTimeMarkers(this::registerTimeMarker));
      this.packedClockStates.clocks().forEach((definition, state) -> {
         ServerClockManager.ServerClockInstance instance = this.getInstance(definition);
         instance.loadFrom(state);
      });
   }

   private void registerTimeMarker(final ResourceKey timeMarkerId, final ClockTimeMarker timeMarker) {
      this.getInstance(timeMarker.clock()).timeMarkers.put(timeMarkerId, timeMarker);
   }

   public PackedClockStates packState() {
      return new PackedClockStates(Util.mapValues(this.clocks, ServerClockManager.ServerClockInstance::packState));
   }

   public void tick() {
      boolean advanceTime = this.server.getGlobalGameRules().get(GameRules.ADVANCE_TIME);
      if (advanceTime) {
         this.clocks.values().forEach(ServerClockManager.ServerClockInstance::tick);
         this.setDirty();
      }

   }

   public ServerClockManager.ServerClockInstance getInstance(final Holder definition) {
      ServerClockManager.ServerClockInstance instance = (ServerClockManager.ServerClockInstance)this.clocks.get(definition);
      if (instance == null) {
         throw new IllegalStateException("No clock initialized for definition: " + String.valueOf(definition));
      } else {
         return instance;
      }
   }

   public void setTotalTicks(final Holder clock, final long totalTicks) {
      this.modifyClock(clock, (instance) -> {
         instance.totalTicks = totalTicks;
         instance.partialTick = 0.0F;
      });
   }

   public ServerClockManager.MoveResult moveToTimeMarker(final Holder clock, final ResourceKey timeMarkerId) {
      MutableObject result = new MutableObject();
      this.modifyClock(clock, (instance) -> {
         ClockTimeMarker timeMarker = (ClockTimeMarker)instance.timeMarkers.get(timeMarkerId);
         if (timeMarker == null) {
            result.setValue(ServerClockManager.MoveResult.NO_TIME_MARKER_FOUND);
         } else if (timeMarker.occursAt(instance.totalTicks)) {
            result.setValue(ServerClockManager.MoveResult.NOT_MOVED);
         } else {
            instance.totalTicks = timeMarker.resolveTimeToMoveTo(instance.totalTicks);
            instance.partialTick = 0.0F;
            result.setValue(ServerClockManager.MoveResult.MOVED);
         }
      });
      return (ServerClockManager.MoveResult)result.get();
   }

   public void addTicks(final Holder clock, final int ticks) {
      this.modifyClock(clock, (instance) -> instance.totalTicks = Math.max(instance.totalTicks + (long)ticks, 0L));
   }

   public void setPaused(final Holder clock, final boolean paused) {
      this.modifyClock(clock, (instance) -> instance.paused = paused);
   }

   public void setRate(final Holder clock, final float rate) {
      this.modifyClock(clock, (instance) -> instance.rate = rate);
   }

   private void modifyClock(final Holder clock, final Consumer action) {
      ServerClockManager.ServerClockInstance instance = this.getInstance(clock);
      action.accept(instance);
      Map updates = Map.of(clock, instance.packNetworkState(this.server));
      this.server.getPlayerList().broadcastAll(new ClientboundSetTimePacket(this.getGameTime(), updates));
      this.setDirty();

      for(ServerLevel level : this.server.getAllLevels()) {
         level.environmentAttributes().invalidateTickCache();
      }

   }

   public ClientboundSetTimePacket createFullSyncPacket() {
      return new ClientboundSetTimePacket(this.getGameTime(), Util.mapValues(this.clocks, (clock) -> clock.packNetworkState(this.server)));
   }

   private long getGameTime() {
      return this.server.overworld().getGameTime();
   }

   public boolean isAtTimeMarker(final Holder clock, final ResourceKey timeMarkerId) {
      ServerClockManager.ServerClockInstance clockInstance = this.getInstance(clock);
      ClockTimeMarker timeMarker = (ClockTimeMarker)clockInstance.timeMarkers.get(timeMarkerId);
      return timeMarker != null && timeMarker.occursAt(clockInstance.totalTicks);
   }

   public Stream commandTimeMarkersForClock(final Holder clock) {
      return this.getInstance(clock).timeMarkers.entrySet().stream().filter((entry) -> ((ClockTimeMarker)entry.getValue()).showInCommands()).map(Map.Entry::getKey);
   }

   public static enum MoveResult {
      NO_TIME_MARKER_FOUND,
      NOT_MOVED,
      MOVED;

      // $FF: synthetic method
      private static ServerClockManager.MoveResult[] $values() {
         return new ServerClockManager.MoveResult[]{NO_TIME_MARKER_FOUND, NOT_MOVED, MOVED};
      }
   }

   public static class ServerClockInstance implements ClockInstance {
      private final Map timeMarkers = new Reference2ObjectOpenHashMap();
      private long totalTicks;
      private float partialTick;
      private float rate = 1.0F;
      private boolean paused;

      public void loadFrom(final ClockState state) {
         this.totalTicks = state.totalTicks();
         this.partialTick = state.partialTick();
         this.rate = state.rate();
         this.paused = state.paused();
      }

      public void tick() {
         if (!this.paused) {
            this.partialTick += this.rate;
            int fullTicks = Mth.floor(this.partialTick);
            this.partialTick -= (float)fullTicks;
            this.totalTicks += (long)fullTicks;
         }

      }

      public long totalTicks() {
         return this.totalTicks;
      }

      public float partialTick() {
         return this.partialTick;
      }

      public float rate() {
         return this.rate;
      }

      public boolean isPaused() {
         return this.paused;
      }

      public ClockState packState() {
         return new ClockState(this.totalTicks, this.partialTick, this.rate, this.paused);
      }

      public ClockNetworkState packNetworkState(final MinecraftServer server) {
         boolean advanceTime = server.getGlobalGameRules().get(GameRules.ADVANCE_TIME);
         boolean paused = this.paused || !advanceTime;
         return new ClockNetworkState(this.totalTicks, this.partialTick, paused ? 0.0F : this.rate);
      }
   }
}
