package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record DirectPoolAlias(ResourceKey alias, ResourceKey target) implements PoolAliasBinding {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(DirectPoolAlias::alias), ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("target").forGetter(DirectPoolAlias::target)).apply(i, DirectPoolAlias::new));

   public void forEachResolved(final RandomSource random, final BiConsumer aliasAndTargetConsumer) {
      aliasAndTargetConsumer.accept(this.alias, this.target);
   }

   public Stream allTargets() {
      return Stream.of(this.target);
   }

   public MapCodec codec() {
      return CODEC;
   }
}
