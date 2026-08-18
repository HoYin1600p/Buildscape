package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ClientboundEntityEventPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundEntityEventPacket::write, ClientboundEntityEventPacket::new);
   private final int entityId;
   private final @EntityEvent.Value byte eventId;

   public ClientboundEntityEventPacket(final Entity entity, final @EntityEvent.Value byte eventId) {
      this.entityId = entity.getId();
      this.eventId = eventId;
   }

   private ClientboundEntityEventPacket(final FriendlyByteBuf input) {
      this.entityId = input.readInt();
      this.eventId = input.readByte();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeInt(this.entityId);
      output.writeByte(this.eventId);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_ENTITY_EVENT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleEntityEvent(this);
   }

   public @Nullable Entity getEntity(final Level level) {
      return level.getEntity(this.entityId);
   }

   public @EntityEvent.Value byte getEventId() {
      return this.eventId;
   }
}
