package net.minecraft.util.debug;

import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

public interface DebugValueSource {
   void registerDebugValues(ServerLevel level, DebugValueSource.Registration registration);

   public interface Registration {
      void register(DebugSubscription subscription, DebugValueSource.ValueGetter getter);
   }

   public interface ValueGetter {
      @Nullable Object get();
   }
}
