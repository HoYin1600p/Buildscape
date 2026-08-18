package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundClientCommandPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundClientCommandPacket::write, ServerboundClientCommandPacket::new);
   private final ServerboundClientCommandPacket.Action action;

   public ServerboundClientCommandPacket(final ServerboundClientCommandPacket.Action action) {
      this.action = action;
   }

   private ServerboundClientCommandPacket(final FriendlyByteBuf input) {
      this.action = (ServerboundClientCommandPacket.Action)input.readEnum(ServerboundClientCommandPacket.Action.class);
   }

   private void write(final FriendlyByteBuf output) {
      output.writeEnum(this.action);
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CLIENT_COMMAND;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleClientCommand(this);
   }

   public ServerboundClientCommandPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      PERFORM_RESPAWN,
      REQUEST_STATS,
      REQUEST_GAMERULE_VALUES;

      // $FF: synthetic method
      private static ServerboundClientCommandPacket.Action[] $values() {
         return new ServerboundClientCommandPacket.Action[]{PERFORM_RESPAWN, REQUEST_STATS, REQUEST_GAMERULE_VALUES};
      }
   }
}
