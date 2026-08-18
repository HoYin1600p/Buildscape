package net.minecraft.advancements;

import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

public class CriterionProgress {
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.INSTANT.apply(ByteBufCodecs::optional).map((obtained) -> new CriterionProgress((Instant)obtained.orElse((Object)null)), (progress) -> Optional.ofNullable(progress.obtained));
   private @Nullable Instant obtained;

   public CriterionProgress() {
   }

   public CriterionProgress(final @Nullable Instant obtained) {
      this.obtained = obtained;
   }

   public boolean isDone() {
      return this.obtained != null;
   }

   public void grant() {
      this.obtained = Instant.now();
   }

   public void revoke() {
      this.obtained = null;
   }

   public @Nullable Instant getObtained() {
      return this.obtained;
   }

   public String toString() {
      return "CriterionProgress{obtained=" + String.valueOf(this.obtained == null ? "false" : this.obtained) + "}";
   }
}
