package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ClientboundSetCameraPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundSetCameraPacket::write, ClientboundSetCameraPacket::new);
   private final int cameraId;

   public ClientboundSetCameraPacket(final Entity camera) {
      this.cameraId = camera.getId();
   }

   private ClientboundSetCameraPacket(final FriendlyByteBuf input) {
      this.cameraId = input.readVarInt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.cameraId);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_CAMERA;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetCamera(this);
   }

   public @Nullable Entity getEntity(final Level level) {
      return level.getEntity(this.cameraId);
   }
}
