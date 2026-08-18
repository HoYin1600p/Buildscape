package net.minecraft.network.protocol.common.custom;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.Identifier;

public interface CustomPacketPayload {
   CustomPacketPayload.Type type();

   static StreamCodec codec(final StreamMemberEncoder writer, final StreamDecoder reader) {
      return StreamCodec.ofMember(writer, reader);
   }

   static CustomPacketPayload.Type createType(final String id) {
      return new CustomPacketPayload.Type(Identifier.withDefaultNamespace(id));
   }

   static StreamCodec codec(final CustomPacketPayload.FallbackProvider fallback, final List types) {
      final Map idToType = (Map)types.stream().collect(Collectors.toUnmodifiableMap((t) -> t.type().id(), CustomPacketPayload.TypeAndCodec::codec));
      return new StreamCodec() {
         private StreamCodec findCodec(final Identifier typeId) {
            StreamCodec codec = (StreamCodec)idToType.get(typeId);
            return codec != null ? codec : fallback.create(typeId);
         }

         private void writeCap(final FriendlyByteBuf output, final CustomPacketPayload.Type type, final CustomPacketPayload payload) {
            output.writeIdentifier(type.id());
            StreamCodec codec = this.findCodec(type.id);
            codec.encode(output, payload);
         }

         public void encode(final FriendlyByteBuf output, final CustomPacketPayload value) {
            this.writeCap(output, value.type(), value);
         }

         public CustomPacketPayload decode(final FriendlyByteBuf input) {
            Identifier identifier = input.readIdentifier();
            return (CustomPacketPayload)this.findCodec(identifier).decode(input);
         }
      };
   }

   public interface FallbackProvider {
      StreamCodec create(Identifier typeId);
   }

   public static record Type(Identifier id) {
   }

   public static record TypeAndCodec(CustomPacketPayload.Type type, StreamCodec codec) {
   }
}
