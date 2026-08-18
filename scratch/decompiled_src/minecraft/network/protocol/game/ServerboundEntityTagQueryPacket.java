package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundEntityTagQueryPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundEntityTagQueryPacket::write, ServerboundEntityTagQueryPacket::new);
   private final int transactionId;
   private final int entityId;

   public ServerboundEntityTagQueryPacket(final int transactionId, final int entityId) {
      this.transactionId = transactionId;
      this.entityId = entityId;
   }

   private ServerboundEntityTagQueryPacket(final FriendlyByteBuf input) {
      this.transactionId = input.readVarInt();
      this.entityId = input.readVarInt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.transactionId);
      output.writeVarInt(this.entityId);
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_ENTITY_TAG_QUERY;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
