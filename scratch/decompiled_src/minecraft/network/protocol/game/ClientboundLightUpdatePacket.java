package net.minecraft.network.protocol.game;

import java.util.BitSet;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;

public record ClientboundLightUpdatePacket(int x, int z, ClientboundLightUpdatePacketData lightData) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundLightUpdatePacket::x, ByteBufCodecs.VAR_INT, ClientboundLightUpdatePacket::z, ClientboundLightUpdatePacketData.STREAM_CODEC, ClientboundLightUpdatePacket::lightData, ClientboundLightUpdatePacket::new);

   public ClientboundLightUpdatePacket(final ChunkPos pos, final LevelLightEngine lightEngine, final @Nullable BitSet skyChangedLightSectionFilter, final @Nullable BitSet blockChangedLightSectionFilter) {
      this(pos.x(), pos.z(), new ClientboundLightUpdatePacketData(pos, lightEngine, skyChangedLightSectionFilter, blockChangedLightSectionFilter));
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_LIGHT_UPDATE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleLightUpdatePacket(this);
   }
}
