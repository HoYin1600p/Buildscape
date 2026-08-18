package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record RandomPoolAlias(ResourceKey alias, WeightedList targets) implements PoolAliasBinding {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(RandomPoolAlias::alias), WeightedList.nonEmptyCodec(ResourceKey.codec(Registries.TEMPLATE_POOL)).fieldOf("targets").forGetter(RandomPoolAlias::targets)).apply(i, RandomPoolAlias::new));

   public void forEachResolved(final RandomSource random, final BiConsumer aliasAndTargetConsumer) {
      aliasAndTargetConsumer.accept(this.alias, (ResourceKey)this.targets.getRandomOrThrow(random));
   }

   public Stream allTargets() {
      return this.targets.unwrap().stream().map(Weighted::value);
   }

   public MapCodec codec() {
      return CODEC;
   }
}
