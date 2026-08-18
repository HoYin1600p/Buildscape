package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gamerules.GameRule;

public record ServerboundSetGameRulePacket(List entries) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ServerboundSetGameRulePacket.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ServerboundSetGameRulePacket::entries, ServerboundSetGameRulePacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_SET_GAME_RULE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleSetGameRule(this);
   }

   public static record Entry(ResourceKey gameRuleKey, String value) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(Registries.GAME_RULE), ServerboundSetGameRulePacket.Entry::gameRuleKey, ByteBufCodecs.STRING_UTF8, ServerboundSetGameRulePacket.Entry::value, ServerboundSetGameRulePacket.Entry::new);
   }
}
