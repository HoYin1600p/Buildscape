package net.minecraft.network.protocol.login;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundLoginAcknowledgedPacket implements Packet {
   public static final ServerboundLoginAcknowledgedPacket INSTANCE = new ServerboundLoginAcknowledgedPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ServerboundLoginAcknowledgedPacket() {
   }

   public PacketType type() {
      return LoginPacketTypes.SERVERBOUND_LOGIN_ACKNOWLEDGED;
   }

   public void handle(final ServerLoginPacketListener listener) {
      listener.handleLoginAcknowledgement(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
