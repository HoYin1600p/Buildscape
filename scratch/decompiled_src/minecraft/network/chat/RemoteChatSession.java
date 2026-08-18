package net.minecraft.network.chat;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {
   public SignedMessageValidator createMessageValidator(final Duration gracePeriod) {
      return new SignedMessageValidator.KeyBased(this.profilePublicKey.createSignatureValidator(), () -> this.profilePublicKey.data().hasExpired(gracePeriod));
   }

   public SignedMessageChain.Decoder createMessageDecoder(final UUID profileId) {
      return (new SignedMessageChain(profileId, this.sessionId)).decoder(this.profilePublicKey);
   }

   public RemoteChatSession.Data asData() {
      return new RemoteChatSession.Data(this.sessionId, this.profilePublicKey.data());
   }

   public boolean hasExpired() {
      return this.profilePublicKey.data().hasExpired();
   }

   public static record Data(UUID sessionId, ProfilePublicKey.Data profilePublicKey) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, RemoteChatSession.Data::sessionId, ProfilePublicKey.Data.STREAM_CODEC, RemoteChatSession.Data::profilePublicKey, RemoteChatSession.Data::new);

      public RemoteChatSession validate(final GameProfile profile, final SignatureValidator serviceSignatureValidator) throws ProfilePublicKey.ValidationException {
         return new RemoteChatSession(this.sessionId, ProfilePublicKey.createValidated(serviceSignatureValidator, profile.id(), this.profilePublicKey));
      }
   }
}
