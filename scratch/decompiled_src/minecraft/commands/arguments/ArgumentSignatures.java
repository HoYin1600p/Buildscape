package net.minecraft.commands.arguments;

import java.util.List;
import java.util.Objects;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

public record ArgumentSignatures(List entries) {
   public static final ArgumentSignatures EMPTY = new ArgumentSignatures(List.of());
   private static final int MAX_ARGUMENT_COUNT = 8;
   private static final int MAX_ARGUMENT_NAME_LENGTH = 16;
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ArgumentSignatures.Entry.STREAM_CODEC.apply(ByteBufCodecs.list(8)), ArgumentSignatures::entries, ArgumentSignatures::new);

   public static ArgumentSignatures signCommand(final SignableCommand command, final ArgumentSignatures.Signer signer) {
      List entries = command.arguments().stream().map((argument) -> {
         MessageSignature signature = signer.sign(argument.value());
         return signature != null ? new ArgumentSignatures.Entry(argument.name(), signature) : null;
      }).filter(Objects::nonNull).toList();
      return new ArgumentSignatures(entries);
   }

   public static record Entry(String name, MessageSignature signature) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(16), ArgumentSignatures.Entry::name, MessageSignature.STREAM_CODEC, ArgumentSignatures.Entry::signature, ArgumentSignatures.Entry::new);
   }

   @FunctionalInterface
   public interface Signer {
      @Nullable MessageSignature sign(String content);
   }
}
