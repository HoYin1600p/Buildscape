package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundLoginFinishedPacket(GameProfile gameProfile, UUID sessionId) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.GAME_PROFILE, ClientboundLoginFinishedPacket::gameProfile, UUIDUtil.STREAM_CODEC, ClientboundLoginFinishedPacket::sessionId, ClientboundLoginFinishedPacket::new);

   public PacketType type() {
      return LoginPacketTypes.CLIENTBOUND_LOGIN_FINISHED;
   }

   public void handle(final ClientLoginPacketListener listener) {
      listener.handleLoginFinished(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
