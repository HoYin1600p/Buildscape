package net.minecraft.network.chat;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;
import org.jspecify.annotations.Nullable;

public record MessageSignature(byte[] bytes) {
   public static final Codec CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::bytes);
   public static final StreamCodec STREAM_CODEC = StreamCodec.of(MessageSignature::write, MessageSignature::read);
   public static final int BYTES = 256;

   public MessageSignature {
      Preconditions.checkState(bytes.length == 256, "Invalid message signature size");
   }

   private static MessageSignature read(final ByteBuf input) {
      byte[] bytes = new byte[256];
      input.readBytes(bytes);
      return new MessageSignature(bytes);
   }

   private static void write(final ByteBuf output, final MessageSignature signature) {
      output.writeBytes(signature.bytes);
   }

   public boolean verify(final SignatureValidator signature, final SignatureUpdater updater) {
      return signature.validate(updater, this.bytes);
   }

   public ByteBuffer asByteBuffer() {
      return ByteBuffer.wrap(this.bytes);
   }

   public boolean equals(final Object o) {
      if (this != o) {
         if (o instanceof MessageSignature) {
            MessageSignature that = (MessageSignature)o;
            if (Arrays.equals(this.bytes, that.bytes)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.bytes);
   }

   public String toString() {
      return Base64.getEncoder().encodeToString(this.bytes);
   }

   public static String describe(final @Nullable MessageSignature signature) {
      return signature == null ? "<no signature>" : signature.toString();
   }

   public MessageSignature.Packed pack(final MessageSignatureCache cache) {
      int packedId = cache.pack(this);
      return packedId != -1 ? new MessageSignature.Packed(packedId) : new MessageSignature.Packed(this);
   }

   public int checksum() {
      return Arrays.hashCode(this.bytes);
   }

   public static record Packed(int id, @Nullable MessageSignature fullSignature) {
      public static final int FULL_SIGNATURE = -1;
      public static final StreamCodec STREAM_CODEC = StreamCodec.of(MessageSignature.Packed::write, MessageSignature.Packed::read);

      public Packed(final MessageSignature signature) {
         this(-1, signature);
      }

      public Packed(final int id) {
         this(id, (MessageSignature)null);
      }

      private static MessageSignature.Packed read(final ByteBuf input) {
         int id = VarInt.read(input) - 1;
         return id == -1 ? new MessageSignature.Packed(MessageSignature.read(input)) : new MessageSignature.Packed(id);
      }

      private static void write(final ByteBuf output, final MessageSignature.Packed packed) {
         VarInt.write(output, packed.id() + 1);
         if (packed.fullSignature() != null) {
            MessageSignature.write(output, packed.fullSignature());
         }

      }

      public Optional unpack(final MessageSignatureCache cache) {
         return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(cache.unpack(this.id));
      }
   }
}
