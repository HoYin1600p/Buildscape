package net.minecraft.network.protocol.game;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerCombatKillPacket(int playerId, Component message) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundPlayerCombatKillPacket::playerId, ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundPlayerCombatKillPacket::message, ClientboundPlayerCombatKillPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_COMBAT_KILL;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handlePlayerCombatKill(this);
   }

   public boolean isSkippable() {
      return true;
   }
}
