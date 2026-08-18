package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.SignatureUpdater;

public record LastSeenMessages(List entries) {
   public static final Codec CODEC = MessageSignature.CODEC.listOf().xmap(LastSeenMessages::new, LastSeenMessages::entries);
   public static final LastSeenMessages EMPTY = new LastSeenMessages(List.of());
   public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 20;

   public void updateSignature(final SignatureUpdater.Output output) throws SignatureException {
      output.update(Ints.toByteArray(this.entries.size()));

      for(MessageSignature entry : this.entries) {
         output.update(entry.bytes());
      }

   }

   public LastSeenMessages.Packed pack(final MessageSignatureCache cache) {
      return new LastSeenMessages.Packed(this.entries.stream().map((entry) -> entry.pack(cache)).toList());
   }

   public byte computeChecksum() {
      int checksum = 1;

      for(MessageSignature entry : this.entries) {
         checksum = 31 * checksum + entry.checksum();
      }

      byte checksumByte = (byte)checksum;
      return checksumByte == 0 ? 1 : checksumByte;
   }

   public static record Packed(List entries) {
      public static final LastSeenMessages.Packed EMPTY = new LastSeenMessages.Packed(List.of());
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(MessageSignature.Packed.STREAM_CODEC.apply(ByteBufCodecs.list(20)), LastSeenMessages.Packed::entries, LastSeenMessages.Packed::new);

      public Optional unpack(final MessageSignatureCache cache) {
         List unpacked = new ArrayList(this.entries.size());

         for(MessageSignature.Packed packed : this.entries) {
            Optional entry = packed.unpack(cache);
            if (entry.isEmpty()) {
               return Optional.empty();
            }

            unpacked.add((MessageSignature)entry.get());
         }

         return Optional.of(new LastSeenMessages(unpacked));
      }
   }

   public static record Update(int offset, BitSet acknowledged, byte checksum) {
      public static final byte IGNORE_CHECKSUM = 0;
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, LastSeenMessages.Update::offset, ByteBufCodecs.fixedBitSet(20), LastSeenMessages.Update::acknowledged, ByteBufCodecs.BYTE, LastSeenMessages.Update::checksum, LastSeenMessages.Update::new);

      public boolean verifyChecksum(final LastSeenMessages lastSeen) {
         return this.checksum == 0 || this.checksum == lastSeen.computeChecksum();
      }
   }
}
