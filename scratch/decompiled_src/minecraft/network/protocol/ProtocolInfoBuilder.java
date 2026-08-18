package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import org.jspecify.annotations.Nullable;

public class ProtocolInfoBuilder {
   private final ConnectionProtocol protocol;
   private final PacketFlow flow;
   private final List codecs = new ArrayList();
   private @Nullable BundlerInfo bundlerInfo;

   public ProtocolInfoBuilder(final ConnectionProtocol protocol, final PacketFlow flow) {
      this.protocol = protocol;
      this.flow = flow;
   }

   public ProtocolInfoBuilder addPacket(final PacketType type, final StreamCodec serializer) {
      this.codecs.add(new ProtocolInfoBuilder.CodecEntry(type, serializer, (CodecModifier)null));
      return this;
   }

   public ProtocolInfoBuilder addPacket(final PacketType type, final StreamCodec serializer, final CodecModifier modifier) {
      this.codecs.add(new ProtocolInfoBuilder.CodecEntry(type, serializer, modifier));
      return this;
   }

   public ProtocolInfoBuilder withBundlePacket(final PacketType bundlerPacket, final Function constructor, final BundleDelimiterPacket delimiterPacket) {
      StreamCodec delimitedCodec = StreamCodec.unit(delimiterPacket);
      PacketType delimiterType = delimiterPacket.type();
      this.codecs.add(new ProtocolInfoBuilder.CodecEntry(delimiterType, delimitedCodec, (CodecModifier)null));
      this.bundlerInfo = BundlerInfo.createForPacket(bundlerPacket, constructor, delimiterPacket);
      return this;
   }

   private StreamCodec buildPacketCodec(final Function contextWrapper, final List codecs, final Object context) {
      ProtocolCodecBuilder codecBuilder = new ProtocolCodecBuilder(this.flow);

      for(ProtocolInfoBuilder.CodecEntry codec : codecs) {
         codec.addToBuilder(codecBuilder, contextWrapper, context);
      }

      return codecBuilder.build();
   }

   private static ProtocolInfo.Details buildDetails(final ConnectionProtocol protocol, final PacketFlow flow, final List codecs) {
      return new ProtocolInfo.Details() {
         public ConnectionProtocol id() {
            return protocol;
         }

         public PacketFlow flow() {
            return flow;
         }

         public void listPackets(final ProtocolInfo.Details.PacketVisitor output) {
            for(int i = 0; i < codecs.size(); ++i) {
               ProtocolInfoBuilder.CodecEntry entry = (ProtocolInfoBuilder.CodecEntry)codecs.get(i);
               output.accept(entry.type, i);
            }

         }
      };
   }

   public SimpleUnboundProtocol buildUnbound(final Object context) {
      final List codecs = List.copyOf(this.codecs);
      final BundlerInfo bundlerInfo = this.bundlerInfo;
      final ProtocolInfo.Details details = buildDetails(this.protocol, this.flow, codecs);
      return new SimpleUnboundProtocol() {
         {
            Objects.requireNonNull(ProtocolInfoBuilder.this);
         }

         public ProtocolInfo bind(final Function contextWrapper) {
            return new ProtocolInfoBuilder.Implementation(ProtocolInfoBuilder.this.protocol, ProtocolInfoBuilder.this.flow, ProtocolInfoBuilder.this.buildPacketCodec(contextWrapper, codecs, context), bundlerInfo);
         }

         public ProtocolInfo.Details details() {
            return details;
         }
      };
   }

   public UnboundProtocol buildUnbound() {
      final List codecs = List.copyOf(this.codecs);
      final BundlerInfo bundlerInfo = this.bundlerInfo;
      final ProtocolInfo.Details details = buildDetails(this.protocol, this.flow, codecs);
      return new UnboundProtocol() {
         {
            Objects.requireNonNull(ProtocolInfoBuilder.this);
         }

         public ProtocolInfo bind(final Function contextWrapper, final Object context) {
            return new ProtocolInfoBuilder.Implementation(ProtocolInfoBuilder.this.protocol, ProtocolInfoBuilder.this.flow, ProtocolInfoBuilder.this.buildPacketCodec(contextWrapper, codecs, context), bundlerInfo);
         }

         public ProtocolInfo.Details details() {
            return details;
         }
      };
   }

   private static SimpleUnboundProtocol protocol(final ConnectionProtocol id, final PacketFlow flow, final Consumer config) {
      ProtocolInfoBuilder builder = new ProtocolInfoBuilder(id, flow);
      config.accept(builder);
      return builder.buildUnbound(Unit.INSTANCE);
   }

   public static SimpleUnboundProtocol serverboundProtocol(final ConnectionProtocol id, final Consumer config) {
      return protocol(id, PacketFlow.SERVERBOUND, config);
   }

   public static SimpleUnboundProtocol clientboundProtocol(final ConnectionProtocol id, final Consumer config) {
      return protocol(id, PacketFlow.CLIENTBOUND, config);
   }

   private static UnboundProtocol contextProtocol(final ConnectionProtocol id, final PacketFlow flow, final Consumer config) {
      ProtocolInfoBuilder builder = new ProtocolInfoBuilder(id, flow);
      config.accept(builder);
      return builder.buildUnbound();
   }

   public static UnboundProtocol contextServerboundProtocol(final ConnectionProtocol id, final Consumer config) {
      return contextProtocol(id, PacketFlow.SERVERBOUND, config);
   }

   public static UnboundProtocol contextClientboundProtocol(final ConnectionProtocol id, final Consumer config) {
      return contextProtocol(id, PacketFlow.CLIENTBOUND, config);
   }

   private static record CodecEntry(PacketType type, StreamCodec serializer, @Nullable CodecModifier modifier) {
      public void addToBuilder(final ProtocolCodecBuilder codecBuilder, final Function contextWrapper, final Object context) {
         StreamCodec finalSerializer;
         if (this.modifier != null) {
            finalSerializer = this.modifier.apply(this.serializer, context);
         } else {
            finalSerializer = this.serializer;
         }

         StreamCodec baseCodec = finalSerializer.mapStream(contextWrapper);
         codecBuilder.add(this.type, baseCodec);
      }
   }

   private static record Implementation(ConnectionProtocol id, PacketFlow flow, StreamCodec codec, @Nullable BundlerInfo bundlerInfo) implements ProtocolInfo {
   }
}
