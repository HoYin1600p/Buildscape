package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public class RandomizedIntStateProvider implements BlockStateProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockStateProvider.CODEC.fieldOf("source").forGetter((c) -> c.source), Codec.STRING.fieldOf("property").forGetter((c) -> c.propertyName), IntProviders.CODEC.fieldOf("values").forGetter((c) -> c.values)).apply(i, RandomizedIntStateProvider::new));
   private final BlockStateProvider source;
   private final String propertyName;
   private @Nullable IntegerProperty property;
   private final IntProvider values;

   public RandomizedIntStateProvider(final BlockStateProvider source, final IntegerProperty property, final IntProvider values) {
      this.source = source;
      this.property = property;
      this.propertyName = property.getName();
      this.values = values;
      Collection possibleValues = property.getPossibleValues();

      for(int i = values.minInclusive(); i <= values.maxInclusive(); ++i) {
         if (!possibleValues.contains(i)) {
            throw new IllegalArgumentException("Property value out of range: " + property.getName() + ": " + i);
         }
      }

   }

   public RandomizedIntStateProvider(final BlockStateProvider source, final String propertyName, final IntProvider values) {
      this.source = source;
      this.propertyName = propertyName;
      this.values = values;
   }

   public MapCodec codec() {
      return CODEC;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      BlockState unmodifiedState = this.source.getState(level, random, pos);
      if (this.property == null || !unmodifiedState.hasProperty(this.property)) {
         IntegerProperty property = findProperty(unmodifiedState, this.propertyName);
         if (property == null) {
            return unmodifiedState;
         }

         this.property = property;
      }

      return (BlockState)unmodifiedState.setValue(this.property, Integer.valueOf(this.values.sample(random)));
   }

   private static @Nullable IntegerProperty findProperty(final BlockState source, final String propertyName) {
      Collection properties = source.getProperties();
      Optional found = properties.stream().filter((p) -> p.getName().equals(propertyName)).filter((p) -> p instanceof IntegerProperty).map((p) -> (IntegerProperty)p).findAny();
      return (IntegerProperty)found.orElse((Object)null);
   }
}
