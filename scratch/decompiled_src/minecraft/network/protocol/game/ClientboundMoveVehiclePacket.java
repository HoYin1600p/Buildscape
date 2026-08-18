package net.minecraft.network.protocol.game;

import net.minecraft.core.PositionAndRotation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public record ClientboundMoveVehiclePacket(PositionAndRotation movingTo) implements Packet {
   public static final StreamCodec STREAM_CODEC = PositionAndRotation.STREAM_CODEC.map(ClientboundMoveVehiclePacket::new, ClientboundMoveVehiclePacket::movingTo);

   public static ClientboundMoveVehiclePacket fromEntity(final Entity entity) {
      return new ClientboundMoveVehiclePacket(entity.storePositionAndRotation());
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_MOVE_VEHICLE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleMoveVehicle(this);
   }
}
