package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundPlayerCombatEnterPacket implements Packet {
   public static final ClientboundPlayerCombatEnterPacket INSTANCE = new ClientboundPlayerCombatEnterPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ClientboundPlayerCombatEnterPacket() {
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_COMBAT_ENTER;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handlePlayerCombatEnter(this);
   }
}
