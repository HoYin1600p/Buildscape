package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ClientboundSelectAdvancementsTabPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundSelectAdvancementsTabPacket::write, ClientboundSelectAdvancementsTabPacket::new);
   private final @Nullable Identifier tab;

   public ClientboundSelectAdvancementsTabPacket(final @Nullable Identifier tab) {
      this.tab = tab;
   }

   private ClientboundSelectAdvancementsTabPacket(final FriendlyByteBuf input) {
      this.tab = (Identifier)input.readNullable(FriendlyByteBuf::readIdentifier);
   }

   private void write(final FriendlyByteBuf output) {
      output.writeNullable(this.tab, FriendlyByteBuf::writeIdentifier);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SELECT_ADVANCEMENTS_TAB;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSelectAdvancementsTab(this);
   }

   public @Nullable Identifier getTab() {
      return this.tab;
   }
}
