package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundEditBookPacket(int slot, List pages, Optional title) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ServerboundEditBookPacket::slot, ByteBufCodecs.stringUtf8(1024).apply(ByteBufCodecs.list(100)), ServerboundEditBookPacket::pages, ByteBufCodecs.stringUtf8(32).apply(ByteBufCodecs::optional), ServerboundEditBookPacket::title, ServerboundEditBookPacket::new);

   public ServerboundEditBookPacket {
      pages = List.copyOf(pages);
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_EDIT_BOOK;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleEditBook(this);
   }
}
