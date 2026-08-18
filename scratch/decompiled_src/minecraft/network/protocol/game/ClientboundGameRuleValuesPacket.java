package net.minecraft.network.protocol.game;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gamerules.GameRule;

public record ClientboundGameRuleValuesPacket(Map values) implements Packet {
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.map(HashMap::new, ResourceKey.streamCodec(Registries.GAME_RULE), ByteBufCodecs.STRING_UTF8).map(ClientboundGameRuleValuesPacket::new, ClientboundGameRuleValuesPacket::values);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_GAME_RULE_VALUES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleGameRuleValues(this);
   }
}
