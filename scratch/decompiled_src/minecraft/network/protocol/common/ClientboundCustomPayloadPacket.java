package net.minecraft.network.protocol.common;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.util.Util;

public record ClientboundCustomPayloadPacket(CustomPacketPayload payload) implements Packet {
   private static final int MAX_PAYLOAD_SIZE = 1048576;
   public static final StreamCodec GAMEPLAY_STREAM_CODEC = CustomPacketPayload.codec((id) -> DiscardedPayload.codec(id, 1048576), (List)Util.make(Lists.newArrayList(new CustomPacketPayload.TypeAndCodec[]{new CustomPacketPayload.TypeAndCodec(BrandPayload.TYPE, BrandPayload.STREAM_CODEC)}), (types) -> {
   })).map(ClientboundCustomPayloadPacket::new, ClientboundCustomPayloadPacket::payload);
   public static final StreamCodec CONFIG_STREAM_CODEC = CustomPacketPayload.codec((id) -> DiscardedPayload.codec(id, 1048576), List.of(new CustomPacketPayload.TypeAndCodec(BrandPayload.TYPE, BrandPayload.STREAM_CODEC))).map(ClientboundCustomPayloadPacket::new, ClientboundCustomPayloadPacket::payload);

   public PacketType type() {
      return CommonPacketTypes.CLIENTBOUND_CUSTOM_PAYLOAD;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleCustomPayload(this);
   }
}
