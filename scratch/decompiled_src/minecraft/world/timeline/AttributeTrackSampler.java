package net.minecraft.world.timeline;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.util.KeyframeTrackSampler;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.WorldClock;
import org.jspecify.annotations.Nullable;

public class AttributeTrackSampler implements EnvironmentAttributeLayer.TimeBased {
   private final Holder clock;
   private final AttributeModifier modifier;
   private final KeyframeTrackSampler argumentSampler;
   private final ClockManager clockManager;
   private int cachedTickId;
   private @Nullable Object cachedArgument;

   public AttributeTrackSampler(final Holder clock, final Optional periodTicks, final AttributeModifier modifier, final KeyframeTrack argumentTrack, final LerpFunction argumentLerp, final ClockManager clockManager) {
      this.clock = clock;
      this.modifier = modifier;
      this.clockManager = clockManager;
      this.argumentSampler = argumentTrack.bakeSampler(periodTicks, argumentLerp);
   }

   public Object applyTimeBased(final Object baseValue, final int cacheTickId) {
      if (this.cachedArgument == null || cacheTickId != this.cachedTickId) {
         this.cachedTickId = cacheTickId;
         this.cachedArgument = this.argumentSampler.sample(this.clockManager.getInstance(this.clock).totalTicks());
      }

      return this.modifier.apply(baseValue, this.cachedArgument);
   }
}
