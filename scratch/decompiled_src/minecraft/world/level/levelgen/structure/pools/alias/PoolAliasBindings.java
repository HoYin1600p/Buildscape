package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class PoolAliasBindings {
   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "random", RandomPoolAlias.CODEC);
      Registry.register(registry, "random_group", RandomGroupPoolAlias.CODEC);
      return (MapCodec)Registry.register(registry, "direct", DirectPoolAlias.CODEC);
   }

   public static void registerTargetsAsPools(final BootstrapContext context, final Holder emptyPool, final List aliasBindings) {
      aliasBindings.stream().flatMap(PoolAliasBinding::allTargets).map((key) -> key.identifier().getPath()).forEach((path) -> Pools.register(context, path, new StructureTemplatePool(emptyPool, List.of(Pair.of(StructurePoolElement.single(path), 1)), StructureTemplatePool.Projection.RIGID)));
   }
}
