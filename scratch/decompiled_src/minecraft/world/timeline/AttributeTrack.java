package net.minecraft.world.timeline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.WorldClock;

public record AttributeTrack(AttributeModifier modifier, KeyframeTrack argumentTrack) {
   public static Codec createCodec(final EnvironmentAttribute attribute) {
      MapCodec modifierCodec = attribute.type().modifierCodec().optionalFieldOf("modifier", AttributeModifier.override());
      return modifierCodec.dispatch(AttributeTrack::modifier, Util.memoize((Function)((modifier) -> createCodecWithModifier(attribute, modifier))));
   }

   private static MapCodec createCodecWithModifier(final EnvironmentAttribute attribute, final AttributeModifier modifier) {
      return KeyframeTrack.mapCodec(modifier.argumentCodec(attribute)).xmap((track) -> new AttributeTrack(modifier, track), AttributeTrack::argumentTrack);
   }

   public AttributeTrackSampler bakeSampler(final EnvironmentAttribute attribute, final Holder clock, final Optional periodTicks, final ClockManager clockManager) {
      return new AttributeTrackSampler(clock, periodTicks, this.modifier, this.argumentTrack, this.modifier.argumentKeyframeLerp(attribute), clockManager);
   }

   public static DataResult validatePeriod(final AttributeTrack track, final int periodTicks) {
      return KeyframeTrack.validatePeriod(track.argumentTrack(), periodTicks).map((ignored) -> track);
   }
}
