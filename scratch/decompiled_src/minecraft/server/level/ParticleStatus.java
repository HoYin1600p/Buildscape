package net.minecraft.server.level;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

public enum ParticleStatus {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   private static final IntFunction BY_ID = ByIdMap.continuous((s) -> s.id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec LEGACY_CODEC = Codec.INT.xmap(BY_ID::apply, (s) -> s.id);
   private final int id;
   private final Component caption;

   private ParticleStatus(final int id, final String key) {
      this.id = id;
      this.caption = Component.translatable(key);
   }

   public Component caption() {
      return this.caption;
   }

   // $FF: synthetic method
   private static ParticleStatus[] $values() {
      return new ParticleStatus[]{ALL, DECREASED, MINIMAL};
   }
}
