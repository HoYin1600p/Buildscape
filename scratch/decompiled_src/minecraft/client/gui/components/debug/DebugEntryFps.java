package net.minecraft.client.gui.components.debug;

import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.renderpearl.api.device.GpuSurface;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryFps implements DebugScreenEntry {
   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      int framerateLimit = minecraft.getFramerateLimitTracker().getFramerateLimit();
      Optional surfaceConfiguration = minecraft.windowSurface().currentConfiguration();
      VideoMode activeMode = minecraft.getWindow().getActiveVideoMode();
      displayer.addPriorityLine(String.format(Locale.ROOT, "%d fps T: %s%s @%sHz", minecraft.getFps(), framerateLimit == 260 ? "inf" : framerateLimit, presentModeName((GpuSurface.PresentMode)surfaceConfiguration.map(GpuSurface.Configuration::presentMode).orElse((Object)null)), activeMode == null ? "0" : activeMode.refreshRateLabel()));
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }

   private static String presentModeName(final GpuSurface.@Nullable PresentMode mode) {
      byte var2 = 0;
      String var10000;
      switch (mode.enumSwitch<invokedynamic>(mode, var2)) {
         case -1:
            var10000 = "";
            break;
         case 0:
            var10000 = " (immediate)";
            break;
         case 1:
            var10000 = " (mailbox)";
            break;
         case 2:
            var10000 = " (fifo)";
            break;
         case 3:
            var10000 = " (fifo relaxed)";
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
