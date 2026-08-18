package net.minecraft.world.attribute;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttributeProbe {
   private final Map valueProbes = new Reference2ObjectOpenHashMap();
   private final Function valueProbeFactory = (x$0) -> new EnvironmentAttributeProbe.ValueProbe(x$0);
   private @Nullable Level level;
   private @Nullable Vec3 position;
   private final SpatialAttributeInterpolator biomeInterpolator = new SpatialAttributeInterpolator();

   public void reset() {
      this.level = null;
      this.position = null;
      this.biomeInterpolator.clear();
      this.valueProbes.clear();
   }

   public void tick(final Level level, final Vec3 position) {
      this.level = level;
      this.position = position;
      this.valueProbes.values().removeIf(EnvironmentAttributeProbe.ValueProbe::tick);
      this.biomeInterpolator.clear();
      GaussianSampler.sample(position.scale(0.25D), level.getBiomeManager()::getNoiseBiomeAtQuart, (weight, biome) -> this.biomeInterpolator.accumulate(weight, ((Biome)biome.value()).getAttributes()));
   }

   public Object getValue(final EnvironmentAttribute attribute, final float partialTicks) {
      EnvironmentAttributeProbe.ValueProbe valueProbe = (EnvironmentAttributeProbe.ValueProbe)this.valueProbes.computeIfAbsent(attribute, this.valueProbeFactory);
      return valueProbe.get(attribute, partialTicks);
   }

   private class ValueProbe {
      private Object lastValue;
      private @Nullable Object newValue;

      public ValueProbe(final EnvironmentAttribute attribute) {
         Objects.requireNonNull(EnvironmentAttributeProbe.this);
         super();
         Object value = (Value)this.getValueFromLevel(attribute);
         this.lastValue = value;
         this.newValue = value;
      }

      private Object getValueFromLevel(final EnvironmentAttribute attribute) {
         return EnvironmentAttributeProbe.this.level != null && EnvironmentAttributeProbe.this.position != null ? EnvironmentAttributeProbe.this.level.environmentAttributes().getValue(attribute, EnvironmentAttributeProbe.this.position, EnvironmentAttributeProbe.this.biomeInterpolator) : attribute.defaultValue();
      }

      public boolean tick() {
         if (this.newValue == null) {
            return true;
         } else {
            this.lastValue = this.newValue;
            this.newValue = null;
            return false;
         }
      }

      public Object get(final EnvironmentAttribute attribute, final float partialTicks) {
         if (this.newValue == null) {
            this.newValue = this.getValueFromLevel(attribute);
         }

         return attribute.type().partialTickLerp().apply(partialTicks, this.lastValue, this.newValue);
      }
   }
}
