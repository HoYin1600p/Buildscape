package net.minecraft.network;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.network.chat.Component;

public record DisconnectionDetails(Component reason, Optional report, Optional bugReportLink) {
   public DisconnectionDetails(final Component reason) {
      this(reason, Optional.empty(), Optional.empty());
   }
}
