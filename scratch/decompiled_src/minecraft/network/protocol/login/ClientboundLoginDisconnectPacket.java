package net.minecraft.network.protocol.login;

import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.RegistryOps;

public record ClientboundLoginDisconnectPacket(Component reason) implements Packet {
   private static final RegistryOps OPS = RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE);
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.lenientJson(262144).apply(ByteBufCodecs.fromCodec(OPS, ComponentSerialization.CODEC)), ClientboundLoginDisconnectPacket::reason, ClientboundLoginDisconnectPacket::new);

   public PacketType type() {
      return LoginPacketTypes.CLIENTBOUND_LOGIN_DISCONNECT;
   }

   public void handle(final ClientLoginPacketListener listener) {
      listener.handleDisconnect(this);
   }
}
