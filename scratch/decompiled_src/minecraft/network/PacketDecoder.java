package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketDecoder extends ByteToMessageDecoder implements ProtocolSwapHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ProtocolInfo protocolInfo;

   public PacketDecoder(final ProtocolInfo protocolInfo) {
      this.protocolInfo = protocolInfo;
   }

   protected void decode(final ChannelHandlerContext ctx, final ByteBuf input, final List out) throws Exception {
      int readableBytes = input.readableBytes();

      Packet packet;
      try {
         packet = (Packet)this.protocolInfo.codec().decode(input);
      } catch (Exception var7) {
         if (var7 instanceof SkipPacketException) {
            input.skipBytes(input.readableBytes());
         }

         throw var7;
      }

      PacketType packetId = packet.type();
      JvmProfiler.INSTANCE.onPacketReceived(this.protocolInfo.id(), packetId, ctx.channel().remoteAddress(), readableBytes);
      if (input.readableBytes() > 0) {
         throw new IOException("Packet " + this.protocolInfo.id().id() + "/" + String.valueOf(packetId) + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + input.readableBytes() + " bytes extra whilst reading packet " + String.valueOf(packetId));
      } else {
         out.add(packet);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Connection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {} -> {} bytes", new Object[]{this.protocolInfo.id().id(), packetId, packet.getClass().getName(), readableBytes});
         }

         ProtocolSwapHandler.handleInboundTerminalPacket(ctx, packet);
      }
   }
}
