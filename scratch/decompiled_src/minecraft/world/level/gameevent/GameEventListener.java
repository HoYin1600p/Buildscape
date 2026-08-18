package net.minecraft.world.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public interface GameEventListener {
   PositionSource getListenerSource();

   int getListenerRadius();

   boolean handleGameEvent(ServerLevel level, Holder event, GameEvent.Context context, Vec3 sourcePosition);

   default GameEventListener.DeliveryMode getDeliveryMode() {
      return GameEventListener.DeliveryMode.UNSPECIFIED;
   }

   public static enum DeliveryMode {
      UNSPECIFIED,
      BY_DISTANCE;

      // $FF: synthetic method
      private static GameEventListener.DeliveryMode[] $values() {
         return new GameEventListener.DeliveryMode[]{UNSPECIFIED, BY_DISTANCE};
      }
   }

   public interface Provider {
      GameEventListener getListener();
   }
}
