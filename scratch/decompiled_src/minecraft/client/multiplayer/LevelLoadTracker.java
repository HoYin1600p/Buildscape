package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.level.progress.LevelLoadProgressTracker;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class LevelLoadTracker implements LevelLoadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long CLIENT_WAIT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(30L);
   public static final long LEVEL_LOAD_CLOSE_DELAY_MS = 500L;
   private final LevelLoadProgressTracker serverProgressTracker = new LevelLoadProgressTracker(true);
   private @Nullable ChunkLoadStatusView serverChunkStatusView;
   private volatile LevelLoadListener.@Nullable Stage serverStage;
   private LevelLoadTracker.@Nullable ClientState clientState;
   private final long closeDelayMs;

   public LevelLoadTracker() {
      this(0L);
   }

   public LevelLoadTracker(final long closeDelayMs) {
      this.closeDelayMs = closeDelayMs;
   }

   public void setServerChunkStatusView(final ChunkLoadStatusView serverChunkStatusView) {
      this.serverChunkStatusView = serverChunkStatusView;
   }

   public void startClientLoad(final LocalPlayer player, final ClientLevel level) {
      this.clientState = new LevelLoadTracker.WaitingForServer(player, level, Util.getMillis() + CLIENT_WAIT_TIMEOUT_MS);
   }

   public void tickClientLoad() {
      if (this.clientState != null) {
         this.clientState = this.clientState.tick();
      }

   }

   public boolean isLevelReady() {
      LevelLoadTracker.ClientState var4 = this.clientState;
      if (var4 instanceof LevelLoadTracker.ClientLevelReady var3) {
         LevelLoadTracker.ClientLevelReady var10000 = var3;

         try {
            var10 = var10000.readyAt();
         } catch (Throwable var9) {
            throw new MatchException(var9.toString(), var9);
         }

         long var5 = var10;
         if (true && Util.getMillis() >= var5 + this.closeDelayMs) {
            return true;
         }
      }

      return false;
   }

   public void loadingPacketsReceived() {
      if (this.clientState != null) {
         this.clientState = this.clientState.loadingPacketsReceived();
      }

   }

   public void start(final LevelLoadListener.Stage stage, final int totalChunks) {
      this.serverProgressTracker.start(stage, totalChunks);
      this.serverStage = stage;
   }

   public void update(final LevelLoadListener.Stage stage, final int currentChunks, final int totalChunks) {
      this.serverProgressTracker.update(stage, currentChunks, totalChunks);
   }

   public void finish(final LevelLoadListener.Stage stage) {
      this.serverProgressTracker.finish(stage);
   }

   public void updateFocus(final ResourceKey dimension, final ChunkPos chunkPos) {
      if (this.serverChunkStatusView != null) {
         this.serverChunkStatusView.moveTo(dimension, chunkPos);
      }

   }

   public @Nullable ChunkLoadStatusView statusView() {
      return this.serverChunkStatusView;
   }

   public float serverProgress() {
      return this.serverProgressTracker.get();
   }

   public boolean hasProgress() {
      return this.serverStage != null;
   }

   public @Nullable Runnable getPlayerCompiledSectionCallback() {
      LevelLoadTracker.ClientState var2 = this.clientState;
      if (var2 instanceof LevelLoadTracker.WaitingForPlayerChunk waitingForPlayerChunk) {
         return () -> waitingForPlayerChunk.playerSectionReady().set(true);
      } else {
         return null;
      }
   }

   private static record ClientLevelReady(long readyAt) implements LevelLoadTracker.ClientState {
   }

   private sealed interface ClientState permits LevelLoadTracker.ClientLevelReady, LevelLoadTracker.WaitingForPlayerChunk, LevelLoadTracker.WaitingForServer {
      default LevelLoadTracker.ClientState tick() {
         return this;
      }

      default LevelLoadTracker.ClientState loadingPacketsReceived() {
         return this;
      }
   }

   private static record WaitingForPlayerChunk(LocalPlayer player, ClientLevel level, AtomicBoolean playerSectionReady, long timeoutAfter) implements LevelLoadTracker.ClientState {
      public LevelLoadTracker.ClientState tick() {
         return (LevelLoadTracker.ClientState)(this.isReady() ? new LevelLoadTracker.ClientLevelReady(Util.getMillis()) : this);
      }

      private boolean isReady() {
         if (Util.getMillis() > this.timeoutAfter) {
            LevelLoadTracker.LOGGER.warn("Timed out while waiting for the client to load chunks, letting the player into the world anyway");
            return true;
         } else {
            BlockPos cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().blockPosition();
            return !this.level.isOutsideBuildHeight(cameraPos.getY()) && !this.player.isSpectator() && this.player.isAlive() ? this.playerSectionReady.get() : true;
         }
      }
   }

   private static record WaitingForServer(LocalPlayer player, ClientLevel level, long timeoutAfter) implements LevelLoadTracker.ClientState {
      public LevelLoadTracker.ClientState loadingPacketsReceived() {
         return new LevelLoadTracker.WaitingForPlayerChunk(this.player, this.level, new AtomicBoolean(), this.timeoutAfter);
      }
   }
}
