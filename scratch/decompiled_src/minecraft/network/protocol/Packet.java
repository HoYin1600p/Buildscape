package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;

public interface Packet {
   PacketType type();

   void handle(PacketListener listener);

   default boolean isSkippable() {
      return false;
   }

   default boolean isTerminal() {
      return false;
   }

   static StreamCodec codec(final StreamMemberEncoder writer, final StreamDecoder reader) {
      return StreamCodec.ofMember(writer, reader);
   }
}
