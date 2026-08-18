package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;

public interface LoggedChatEvent {
   Codec CODEC = StringRepresentable.fromEnum(LoggedChatEvent.Type::values).dispatch(LoggedChatEvent::type, LoggedChatEvent.Type::codec);

   LoggedChatEvent.Type type();

   public static enum Type implements StringRepresentable {
      PLAYER("player", () -> LoggedChatMessage.Player.CODEC),
      SYSTEM("system", () -> LoggedChatMessage.System.CODEC);

      private final String serializedName;
      private final Supplier codec;

      private Type(final String serializedName, final Supplier codec) {
         this.serializedName = serializedName;
         this.codec = codec;
      }

      private MapCodec codec() {
         return (MapCodec)this.codec.get();
      }

      public String getSerializedName() {
         return this.serializedName;
      }

      // $FF: synthetic method
      private static LoggedChatEvent.Type[] $values() {
         return new LoggedChatEvent.Type[]{PLAYER, SYSTEM};
      }
   }
}
