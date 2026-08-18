package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record FluidPredicate(Optional fluids, Optional properties) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.FLUID).optionalFieldOf("fluids").forGetter(FluidPredicate::fluids), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FluidPredicate::properties)).apply(i, FluidPredicate::new));

   public boolean matches(final ServerLevel level, final BlockPos pos) {
      if (!level.isLoaded(pos)) {
         return false;
      } else {
         FluidState state = level.getFluidState(pos);
         if (this.fluids.isPresent() && !state.is((HolderSet)this.fluids.get())) {
            return false;
         } else {
            return !this.properties.isPresent() || ((StatePropertiesPredicate)this.properties.get()).matches(state);
         }
      }
   }

   public static class Builder {
      private Optional fluids = Optional.empty();
      private Optional properties = Optional.empty();

      private Builder() {
      }

      public static FluidPredicate.Builder fluid() {
         return new FluidPredicate.Builder();
      }

      public FluidPredicate.Builder of(final Fluid fluid) {
         this.fluids = Optional.of(HolderSet.direct(fluid.builtInRegistryHolder()));
         return this;
      }

      public FluidPredicate.Builder of(final HolderSet fluids) {
         this.fluids = Optional.of(fluids);
         return this;
      }

      public FluidPredicate.Builder setProperties(final StatePropertiesPredicate properties) {
         this.properties = Optional.of(properties);
         return this;
      }

      public FluidPredicate build() {
         return new FluidPredicate(this.fluids, this.properties);
      }
   }
}
