package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketEncoder extends MessageToByteEncoder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ProtocolInfo protocolInfo;

   public PacketEncoder(final ProtocolInfo protocolInfo) {
      this.protocolInfo = protocolInfo;
   }

   protected void encode(final ChannelHandlerContext ctx, final Packet packet, final ByteBuf output) throws Exception {
      PacketType packetId = packet.type();

      try {
         this.protocolInfo.codec().encode(output, packet);
         int writtenBytes = output.readableBytes();
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {} -> {} bytes", new Object[]{this.protocolInfo.id().id(), packetId, packet.getClass().getName(), writtenBytes});
         }

         JvmProfiler.INSTANCE.onPacketSent(this.protocolInfo.id(), packetId, ctx.channel().remoteAddress(), writtenBytes);
      } catch (Throwable var9) {
         LOGGER.error("Error sending packet {}", packetId, var9);
         if (packet.isSkippable()) {
            throw new SkipPacketEncoderException(var9);
         }

         throw var9;
      } finally {
         ProtocolSwapHandler.handleOutboundTerminalPacket(ctx, packet);
      }

   }
}
