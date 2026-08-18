package net.minecraft.network.protocol.common;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundResourcePackPacket(UUID id, ServerboundResourcePackPacket.Action action) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundResourcePackPacket::write, ServerboundResourcePackPacket::new);

   private ServerboundResourcePackPacket(final FriendlyByteBuf input) {
      this(input.readUUID(), (ServerboundResourcePackPacket.Action)input.readEnum(ServerboundResourcePackPacket.Action.class));
   }

   private void write(final FriendlyByteBuf output) {
      output.writeUUID(this.id);
      output.writeEnum(this.action);
   }

   public PacketType type() {
      return CommonPacketTypes.SERVERBOUND_RESOURCE_PACK;
   }

   public void handle(final ServerCommonPacketListener listener) {
      listener.handleResourcePackResponse(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED,
      DOWNLOADED,
      INVALID_URL,
      FAILED_RELOAD,
      DISCARDED;

      public boolean isTerminal() {
         return this != ACCEPTED && this != DOWNLOADED;
      }

      // $FF: synthetic method
      private static ServerboundResourcePackPacket.Action[] $values() {
         return new ServerboundResourcePackPacket.Action[]{SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED, DOWNLOADED, INVALID_URL, FAILED_RELOAD, DISCARDED};
      }
   }
}
