package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;

public record LocationPredicate(Optional position, Optional biomes, Optional structures, Optional dimension, Optional smokey, Optional light, Optional block, Optional fluid, Optional canSeeSky) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LocationPredicate.PositionPredicate.CODEC.optionalFieldOf("position").forGetter(LocationPredicate::position), RegistryCodecs.holderSet(Registries.BIOME).optionalFieldOf("biomes").forGetter(LocationPredicate::biomes), RegistryCodecs.holderSet(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(LocationPredicate::structures), ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension").forGetter(LocationPredicate::dimension), Codec.BOOL.optionalFieldOf("smokey").forGetter(LocationPredicate::smokey), LightPredicate.CODEC.optionalFieldOf("light").forGetter(LocationPredicate::light), BlockPredicate.CODEC.optionalFieldOf("block").forGetter(LocationPredicate::block), FluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(LocationPredicate::fluid), Codec.BOOL.optionalFieldOf("can_see_sky").forGetter(LocationPredicate::canSeeSky)).apply(i, LocationPredicate::new));

   public boolean matches(final ServerLevel level, final double x, final double y, final double z) {
      if (this.position.isPresent() && !((LocationPredicate.PositionPredicate)this.position.get()).matches(x, y, z)) {
         return false;
      } else if (this.dimension.isPresent() && this.dimension.get() != level.dimension()) {
         return false;
      } else {
         BlockPos pos = BlockPos.containing(x, y, z);
         boolean loaded = level.isLoaded(pos);
         if (!this.biomes.isPresent() || loaded && ((HolderSet)this.biomes.get()).contains(level.getBiome(pos))) {
            if (!this.structures.isPresent() || loaded && level.structureManager().getStructureWithPieceAt(pos, (HolderSet)this.structures.get()).isValid()) {
               if (!this.smokey.isPresent() || loaded && this.smokey.get() == CampfireBlock.isSmokeyPos(level, pos)) {
                  if (this.light.isPresent() && !((LightPredicate)this.light.get()).matches(level, pos)) {
                     return false;
                  } else if (this.block.isPresent() && !((BlockPredicate)this.block.get()).matches(level, pos)) {
                     return false;
                  } else if (this.fluid.isPresent() && !((FluidPredicate)this.fluid.get()).matches(level, pos)) {
                     return false;
                  } else {
                     return !this.canSeeSky.isPresent() || this.canSeeSky.get() == level.canSeeSky(pos);
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles x = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles y = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles z = MinMaxBounds.Doubles.ANY;
      private Optional biomes = Optional.empty();
      private Optional structures = Optional.empty();
      private Optional dimension = Optional.empty();
      private Optional smokey = Optional.empty();
      private Optional light = Optional.empty();
      private Optional block = Optional.empty();
      private Optional fluid = Optional.empty();
      private Optional canSeeSky = Optional.empty();

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
      }

      public static LocationPredicate.Builder inBiome(final Holder biome) {
         return location().setBiomes(HolderSet.direct(biome));
      }

      public static LocationPredicate.Builder inDimension(final ResourceKey dimension) {
         return location().setDimension(dimension);
      }

      public static LocationPredicate.Builder inStructure(final Holder structure) {
         return location().setStructures(HolderSet.direct(structure));
      }

      public static LocationPredicate.Builder atYLocation(final MinMaxBounds.Doubles yLocation) {
         return location().setY(yLocation);
      }

      public LocationPredicate.Builder setX(final MinMaxBounds.Doubles x) {
         this.x = x;
         return this;
      }

      public LocationPredicate.Builder setY(final MinMaxBounds.Doubles y) {
         this.y = y;
         return this;
      }

      public LocationPredicate.Builder setZ(final MinMaxBounds.Doubles z) {
         this.z = z;
         return this;
      }

      public LocationPredicate.Builder setBiomes(final HolderSet biomes) {
         this.biomes = Optional.of(biomes);
         return this;
      }

      public LocationPredicate.Builder setStructures(final HolderSet structures) {
         this.structures = Optional.of(structures);
         return this;
      }

      public LocationPredicate.Builder setDimension(final ResourceKey dimension) {
         this.dimension = Optional.of(dimension);
         return this;
      }

      public LocationPredicate.Builder setLight(final LightPredicate.Builder light) {
         this.light = Optional.of(light.build());
         return this;
      }

      public LocationPredicate.Builder setBlock(final BlockPredicate.Builder block) {
         this.block = Optional.of(block.build());
         return this;
      }

      public LocationPredicate.Builder setFluid(final FluidPredicate.Builder fluid) {
         this.fluid = Optional.of(fluid.build());
         return this;
      }

      public LocationPredicate.Builder setSmokey(final boolean smokey) {
         this.smokey = Optional.of(smokey);
         return this;
      }

      public LocationPredicate.Builder setCanSeeSky(final boolean canSeeSky) {
         this.canSeeSky = Optional.of(canSeeSky);
         return this;
      }

      public LocationPredicate build() {
         Optional position = LocationPredicate.PositionPredicate.of(this.x, this.y, this.z);
         return new LocationPredicate(position, this.biomes, this.structures, this.dimension, this.smokey, this.light, this.block, this.fluid, this.canSeeSky);
      }
   }

   private static record PositionPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::x), MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::y), MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::z)).apply(i, LocationPredicate.PositionPredicate::new));

      private static Optional of(final MinMaxBounds.Doubles x, final MinMaxBounds.Doubles y, final MinMaxBounds.Doubles z) {
         return x.isAny() && y.isAny() && z.isAny() ? Optional.empty() : Optional.of(new LocationPredicate.PositionPredicate(x, y, z));
      }

      public boolean matches(final double x, final double y, final double z) {
         return this.x.matches(x) && this.y.matches(y) && this.z.matches(z);
      }
   }
}
