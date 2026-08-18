package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int id, List packedItems) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundSetEntityDataPacket::write, ClientboundSetEntityDataPacket::new);
   public static final int EOF_MARKER = 255;

   private ClientboundSetEntityDataPacket(final RegistryFriendlyByteBuf input) {
      this(input.readVarInt(), unpack(input));
   }

   private static void pack(final List items, final RegistryFriendlyByteBuf output) {
      for(SynchedEntityData.DataValue item : items) {
         item.write(output);
      }

      output.writeByte(255);
   }

   private static List unpack(final RegistryFriendlyByteBuf input) {
      List result = new ArrayList();

      int id;
      while((id = input.readUnsignedByte()) != 255) {
         result.add(SynchedEntityData.DataValue.read(input, id));
      }

      return result;
   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeVarInt(this.id);
      pack(this.packedItems, output);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_ENTITY_DATA;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetEntityData(this);
   }
}
