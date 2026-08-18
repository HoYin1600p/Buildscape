package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundPlayerCombatEndPacket::write, ClientboundPlayerCombatEndPacket::new);
   private final int duration;

   public ClientboundPlayerCombatEndPacket(final CombatTracker tracker) {
      this(tracker.getCombatDuration());
   }

   public ClientboundPlayerCombatEndPacket(final int duration) {
      this.duration = duration;
   }

   private ClientboundPlayerCombatEndPacket(final FriendlyByteBuf input) {
      this.duration = input.readVarInt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.duration);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_COMBAT_END;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handlePlayerCombatEnd(this);
   }
}
