package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.storage.LevelData;

public record ClientboundSetDefaultSpawnPositionPacket(LevelData.RespawnData respawnData) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(LevelData.RespawnData.STREAM_CODEC, ClientboundSetDefaultSpawnPositionPacket::respawnData, ClientboundSetDefaultSpawnPositionPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_DEFAULT_SPAWN_POSITION;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetSpawn(this);
   }
}
